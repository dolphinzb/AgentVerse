package com.agentverse.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Agent 创建请求
 */
@Data
public class AgentCreateRequest {

    /**
     * Agent 名称
     */
    @NotBlank(message = "Agent 名称不能为空")
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
    @NotBlank(message = "系统提示词不能为空")
    private String sysPrompt;

    /**
     * 最大迭代次数
     */
    private Integer maxIterations = 10;
}
