/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class SessionCreateRequest {
    @NotBlank(message="agentId \u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="agentId \u4e0d\u80fd\u4e3a\u7a7a") String agentId;

    @Generated
    public SessionCreateRequest() {
    }

    @Generated
    public String getAgentId() {
        return this.agentId;
    }

    @Generated
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SessionCreateRequest)) {
            return false;
        }
        SessionCreateRequest other = (SessionCreateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$agentId = this.getAgentId();
        String other$agentId = other.getAgentId();
        return !(this$agentId == null ? other$agentId != null : !this$agentId.equals(other$agentId));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SessionCreateRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $agentId = this.getAgentId();
        result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SessionCreateRequest(agentId=" + this.getAgentId() + ")";
    }
}

