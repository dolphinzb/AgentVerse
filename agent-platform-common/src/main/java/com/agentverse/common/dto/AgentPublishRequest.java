/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Generated;

public class AgentPublishRequest {
    @NotBlank(message="\u7248\u672c\u53f7\u4e0d\u80fd\u4e3a\u7a7a")
    @Pattern(regexp="^v\\d+\\.\\d+\\.\\d+$", message="\u7248\u672c\u53f7\u683c\u5f0f\u5fc5\u987b\u4e3a vX.Y.Z\uff08\u4f8b\u5982\uff1av1.0.0\uff09")
    private @NotBlank(message="\u7248\u672c\u53f7\u4e0d\u80fd\u4e3a\u7a7a") @Pattern(regexp="^v\\d+\\.\\d+\\.\\d+$", message="\u7248\u672c\u53f7\u683c\u5f0f\u5fc5\u987b\u4e3a vX.Y.Z\uff08\u4f8b\u5982\uff1av1.0.0\uff09") String version;
    @Size(max=1000, message="\u53d8\u66f4\u65e5\u5fd7\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7 1000 \u4e2a\u5b57\u7b26")
    private @Size(max=1000, message="\u53d8\u66f4\u65e5\u5fd7\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7 1000 \u4e2a\u5b57\u7b26") String changelog;

    @Generated
    public AgentPublishRequest() {
    }

    @Generated
    public String getVersion() {
        return this.version;
    }

    @Generated
    public String getChangelog() {
        return this.changelog;
    }

    @Generated
    public void setVersion(String version) {
        this.version = version;
    }

    @Generated
    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AgentPublishRequest)) {
            return false;
        }
        AgentPublishRequest other = (AgentPublishRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$version = this.getVersion();
        String other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) {
            return false;
        }
        String this$changelog = this.getChangelog();
        String other$changelog = other.getChangelog();
        return !(this$changelog == null ? other$changelog != null : !this$changelog.equals(other$changelog));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AgentPublishRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : $version.hashCode());
        String $changelog = this.getChangelog();
        result = result * 59 + ($changelog == null ? 43 : $changelog.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AgentPublishRequest(version=" + this.getVersion() + ", changelog=" + this.getChangelog() + ")";
    }
}

