/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import java.time.LocalDateTime;
import lombok.Generated;

public class SessionResponse {
    private String sessionId;
    private String agentId;
    private String agentName;
    private LocalDateTime createdAt;

    @Generated
    public SessionResponse() {
    }

    @Generated
    public String getSessionId() {
        return this.sessionId;
    }

    @Generated
    public String getAgentId() {
        return this.agentId;
    }

    @Generated
    public String getAgentName() {
        return this.agentName;
    }

    @Generated
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Generated
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Generated
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Generated
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SessionResponse)) {
            return false;
        }
        SessionResponse other = (SessionResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$sessionId = this.getSessionId();
        String other$sessionId = other.getSessionId();
        if (this$sessionId == null ? other$sessionId != null : !this$sessionId.equals(other$sessionId)) {
            return false;
        }
        String this$agentId = this.getAgentId();
        String other$agentId = other.getAgentId();
        if (this$agentId == null ? other$agentId != null : !this$agentId.equals(other$agentId)) {
            return false;
        }
        String this$agentName = this.getAgentName();
        String other$agentName = other.getAgentName();
        if (this$agentName == null ? other$agentName != null : !this$agentName.equals(other$agentName)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SessionResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $sessionId = this.getSessionId();
        result = result * 59 + ($sessionId == null ? 43 : $sessionId.hashCode());
        String $agentId = this.getAgentId();
        result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
        String $agentName = this.getAgentName();
        result = result * 59 + ($agentName == null ? 43 : $agentName.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SessionResponse(sessionId=" + this.getSessionId() + ", agentId=" + this.getAgentId() + ", agentName=" + this.getAgentName() + ", createdAt=" + String.valueOf(this.getCreatedAt()) + ")";
    }
}

