package com.agentverse.common.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 64, message = "Username must be 3-64 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be 8-128 characters")
    private String password;

    private String email;

    @Override
    public String toString() {
        return "RegisterRequest{username='" + username + "', email='" + email + "', password='***'}";
    }
}