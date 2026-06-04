package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统权限实体
 */
@Data
@TableName("sys_permission")
public class SysPermission {

    @TableField("id")
    private Long id;

    @TableField("perm_code")
    private String permCode;

    @TableField("perm_name")
    private String permName;

    @TableField("description")
    private String description;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    @TableField("deleted")
    private Integer deleted;
}