/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="agent_long_term_memory")
public class AgentLongTermMemory {
    @TableField(value="id")
    private String id;
    @TableField(value="agent_id")
    private String agentId;
    @TableField(value="memory_type")
    private String memoryType;
    @TableField(value="content")
    private String content;
    @TableField(value="metadata_json")
    private String metadataJson;
    @TableField(value="created_time")
    private LocalDateTime createdTime;

    @Generated
    public AgentLongTermMemory() {
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
    public String getMemoryType() {
        return this.memoryType;
    }

    @Generated
    public String getContent() {
        return this.content;
    }

    @Generated
    public String getMetadataJson() {
        return this.metadataJson;
    }

    @Generated
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
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
    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    @Generated
    public void setContent(String content) {
        this.content = content;
    }

    @Generated
    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
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
        if (!(o instanceof AgentLongTermMemory)) {
            return false;
        }
        AgentLongTermMemory other = (AgentLongTermMemory)o;
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
        String this$memoryType = this.getMemoryType();
        String other$memoryType = other.getMemoryType();
        if (this$memoryType == null ? other$memoryType != null : !this$memoryType.equals(other$memoryType)) {
            return false;
        }
        String this$content = this.getContent();
        String other$content = other.getContent();
        if (this$content == null ? other$content != null : !this$content.equals(other$content)) {
            return false;
        }
        String this$metadataJson = this.getMetadataJson();
        String other$metadataJson = other.getMetadataJson();
        if (this$metadataJson == null ? other$metadataJson != null : !this$metadataJson.equals(other$metadataJson)) {
            return false;
        }
        LocalDateTime this$createdTime = this.getCreatedTime();
        LocalDateTime other$createdTime = other.getCreatedTime();
        return !(this$createdTime == null ? other$createdTime != null : !((Object)this$createdTime).equals(other$createdTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AgentLongTermMemory;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $agentId = this.getAgentId();
        result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
        String $memoryType = this.getMemoryType();
        result = result * 59 + ($memoryType == null ? 43 : $memoryType.hashCode());
        String $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : $content.hashCode());
        String $metadataJson = this.getMetadataJson();
        result = result * 59 + ($metadataJson == null ? 43 : $metadataJson.hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AgentLongTermMemory(id=" + this.getId() + ", agentId=" + this.getAgentId() + ", memoryType=" + this.getMemoryType() + ", content=" + this.getContent() + ", metadataJson=" + this.getMetadataJson() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ")";
    }
}

