package com.agentverse.runtime.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionMetadata {

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
