package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Agent 版本实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_version")
public class AgentVersion extends BaseEntity {

    /**
     * Agent ID
     */
    @TableField("agent_id")
    private String agentId;

    /**
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 版本快照数据 (JSON)
     */
    @TableField("snapshot_data")
    private String snapshotData;

    /**
     * 变更日志
     */
    @TableField("changelog")
    private String changelog;
}
