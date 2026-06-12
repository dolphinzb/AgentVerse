/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import java.time.LocalDateTime;
import lombok.Generated;

public class AgentVersionResponse {
    private String id;
    private String agentId;
    private String version;
    private String snapshotData;
    private String changelog;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @Generated
    public AgentVersionResponse() {
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
    public String getVersion() {
        return this.version;
    }

    @Generated
    public String getSnapshotData() {
        return this.snapshotData;
    }

    @Generated
    public String getChangelog() {
        return this.changelog;
    }

    @Generated
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Generated
    public LocalDateTime getUpdatedTime() {
        return this.updatedTime;
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
    public void setVersion(String version) {
        this.version = version;
    }

    @Generated
    public void setSnapshotData(String snapshotData) {
        this.snapshotData = snapshotData;
    }

    @Generated
    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    @Generated
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Generated
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AgentVersionResponse)) {
            return false;
        }
        AgentVersionResponse other = (AgentVersionResponse)o;
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
        String this$version = this.getVersion();
        String other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) {
            return false;
        }
        String this$snapshotData = this.getSnapshotData();
        String other$snapshotData = other.getSnapshotData();
        if (this$snapshotData == null ? other$snapshotData != null : !this$snapshotData.equals(other$snapshotData)) {
            return false;
        }
        String this$changelog = this.getChangelog();
        String other$changelog = other.getChangelog();
        if (this$changelog == null ? other$changelog != null : !this$changelog.equals(other$changelog)) {
            return false;
        }
        LocalDateTime this$createdTime = this.getCreatedTime();
        LocalDateTime other$createdTime = other.getCreatedTime();
        if (this$createdTime == null ? other$createdTime != null : !((Object)this$createdTime).equals(other$createdTime)) {
            return false;
        }
        LocalDateTime this$updatedTime = this.getUpdatedTime();
        LocalDateTime other$updatedTime = other.getUpdatedTime();
        return !(this$updatedTime == null ? other$updatedTime != null : !((Object)this$updatedTime).equals(other$updatedTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AgentVersionResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $agentId = this.getAgentId();
        result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
        String $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : $version.hashCode());
        String $snapshotData = this.getSnapshotData();
        result = result * 59 + ($snapshotData == null ? 43 : $snapshotData.hashCode());
        String $changelog = this.getChangelog();
        result = result * 59 + ($changelog == null ? 43 : $changelog.hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        LocalDateTime $updatedTime = this.getUpdatedTime();
        result = result * 59 + ($updatedTime == null ? 43 : ((Object)$updatedTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AgentVersionResponse(id=" + this.getId() + ", agentId=" + this.getAgentId() + ", version=" + this.getVersion() + ", snapshotData=" + this.getSnapshotData() + ", changelog=" + this.getChangelog() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ", updatedTime=" + String.valueOf(this.getUpdatedTime()) + ")";
    }
}

