package com.agentverse.common.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Agent 更新请求
 */
@Data
public class AgentUpdateRequest {

    /**
     * Agent 名称
     */
    @Size(max = 100, message = "Agent 名称长度不能超过 100 个字符")
    private String name;

    /**
     * Agent 描述
     */
    @Size(max = 500, message = "Agent 描述长度不能超过 500 个字符")
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
}
