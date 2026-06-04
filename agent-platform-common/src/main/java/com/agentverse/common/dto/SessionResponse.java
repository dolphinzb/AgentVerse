package com.agentverse.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话响应
 */
@Data
public class SessionResponse {

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * Agent ID
     */
    private String agentId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
