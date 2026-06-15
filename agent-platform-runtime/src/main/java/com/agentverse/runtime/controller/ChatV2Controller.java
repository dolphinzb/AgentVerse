/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.dto.MessageRequest;
import com.agentverse.common.dto.MessageResponse;
import com.agentverse.common.dto.SessionCreateRequest;
import com.agentverse.common.dto.SessionResponse;
import com.agentverse.runtime.security.RequirePermission;
import com.agentverse.runtime.service.ChatService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(value={"/v2/chat"})
public class ChatV2Controller {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ChatV2Controller.class);
    private final ChatService chatService;

    @RequirePermission(value="chat:read")
    @GetMapping(value={"/sessions"})
    public ResponseEntity<ApiResponse<List<SessionResponse>>> listSessions() {
        log.info("[v2] Received list sessions request");
        List<SessionResponse> sessions = this.chatService.listSessions();
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @RequirePermission(value="chat:create")
    @PostMapping(value={"/sessions"})
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(@Valid @RequestBody SessionCreateRequest request) {
        log.info("[v2] Received create session request for agent: {}", (Object)request.getAgentId());
        SessionResponse session = this.chatService.createSession(request);
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @RequirePermission(value="chat:create")
    @PostMapping(value={"/sessions/{sessionId}/messages"})
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(@PathVariable(value="sessionId") String sessionId, @Valid @RequestBody MessageRequest request) {
        log.info("[v2] Received send message request for session: {}", (Object)sessionId);
        MessageResponse response = this.chatService.sendMessage(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RequirePermission(value="chat:create")
    @GetMapping(value={"/sessions/{sessionId}/stream"}, produces={"text/event-stream"})
    public SseEmitter streamMessage(@PathVariable(value="sessionId") String sessionId, @RequestParam(value="content") String content) {
        log.info("[v2] Received stream message request for session: {}", (Object)sessionId);
        SseEmitter emitter = new SseEmitter(Long.valueOf(30000L));
        emitter.onCompletion(() -> log.info("[v2] SSE completed"));
        emitter.onTimeout(() -> log.warn("[v2] SSE timeout"));
        emitter.onError(e -> log.error("[v2] SSE error", e));
        MessageRequest request = new MessageRequest();
        request.setContent(content);
        this.chatService.streamMessage(sessionId, request, emitter);
        return emitter;
    }

    @RequirePermission(value="chat:read")
    @GetMapping(value={"/sessions/{sessionId}/messages"})
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getSessionHistory(@PathVariable(value="sessionId") String sessionId) {
        log.info("[v2] Received get session history request: {}", (Object)sessionId);
        List<MessageResponse> messages = this.chatService.getSessionHistory(sessionId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @RequirePermission(value="chat:create")
    @DeleteMapping(value={"/sessions/{sessionId}"})
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable(value="sessionId") String sessionId) {
        log.info("[v2] Received delete session request: {}", (Object)sessionId);
        this.chatService.deleteSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @RequirePermission(value="chat:create")
    @PostMapping(value={"/sessions/{sessionId}/interrupt"})
    public ResponseEntity<ApiResponse<Boolean>> interruptSession(@PathVariable(value="sessionId") String sessionId) {
        log.info("[v2] Received interrupt session request: {}", (Object)sessionId);
        boolean result = this.chatService.interruptSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Generated
    public ChatV2Controller(ChatService chatService) {
        this.chatService = chatService;
    }
}

