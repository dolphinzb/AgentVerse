package com.agentverse.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error"),

    // Agent 相关错误 (1000-1099)
    AGENT_NOT_FOUND(1001, "Agent not found"),
    AGENT_ALREADY_EXISTS(1002, "Agent already exists"),
    AGENT_VERSION_NOT_FOUND(1003, "Agent version not found"),
    AGENT_VERSION_DUPLICATE(1004, "Agent version already exists"),

    // 会话相关错误 (1100-1199)
    SESSION_NOT_FOUND(1101, "Session not found"),
    SESSION_EXPIRED(1102, "Session expired"),

    // 验证错误 (1200-1299)
    VALIDATION_FAILED(1201, "Validation failed"),
    PARAMETER_MISSING(1202, "Required parameter missing"),
    PARAMETER_INVALID(1203, "Parameter invalid"),

    // 认证错误 (1300-1399)
    USERNAME_EXISTS(1301, "Username already exists"),
    INVALID_CREDENTIALS(1302, "Invalid username or password"),
    TOKEN_EXPIRED(1303, "Token has expired"),
    TOKEN_INVALID(1304, "Token is invalid"),
    TOKEN_BLACKLISTED(1305, "Token has been revoked"),
    USER_DISABLED(1306, "User account is disabled"),

    // 权限错误 (1400-1499)
    PERMISSION_DENIED(1401, "Permission denied");

    private final Integer code;
    private final String message;
}
