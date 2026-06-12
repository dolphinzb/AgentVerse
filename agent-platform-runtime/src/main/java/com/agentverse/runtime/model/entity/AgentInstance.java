/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="agent_instance")
public class AgentInstance {
    @TableField(value="id")
    private String id;
    @TableField(value="agent_id")
    private String agentId;
    @TableField(value="session_id")
    private String sessionId;
    @TableField(value="status")
    private String status;
    @TableField(value="started_at")
    private LocalDateTime startedAt;
    @TableField(value="last_active_at")
    private LocalDateTime lastActiveAt;

    @Generated
    public AgentInstance() {
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getAgentId() {
        return this.agentId;
    }

    @Generated
    public String getSessionId() {
        return this.sessionId;
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public LocalDateTime getStartedAt() {
        return this.startedAt;
    }

    @Generated
    public LocalDateTime getLastActiveAt() {
        return this.lastActiveAt;
    }

    @Generated
    public void setId(String id) {
        this.id = id;
    }

    @Generated
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Generated
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Generated
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    @Generated
    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AgentInstance)) {
            return false;
        }
        AgentInstance other = (AgentInstance)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$agentId = this.getAgentId();
        String other$agentId = other.getAgentId();
        if (this$agentId == null ? other$agentId != null : !this$agentId.equals(other$agentId)) {
            return false;
        }
        String this$sessionId = this.getSessionId();
        String other$sessionId = other.getSessionId();
        if (this$sessionId == null ? other$sessionId != null : !this$sessionId.equals(other$sessionId)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        LocalDateTime this$startedAt = this.getStartedAt();
        LocalDateTime other$startedAt = other.getStartedAt();
        if (this$startedAt == null ? other$startedAt != null : !((Object)this$startedAt).equals(other$startedAt)) {
            return false;
        }
        LocalDateTime this$lastActiveAt = this.getLastActiveAt();
        LocalDateTime other$lastActiveAt = other.getLastActiveAt();
        return !(this$lastActiveAt == null ? other$lastActiveAt != null : !((Object)this$lastActiveAt).equals(other$lastActiveAt));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AgentInstance;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $agentId = this.getAgentId();
        result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
        String $sessionId = this.getSessionId();
        result = result * 59 + ($sessionId == null ? 43 : $sessionId.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        LocalDateTime $startedAt = this.getStartedAt();
        result = result * 59 + ($startedAt == null ? 43 : ((Object)$startedAt).hashCode());
        LocalDateTime $lastActiveAt = this.getLastActiveAt();
        result = result * 59 + ($lastActiveAt == null ? 43 : ((Object)$lastActiveAt).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AgentInstance(id=" + this.getId() + ", agentId=" + this.getAgentId() + ", sessionId=" + this.getSessionId() + ", status=" + this.getStatus() + ", startedAt=" + String.valueOf(this.getStartedAt()) + ", lastActiveAt=" + String.valueOf(this.getLastActiveAt()) + ")";
    }
}

