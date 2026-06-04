package com.agentverse.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Agent 版本响应
 */
@Data
public class AgentVersionResponse {

    /**
     * 版本 ID
     */
    private String id;

    /**
     * Agent ID
     */
    private String agentId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 快照数据（JSON 字符串）
     */
    private String snapshotData;

    /**
     * 变更日志
     */
    private String changelog;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
