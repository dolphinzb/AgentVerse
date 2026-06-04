package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.dto.auth.*;
import com.agentverse.common.security.JwtUtils;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.service.TokenBlacklistService;
import com.agentverse.runtime.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserInfoResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register request: {}", request.getUsername());
        UserInfoResponse userInfo = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request: {}", request.getUsername());
        LoginResponse loginResponse = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {
        log.info("Logout request");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklist(token);
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> me() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        log.info("Get current user: {}", userId);
        UserInfoResponse userInfo = userService.buildUserInfoResponse(
                userService.getUserById(userId));
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}