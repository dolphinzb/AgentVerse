package com.agentverse.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息响应
 */
@Data
public class MessageResponse {

    /**
     * 角色（user/assistant）
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
}
