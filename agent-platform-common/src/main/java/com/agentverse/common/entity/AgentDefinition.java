package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Agent 定义实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_definition")
public class AgentDefinition extends BaseEntity {

    /**
     * Agent 名称
     */
    @TableField("name")
    private String name;

    /**
     * Agent 描述
     */
    @TableField("description")
    private String description;

    /**
     * 系统提示词
     */
    @TableField("sys_prompt")
    private String sysPrompt;

    /**
     * 最大迭代次数
     */
    @TableField("max_iterations")
    private Integer maxIterations;

    /**
     * 工作区模式 (isolated/shared)
     */
    @TableField("workspace_mode")
    private String workspaceMode;

    /**
     * 状态 (draft/published/archived)
     */
    @TableField("status")
    private String status;

    /**
     * 当前版本 ID
     */
    @TableField("current_version")
    private String currentVersion;
}
