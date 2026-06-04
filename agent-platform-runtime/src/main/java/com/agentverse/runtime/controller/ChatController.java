package com.agentverse.runtime.controller;

import com.agentverse.common.dto.*;
import com.agentverse.runtime.security.RequirePermission;
import com.agentverse.runtime.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 对话控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 创建会话
     */
    @RequirePermission("chat:create")
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
            @Valid @RequestBody SessionCreateRequest request) {
        log.info("Received create session request for agent: {}", request.getAgentId());
        SessionResponse session = chatService.createSession(request);
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    /**
     * 发送消息（同步）
     */
    @RequirePermission("chat:create")
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable("sessionId") String sessionId,
            @Valid @RequestBody MessageRequest request) {
        log.info("Received send message request for session: {}", sessionId);
        MessageResponse response = chatService.sendMessage(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 流式对话（SSE）
     */
    @RequirePermission("chat:create")
    @GetMapping(value = "/sessions/{sessionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessage(
            @PathVariable("sessionId") String sessionId,
            @RequestParam("content") String content) {
        log.info("Received stream message request for session: {}", sessionId);
        SseEmitter emitter = new SseEmitter(30000L); // 30秒超时
        
        emitter.onCompletion(() -> log.info("SSE completed"));
        emitter.onTimeout(() -> log.warn("SSE timeout"));
        emitter.onError(e -> log.error("SSE error", e));
        
        MessageRequest request = new MessageRequest();
        request.setContent(content);
        
        chatService.streamMessage(sessionId, request, emitter);
        
        return emitter;
    }

    /**
     * 查询会话历史消息
     */
    @RequirePermission("chat:read")
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getSessionHistory(
            @PathVariable("sessionId") String sessionId) {
        log.info("Received get session history request: {}", sessionId);
        List<MessageResponse> messages = chatService.getSessionHistory(sessionId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    /**
     * 删除会话
     */
    @RequirePermission("chat:create")
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable("sessionId") String sessionId) {
        log.info("Received delete session request: {}", sessionId);
        chatService.deleteSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 中断对话
     */
    @RequirePermission("chat:create")
    @PostMapping("/sessions/{sessionId}/interrupt")
    public ResponseEntity<ApiResponse<Boolean>> interruptSession(@PathVariable("sessionId") String sessionId) {
        log.info("Received interrupt session request: {}", sessionId);
        boolean result = chatService.interruptSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
