/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.dto.auth.LoginRequest;
import com.agentverse.common.dto.auth.LoginResponse;
import com.agentverse.common.dto.auth.RegisterRequest;
import com.agentverse.common.dto.auth.UserInfoResponse;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.service.TokenBlacklistService;
import com.agentverse.runtime.service.UserService;
import jakarta.validation.Valid;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/auth"})
public class AuthController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping(value={"/register"})
    public ResponseEntity<ApiResponse<UserInfoResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request: {}", (Object)request.getUsername());
        UserInfoResponse userInfo = this.userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    @PostMapping(value={"/login"})
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request: {}", (Object)request.getUsername());
        LoginResponse loginResponse = this.userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @PostMapping(value={"/logout"})
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value="Authorization") String authHeader) {
        log.info("Logout request");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            this.tokenBlacklistService.blacklist(token);
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping(value={"/me"})
    public ResponseEntity<ApiResponse<UserInfoResponse>> me() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return ResponseEntity.status((int)401).build();
        }
        log.info("Get current user: {}", (Object)userId);
        UserInfoResponse userInfo = this.userService.buildUserInfoResponse(this.userService.getUserById(userId));
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    @Generated
    public AuthController(UserService userService, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
    }
}

