package com.agentverse.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Agent 响应对象
 */
@Data
public class AgentResponse {

    /**
     * Agent ID
     */
    private String id;

    /**
     * Agent 名称
     */
    private String name;

    /**
     * Agent 描述
     */
    private String description;

    /**
     * 系统提示词
     */
    private String sysPrompt;

    /**
     * 最大迭代次数
     */
    private Integer maxIterations;

    /**
     * 工作区模式
     */
    private String workspaceMode;

    /**
     * 状态
     */
    private String status;

    /**
     * 当前版本
     */
    private String currentVersion;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
