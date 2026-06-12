/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="audit_log")
public class AuditLog {
    @TableField(value="id")
    private Long id;
    @TableField(value="user_id")
    private Long userId;
    @TableField(value="username")
    private String username;
    @TableField(value="action")
    private String action;
    @TableField(value="target")
    private String target;
    @TableField(value="detail")
    private String detail;
    @TableField(value="ip")
    private String ip;
    @TableField(value="created_time")
    private LocalDateTime createdTime;

    @Generated
    public AuditLog() {
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public Long getUserId() {
        return this.userId;
    }

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public String getAction() {
        return this.action;
    }

    @Generated
    public String getTarget() {
        return this.target;
    }

    @Generated
    public String getDetail() {
        return this.detail;
    }

    @Generated
    public String getIp() {
        return this.ip;
    }

    @Generated
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Generated
    public void setUsername(String username) {
        this.username = username;
    }

    @Generated
    public void setAction(String action) {
        this.action = action;
    }

    @Generated
    public void setTarget(String target) {
        this.target = target;
    }

    @Generated
    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Generated
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Generated
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuditLog)) {
            return false;
        }
        AuditLog other = (AuditLog)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$userId = this.getUserId();
        Long other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !((Object)this$userId).equals(other$userId)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$action = this.getAction();
        String other$action = other.getAction();
        if (this$action == null ? other$action != null : !this$action.equals(other$action)) {
            return false;
        }
        String this$target = this.getTarget();
        String other$target = other.getTarget();
        if (this$target == null ? other$target != null : !this$target.equals(other$target)) {
            return false;
        }
        String this$detail = this.getDetail();
        String other$detail = other.getDetail();
        if (this$detail == null ? other$detail != null : !this$detail.equals(other$detail)) {
            return false;
        }
        String this$ip = this.getIp();
        String other$ip = other.getIp();
        if (this$ip == null ? other$ip != null : !this$ip.equals(other$ip)) {
            return false;
        }
        LocalDateTime this$createdTime = this.getCreatedTime();
        LocalDateTime other$createdTime = other.getCreatedTime();
        return !(this$createdTime == null ? other$createdTime != null : !((Object)this$createdTime).equals(other$createdTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AuditLog;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : ((Object)$userId).hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $action = this.getAction();
        result = result * 59 + ($action == null ? 43 : $action.hashCode());
        String $target = this.getTarget();
        result = result * 59 + ($target == null ? 43 : $target.hashCode());
        String $detail = this.getDetail();
        result = result * 59 + ($detail == null ? 43 : $detail.hashCode());
        String $ip = this.getIp();
        result = result * 59 + ($ip == null ? 43 : $ip.hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AuditLog(id=" + this.getId() + ", userId=" + this.getUserId() + ", username=" + this.getUsername() + ", action=" + this.getAction() + ", target=" + this.getTarget() + ", detail=" + this.getDetail() + ", ip=" + this.getIp() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ")";
    }
}

