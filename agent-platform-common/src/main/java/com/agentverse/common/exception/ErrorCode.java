/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.exception;

import lombok.Generated;

public enum ErrorCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error"),
    AGENT_NOT_FOUND(1001, "Agent not found"),
    AGENT_ALREADY_EXISTS(1002, "Agent already exists"),
    AGENT_VERSION_NOT_FOUND(1003, "Agent version not found"),
    AGENT_VERSION_DUPLICATE(1004, "Agent version already exists"),
    SESSION_NOT_FOUND(1101, "Session not found"),
    SESSION_EXPIRED(1102, "Session expired"),
    SESSION_STOPPED(1103, "Session has been stopped"),
    VALIDATION_FAILED(1201, "Validation failed"),
    PARAMETER_MISSING(1202, "Required parameter missing"),
    PARAMETER_INVALID(1203, "Parameter invalid"),
    USERNAME_EXISTS(1301, "Username already exists"),
    INVALID_CREDENTIALS(1302, "Invalid username or password"),
    TOKEN_EXPIRED(1303, "Token has expired"),
    TOKEN_INVALID(1304, "Token is invalid"),
    TOKEN_BLACKLISTED(1305, "Token has been revoked"),
    USER_DISABLED(1306, "User account is disabled"),
    PERMISSION_DENIED(1401, "Permission denied"),
    MODEL_PROVIDER_NOT_FOUND(1501, "Model provider not found"),
    MODEL_CONFIG_NOT_FOUND(1502, "Model config not found"),
    MODEL_PROVIDER_DISABLED(1503, "Model provider is disabled"),
    API_KEY_ENCRYPTION_ERROR(1504, "API key encryption error"),
    MODEL_CONNECTION_TEST_FAILED(1505, "Model connection test failed"),
    MODEL_CONFIG_IN_USE(1506, "Model config is in use"),
    MODEL_PROVIDER_IN_USE(1507, "Model provider is in use"),
    MODEL_PROVIDER_TYPE_UNSUPPORTED(1508, "Model provider type is unsupported"),
    AGENT_NOT_PUBLISHED(1601, "Agent is not published"),
    AGENT_ARCHIVED(1602, "Agent has been archived"),
    AGENT_ALREADY_PUBLISHED(1603, "Agent is already published"),
    AGENT_NOT_ACTIVE(1604, "Agent is not active");

    private final Integer code;
    private final String message;

    @Generated
    public Integer getCode() {
        return this.code;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    private ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

