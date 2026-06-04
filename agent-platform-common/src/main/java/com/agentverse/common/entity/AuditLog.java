package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志实体
 */
@Data
@TableName("audit_log")
public class AuditLog {

    @TableField("id")
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("username")
    private String username;

    @TableField("action")
    private String action;

    @TableField("target")
    private String target;

    @TableField("detail")
    private String detail;

    @TableField("ip")
    private String ip;

    @TableField("created_time")
    private LocalDateTime createdTime;
}