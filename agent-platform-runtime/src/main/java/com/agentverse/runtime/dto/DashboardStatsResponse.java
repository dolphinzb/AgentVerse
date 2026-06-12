/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.dto;

import java.util.Map;
import lombok.Generated;

public class DashboardStatsResponse {
    private Map<String, Long> agentCount;
    private Long activeSessions;
    private Long todayChatCount;
    private TokenUsage tokenUsage;

    @Generated
    public DashboardStatsResponse() {
    }

    @Generated
    public Map<String, Long> getAgentCount() {
        return this.agentCount;
    }

    @Generated
    public Long getActiveSessions() {
        return this.activeSessions;
    }

    @Generated
    public Long getTodayChatCount() {
        return this.todayChatCount;
    }

    @Generated
    public TokenUsage getTokenUsage() {
        return this.tokenUsage;
    }

    @Generated
    public void setAgentCount(Map<String, Long> agentCount) {
        this.agentCount = agentCount;
    }

    @Generated
    public void setActiveSessions(Long activeSessions) {
        this.activeSessions = activeSessions;
    }

    @Generated
    public void setTodayChatCount(Long todayChatCount) {
        this.todayChatCount = todayChatCount;
    }

    @Generated
    public void setTokenUsage(TokenUsage tokenUsage) {
        this.tokenUsage = tokenUsage;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DashboardStatsResponse)) {
            return false;
        }
        DashboardStatsResponse other = (DashboardStatsResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$activeSessions = this.getActiveSessions();
        Long other$activeSessions = other.getActiveSessions();
        if (this$activeSessions == null ? other$activeSessions != null : !((Object)this$activeSessions).equals(other$activeSessions)) {
            return false;
        }
        Long this$todayChatCount = this.getTodayChatCount();
        Long other$todayChatCount = other.getTodayChatCount();
        if (this$todayChatCount == null ? other$todayChatCount != null : !((Object)this$todayChatCount).equals(other$todayChatCount)) {
            return false;
        }
        Map<String, Long> this$agentCount = this.getAgentCount();
        Map<String, Long> other$agentCount = other.getAgentCount();
        if (this$agentCount == null ? other$agentCount != null : !((Object)this$agentCount).equals(other$agentCount)) {
            return false;
        }
        TokenUsage this$tokenUsage = this.getTokenUsage();
        TokenUsage other$tokenUsage = other.getTokenUsage();
        return !(this$tokenUsage == null ? other$tokenUsage != null : !((Object)this$tokenUsage).equals(other$tokenUsage));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof DashboardStatsResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $activeSessions = this.getActiveSessions();
        result = result * 59 + ($activeSessions == null ? 43 : ((Object)$activeSessions).hashCode());
        Long $todayChatCount = this.getTodayChatCount();
        result = result * 59 + ($todayChatCount == null ? 43 : ((Object)$todayChatCount).hashCode());
        Map<String, Long> $agentCount = this.getAgentCount();
        result = result * 59 + ($agentCount == null ? 43 : ((Object)$agentCount).hashCode());
        TokenUsage $tokenUsage = this.getTokenUsage();
        result = result * 59 + ($tokenUsage == null ? 43 : ((Object)$tokenUsage).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "DashboardStatsResponse(agentCount=" + String.valueOf(this.getAgentCount()) + ", activeSessions=" + this.getActiveSessions() + ", todayChatCount=" + this.getTodayChatCount() + ", tokenUsage=" + String.valueOf(this.getTokenUsage()) + ")";
    }

    public static class TokenUsage {
        private Long inputTokens;
        private Long outputTokens;

        @Generated
        public TokenUsage() {
        }

        @Generated
        public Long getInputTokens() {
            return this.inputTokens;
        }

        @Generated
        public Long getOutputTokens() {
            return this.outputTokens;
        }

        @Generated
        public void setInputTokens(Long inputTokens) {
            this.inputTokens = inputTokens;
        }

        @Generated
        public void setOutputTokens(Long outputTokens) {
            this.outputTokens = outputTokens;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof TokenUsage)) {
                return false;
            }
            TokenUsage other = (TokenUsage)o;
            if (!other.canEqual(this)) {
                return false;
            }
            Long this$inputTokens = this.getInputTokens();
            Long other$inputTokens = other.getInputTokens();
            if (this$inputTokens == null ? other$inputTokens != null : !((Object)this$inputTokens).equals(other$inputTokens)) {
                return false;
            }
            Long this$outputTokens = this.getOutputTokens();
            Long other$outputTokens = other.getOutputTokens();
            return !(this$outputTokens == null ? other$outputTokens != null : !((Object)this$outputTokens).equals(other$outputTokens));
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof TokenUsage;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Long $inputTokens = this.getInputTokens();
            result = result * 59 + ($inputTokens == null ? 43 : ((Object)$inputTokens).hashCode());
            Long $outputTokens = this.getOutputTokens();
            result = result * 59 + ($outputTokens == null ? 43 : ((Object)$outputTokens).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "DashboardStatsResponse.TokenUsage(inputTokens=" + this.getInputTokens() + ", outputTokens=" + this.getOutputTokens() + ")";
        }
    }
}

