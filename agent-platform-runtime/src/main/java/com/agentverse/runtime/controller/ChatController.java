/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.dto.MessageRequest;
import com.agentverse.common.dto.MessageResponse;
import com.agentverse.common.dto.SessionCreateRequest;
import com.agentverse.common.dto.SessionResponse;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequestMapping(value={"/v1/chat"})
public class ChatController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private ResponseEntity<ApiResponse<Void>> gone() {
        ApiResponse body = ApiResponse.error(HttpStatus.GONE.value(), "v1 \u7aef\u70b9\u5df2\u5e9f\u5f03,\u8bf7\u8fc1\u79fb\u5230 /v2/chat (blocks-only MessageResponse)");
        return ((ResponseEntity.BodyBuilder)((ResponseEntity.BodyBuilder)ResponseEntity.status((HttpStatusCode)HttpStatus.GONE).header("Deprecation", new String[]{"true"})).header("Link", new String[]{"</v2/chat>; rel=\"successor-version\""})).body(body);
    }

    @GetMapping(value={"/sessions"})
    public ResponseEntity<ApiResponse<List<SessionResponse>>> listSessions() {
        log.info("[v1] DEPRECATED call: GET /v1/chat/sessions \u2014 return 410");
        return ((ResponseEntity.BodyBuilder)((ResponseEntity.BodyBuilder)ResponseEntity.status((HttpStatusCode)HttpStatus.GONE).header("Deprecation", new String[]{"true"})).header("Link", new String[]{"</v2/chat/sessions>; rel=\"successor-version\""})).body(ApiResponse.error(410, "v1 \u5df2\u5e9f\u5f03,\u8bf7\u4f7f\u7528 /v2/chat/sessions"));
    }

    @PostMapping(value={"/sessions"})
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(@RequestBody(required=false) SessionCreateRequest request) {
        log.info("[v1] DEPRECATED call: POST /v1/chat/sessions \u2014 return 410");
        return ((ResponseEntity.BodyBuilder)((ResponseEntity.BodyBuilder)ResponseEntity.status((HttpStatusCode)HttpStatus.GONE).header("Deprecation", new String[]{"true"})).header("Link", new String[]{"</v2/chat/sessions>; rel=\"successor-version\""})).body(ApiResponse.error(410, "v1 \u5df2\u5e9f\u5f03,\u8bf7\u4f7f\u7528 /v2/chat/sessions"));
    }

    @PostMapping(value={"/sessions/{sessionId}/messages"})
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(@PathVariable(value="sessionId") String sessionId, @RequestBody(required=false) MessageRequest request) {
        log.info("[v1] DEPRECATED call: POST /v1/chat/sessions/{}/messages \u2014 return 410", (Object)sessionId);
        return ((ResponseEntity.BodyBuilder)((ResponseEntity.BodyBuilder)ResponseEntity.status((HttpStatusCode)HttpStatus.GONE).header("Deprecation", new String[]{"true"})).header("Link", new String[]{"</v2/chat/sessions/" + sessionId + "/messages>; rel=\"successor-version\""})).body(ApiResponse.error(410, "v1 \u5df2\u5e9f\u5f03,\u8bf7\u4f7f\u7528 /v2/chat/sessions/" + sessionId + "/messages"));
    }

    @GetMapping(value={"/sessions/{sessionId}/stream"}, produces={"text/event-stream"})
    public ResponseEntity<?> streamMessage(@PathVariable(value="sessionId") String sessionId, @RequestParam(value="content", required=false) String content) {
        log.info("[v1] DEPRECATED call: GET /v1/chat/sessions/{}/stream \u2014 return 410", (Object)sessionId);
        return ((ResponseEntity.BodyBuilder)((ResponseEntity.BodyBuilder)ResponseEntity.status((HttpStatusCode)HttpStatus.GONE).header("Deprecation", new String[]{"true"})).header("Link", new String[]{"</v2/chat/sessions/" + sessionId + "/stream>; rel=\"successor-version\""})).body((Object)("v1 stream \u5df2\u5e9f\u5f03,\u8bf7\u4f7f\u7528 /v2/chat/sessions/" + sessionId + "/stream"));
    }

    @GetMapping(value={"/sessions/{sessionId}/messages"})
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getSessionHistory(@PathVariable(value="sessionId") String sessionId) {
        log.info("[v1] DEPRECATED call: GET /v1/chat/sessions/{}/messages \u2014 return 410", (Object)sessionId);
        return ((ResponseEntity.BodyBuilder)((ResponseEntity.BodyBuilder)ResponseEntity.status((HttpStatusCode)HttpStatus.GONE).header("Deprecation", new String[]{"true"})).header("Link", new String[]{"</v2/chat/sessions/" + sessionId + "/messages>; rel=\"successor-version\""})).body(ApiResponse.error(410, "v1 \u5df2\u5e9f\u5f03,\u8bf7\u4f7f\u7528 /v2/chat/sessions/" + sessionId + "/messages"));
    }

    @DeleteMapping(value={"/sessions/{sessionId}"})
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable(value="sessionId") String sessionId) {
        log.info("[v1] DEPRECATED call: DELETE /v1/chat/sessions/{} \u2014 return 410", (Object)sessionId);
        return ((ResponseEntity.BodyBuilder)((ResponseEntity.BodyBuilder)ResponseEntity.status((HttpStatusCode)HttpStatus.GONE).header("Deprecation", new String[]{"true"})).header("Link", new String[]{"</v2/chat/sessions/" + sessionId + ">; rel=\"successor-version\""})).body(ApiResponse.error(410, "v1 \u5df2\u5e9f\u5f03,\u8bf7\u4f7f\u7528 /v2/chat/sessions/" + sessionId));
    }

    @PostMapping(value={"/sessions/{sessionId}/interrupt"})
    public ResponseEntity<ApiResponse<Boolean>> interruptSession(@PathVariable(value="sessionId") String sessionId) {
        log.info("[v1] DEPRECATED call: POST /v1/chat/sessions/{}/interrupt \u2014 return 410", (Object)sessionId);
        return ((ResponseEntity.BodyBuilder)((ResponseEntity.BodyBuilder)ResponseEntity.status((HttpStatusCode)HttpStatus.GONE).header("Deprecation", new String[]{"true"})).header("Link", new String[]{"</v2/chat/sessions/" + sessionId + "/interrupt>; rel=\"successor-version\""})).body(ApiResponse.error(410, "v1 \u5df2\u5e9f\u5f03,\u8bf7\u4f7f\u7528 /v2/chat/sessions/" + sessionId + "/interrupt"));
    }

    @Generated
    public ChatController() {
    }
}

