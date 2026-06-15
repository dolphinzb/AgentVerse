/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.entity;

import com.agentverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Generated;

@TableName(value="agent_version")
public class AgentVersion
extends BaseEntity {
    @TableField(value="agent_id")
    private String agentId;
    @TableField(value="version")
    private String version;
    @TableField(value="snapshot_data")
    private String snapshotData;
    @TableField(value="changelog")
    private String changelog;

    @Generated
    public AgentVersion() {
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

    @Override
    @Generated
    public String toString() {
        return "AgentVersion(agentId=" + this.getAgentId() + ", version=" + this.getVersion() + ", snapshotData=" + this.getSnapshotData() + ", changelog=" + this.getChangelog() + ")";
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AgentVersion)) {
            return false;
        }
        AgentVersion other = (AgentVersion)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
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
        return !(this$changelog == null ? other$changelog != null : !this$changelog.equals(other$changelog));
    }

    @Override
    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AgentVersion;
    }

    @Override
    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $agentId = this.getAgentId();
        result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
        String $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : $version.hashCode());
        String $snapshotData = this.getSnapshotData();
        result = result * 59 + ($snapshotData == null ? 43 : $snapshotData.hashCode());
        String $changelog = this.getChangelog();
        result = result * 59 + ($changelog == null ? 43 : $changelog.hashCode());
        return result;
    }
}

