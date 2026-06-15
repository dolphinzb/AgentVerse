/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.service;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.agentverse.common.dto.MessageRequest;
import com.agentverse.common.dto.MessageResponse;
import com.agentverse.common.dto.SessionCreateRequest;
import com.agentverse.common.dto.SessionResponse;
import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.chat.SessionIndex;
import com.agentverse.runtime.chat.SessionMeta;
import com.agentverse.runtime.engine.HarnessAgentEngine;
import com.agentverse.runtime.engine.MessageProjector;
import com.agentverse.runtime.mapper.AgentDefinitionMapper;
import com.agentverse.runtime.model.service.ChatUsageService;

import io.agentscope.core.message.Msg;
import io.agentscope.core.model.ChatUsage;
import lombok.Generated;

@Service
public class ChatService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final SessionIndex sessionIndex;
    private final HarnessAgentEngine harnessAgentEngine;
    private final AgentDefinitionService agentDefinitionService;
    private final AgentDefinitionMapper agentDefinitionMapper;
    private final MessageProjector messageProjector;
    private final ChatUsageService chatUsageService;

    public SessionResponse createSession(SessionCreateRequest request) {
        log.info("Creating session for agent: {}", (Object) request.getAgentId());
        AgentDefinition agentDef = (AgentDefinition) this.agentDefinitionMapper
                .selectById((Serializable) ((Object) request.getAgentId()));
        if (agentDef == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        if ("draft".equals(agentDef.getStatus())) {
            throw new BizException(ErrorCode.AGENT_NOT_PUBLISHED);
        }
        if ("archived".equals(agentDef.getStatus())) {
            throw new BizException(ErrorCode.AGENT_ARCHIVED);
        }
        String userId = this.currentUserId();
        log.info("[v2] createSession called, currentUserId={}, agentId={}", userId, request.getAgentId());
        String sessionId = this.sessionIndex.create(request.getAgentId(), userId);
        log.info("[v2] createSession stored sessionId={} with userId={}", sessionId, userId);
        SessionMeta meta = this.sessionIndex.get(sessionId)
                .orElseThrow(() -> new BizException(ErrorCode.SESSION_NOT_FOUND));
        SessionResponse response = new SessionResponse();
        response.setSessionId(sessionId);
        response.setAgentId(request.getAgentId());
        response.setAgentName(agentDef.getName());
        response.setCreatedAt(
                meta.createdAt() != null ? meta.createdAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
        return response;
    }

    public List<SessionResponse> listSessions() {
        String userId = this.currentUserId();
        log.info("[v2] listSessions called, currentUserId={}", userId);
        List<SessionMeta> metas = this.sessionIndex.listByAgent(null, userId);
        log.info("[v2] listSessions result: userId={}, returned={} sessions", userId, metas.size());
        return metas.stream().map(meta -> {
            SessionResponse response = new SessionResponse();
            response.setSessionId(meta.sessionId());
            response.setAgentId(meta.agentId());
            response.setCreatedAt(
                    meta.createdAt() != null ? meta.createdAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                            : null);
            try {
                AgentDefinition agentDef = (AgentDefinition) this.agentDefinitionMapper
                        .selectById((Serializable) ((Object) meta.agentId()));
                if (agentDef != null) {
                    response.setAgentName(agentDef.getName());
                }
            } catch (Exception e) {
                log.warn("Failed to get agent name for session: {}", (Object) meta.sessionId());
            }
            return response;
        }).collect(Collectors.toList());
    }

    public MessageResponse sendMessage(String sessionId, MessageRequest request) {
        log.info("Sending message to session: {}", (Object) sessionId);
        SessionMeta meta = this.sessionIndex.get(sessionId)
                .orElseThrow(() -> new BizException(ErrorCode.SESSION_NOT_FOUND));
        if (meta.status() == SessionMeta.SessionStatus.STOPPED) {
            throw new BizException(ErrorCode.SESSION_STOPPED);
        }
        Msg assistantMsg = this.harnessAgentEngine.executeChat(meta.agentId(), sessionId, request.getContent());
        this.recordTokenUsage(sessionId, meta.agentId(), assistantMsg);
        if (assistantMsg == null) {
            throw new BizException(ErrorCode.INTERNAL_ERROR);
        }
        return this.messageProjector.project(assistantMsg);
    }

    public void streamMessage(String sessionId, MessageRequest request, SseEmitter emitter) {
        log.info("Streaming message to session: {}", (Object) sessionId);
        SessionMeta meta = this.sessionIndex.get(sessionId).orElse(null);
        if (meta == null) {
            try {
                emitter.send(SseEmitter.event().name("error").data((Object) "Session not found"));
                emitter.complete();
            } catch (Exception e2) {
                emitter.completeWithError((Throwable) e2);
            }
            return;
        }
        this.harnessAgentEngine.streamChat(meta.agentId(), sessionId, request.getContent()).doOnNext(content -> {
            try {
                emitter.send(SseEmitter.event().data(content));
            } catch (Exception e) {
                log.warn("Failed to send SSE event", (Throwable) e);
            }
        }).doOnComplete(() -> {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("Failed to complete SSE emitter", (Throwable) e);
            }
            log.info("Stream completed: sessionId={}", (Object) sessionId);
        }).doOnError(e -> {
            log.error("Stream error: sessionId={}", (Object) sessionId, e);
            try {
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.warn("Failed to complete SSE emitter with error", (Throwable) ex);
            }
        }).subscribe();
    }

    public List<MessageResponse> getSessionHistory(String sessionId) {
        log.info("Getting session history: {}", (Object) sessionId);
        SessionMeta meta = this.sessionIndex.get(sessionId)
                .orElseThrow(() -> new BizException(ErrorCode.SESSION_NOT_FOUND));
        List<Msg> msgs = this.harnessAgentEngine.getMemory(meta.agentId(), sessionId);
        return this.messageProjector.projectAll(msgs);
    }

    public void deleteSession(String sessionId) {
        log.info("Deleting session: {}", (Object) sessionId);
        if (!this.sessionIndex.exists(sessionId)) {
            throw new BizException(ErrorCode.SESSION_NOT_FOUND);
        }
        this.sessionIndex.delete(sessionId);
    }

    public boolean interruptSession(String sessionId) {
        log.info("Interrupting session: {}", (Object) sessionId);
        boolean ok = this.harnessAgentEngine.interruptChat(sessionId);
        if (ok) {
            this.sessionIndex.updateStatus(sessionId, SessionMeta.SessionStatus.STOPPED);
        }
        return ok;
    }

    public void recordTokenUsage(String sessionId, String agentId, Msg assistantMsg) {
        try {
            String modelConfigId;
            if (assistantMsg == null) {
                return;
            }
            ChatUsage u = assistantMsg.getChatUsage();
            if (u == null) {
                log.debug("No ChatUsage on assistant Msg for session={}, skip saveUsage", (Object) sessionId);
                return;
            }
            AgentDefinition agentDef = (AgentDefinition) this.agentDefinitionMapper
                    .selectById((Serializable) ((Object) agentId));
            String string = modelConfigId = agentDef != null ? agentDef.getModelConfigId() : null;
            if (modelConfigId == null || modelConfigId.isEmpty()) {
                log.warn(
                        "\u672a\u627e\u5230 Agent \u5bf9\u5e94\u7684\u6a21\u578b\u914d\u7f6e,\u8df3\u8fc7\u7528\u91cf\u8bb0\u5f55: agentId={}",
                        (Object) agentId);
                return;
            }
            this.chatUsageService.saveUsage(sessionId, modelConfigId, Long.valueOf(u.getInputTokens()),
                    Long.valueOf(u.getOutputTokens()));
        } catch (Exception e) {
            log.error("\u8bb0\u5f55 Token \u7528\u91cf\u5931\u8d25: sessionId={}, agentId={}",
                    new Object[] { sessionId, agentId, e });
        }
    }

    private String currentUserId() {
        Long uid = UserContext.getUserId();
        return uid == null ? null : String.valueOf(uid);
    }

    @Generated
    public ChatService(SessionIndex sessionIndex, HarnessAgentEngine harnessAgentEngine,
            AgentDefinitionService agentDefinitionService, AgentDefinitionMapper agentDefinitionMapper,
            MessageProjector messageProjector, ChatUsageService chatUsageService) {
        this.sessionIndex = sessionIndex;
        this.harnessAgentEngine = harnessAgentEngine;
        this.agentDefinitionService = agentDefinitionService;
        this.agentDefinitionMapper = agentDefinitionMapper;
        this.messageProjector = messageProjector;
        this.chatUsageService = chatUsageService;
    }
}
