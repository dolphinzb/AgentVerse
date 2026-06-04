package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Token 黑名单实体
 */
@Data
@TableName("token_blacklist")
public class TokenBlacklist {

    @TableField("id")
    private Long id;

    @TableField("token_jti")
    private String tokenJti;

    @TableField("expires_at")
    private LocalDateTime expiresAt;

    @TableField("created_time")
    private LocalDateTime createdTime;
}