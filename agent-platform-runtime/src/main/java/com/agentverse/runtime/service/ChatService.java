package com.agentverse.runtime.service;

import com.agentverse.common.dto.*;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.runtime.chat.Message;
import com.agentverse.runtime.chat.SessionStore;
import com.agentverse.runtime.engine.HarnessAgentEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final SessionStore sessionStore;
    private final HarnessAgentEngine harnessAgentEngine;
    private final AgentDefinitionService agentDefinitionService;

    /**
     * 创建会话
     */
    public SessionResponse createSession(SessionCreateRequest request) {
        log.info("Creating session for agent: {}", request.getAgentId());

        // 验证 Agent 存在
        agentDefinitionService.getAgentById(request.getAgentId());

        String sessionId = sessionStore.createSession(request.getAgentId());
        LocalDateTime createdAt = sessionStore.getCreatedAt(sessionId);

        SessionResponse response = new SessionResponse();
        response.setSessionId(sessionId);
        response.setAgentId(request.getAgentId());
        response.setCreatedAt(createdAt);
        return response;
    }

    /**
     * 发送消息（同步）
     */
    public MessageResponse sendMessage(String sessionId, MessageRequest request) {
        log.info("Sending message to session: {}", sessionId);

        if (!sessionStore.exists(sessionId)) {
            throw new BizException(ErrorCode.SESSION_NOT_FOUND);
        }

        // 添加用户消息
        Message userMessage = new Message("user", request.getContent());
        sessionStore.addMessage(sessionId, userMessage);

        // 获取 Agent ID 并执行对话
        String agentId = sessionStore.getAgentId(sessionId);
        String agentResponse = harnessAgentEngine.executeChat(agentId, sessionId, request.getContent());

        // 添加 Agent 响应
        Message assistantMessage = new Message("assistant", agentResponse);
        sessionStore.addMessage(sessionId, assistantMessage);

        return convertToResponse(assistantMessage);
    }

    /**
     * 流式对话（使用 SseEmitter）
     */
    public void streamMessage(String sessionId, MessageRequest request, SseEmitter emitter) {
        log.info("Streaming message to session: {}", sessionId);

        if (!sessionStore.exists(sessionId)) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Session not found"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return;
        }

        // 添加用户消息
        Message userMessage = new Message("user", request.getContent());
        sessionStore.addMessage(sessionId, userMessage);

        String agentId = sessionStore.getAgentId(sessionId);

        StringBuilder fullResponse = new StringBuilder();

        harnessAgentEngine.streamChat(agentId, sessionId, request.getContent())
                .doOnNext(content -> {
                    try {
                        emitter.send(SseEmitter.event().data(content));
                        fullResponse.append(content);
                    } catch (Exception e) {
                        log.warn("Failed to send SSE event", e);
                    }
                })
                .doOnComplete(() -> {
                    // 流式完成后，将完整响应添加到会话
                    Message assistantMessage = new Message("assistant", fullResponse.toString());
                    sessionStore.addMessage(sessionId, assistantMessage);
                    try {
                        emitter.complete();
                    } catch (Exception e) {
                        log.warn("Failed to complete SSE emitter", e);
                    }
                    log.info("Stream completed: sessionId={}", sessionId);
                })
                .doOnError(e -> {
                    log.error("Stream error: sessionId={}", sessionId, e);
                    try {
                        emitter.completeWithError(e);
                    } catch (Exception ex) {
                        log.warn("Failed to complete SSE emitter with error", ex);
                    }
                })
                .subscribe();
    }

    /**
     * 获取会话历史消息
     */
    public List<MessageResponse> getSessionHistory(String sessionId) {
        log.info("Getting session history: {}", sessionId);

        if (!sessionStore.exists(sessionId)) {
            throw new BizException(ErrorCode.SESSION_NOT_FOUND);
        }

        List<Message> messages = sessionStore.getSession(sessionId);
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 删除会话
     */
    public void deleteSession(String sessionId) {
        log.info("Deleting session: {}", sessionId);

        if (!sessionStore.exists(sessionId)) {
            throw new BizException(ErrorCode.SESSION_NOT_FOUND);
        }

        sessionStore.deleteSession(sessionId);
    }

    /**
     * 中断对话
     */
    public boolean interruptSession(String sessionId) {
        log.info("Interrupting session: {}", sessionId);
        return harnessAgentEngine.interruptChat(sessionId);
    }

    private MessageResponse convertToResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setRole(message.getRole());
        response.setContent(message.getContent());
        response.setTimestamp(message.getTimestamp());
        return response;
    }
}
