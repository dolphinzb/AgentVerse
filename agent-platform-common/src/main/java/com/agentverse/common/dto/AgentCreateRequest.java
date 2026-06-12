/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Generated;

public class AgentCreateRequest {
    @NotBlank(message="Agent \u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max=100, message="Agent \u540d\u79f0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7 100 \u4e2a\u5b57\u7b26")
    private @NotBlank(message="Agent \u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") @Size(max=100, message="Agent \u540d\u79f0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7 100 \u4e2a\u5b57\u7b26") String name;
    @Size(max=500, message="Agent \u63cf\u8ff0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7 500 \u4e2a\u5b57\u7b26")
    private @Size(max=500, message="Agent \u63cf\u8ff0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7 500 \u4e2a\u5b57\u7b26") String description;
    @NotBlank(message="\u7cfb\u7edf\u63d0\u793a\u8bcd\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u7cfb\u7edf\u63d0\u793a\u8bcd\u4e0d\u80fd\u4e3a\u7a7a") String sysPrompt;
    @NotBlank(message="\u6a21\u578b\u914d\u7f6e\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u6a21\u578b\u914d\u7f6e\u4e0d\u80fd\u4e3a\u7a7a") String modelConfigId;
    private Integer maxIterations = 10;
    private String filesystemType;
    private Integer enableMemoryFlush;
    private Integer enableMemoryMaintenance;
    private Integer enableCompaction;
    private Integer compactionTriggerPct;
    private Integer compactionKeepRecent;
    private Integer enableToolResultEviction;
    private Integer toolResultEvictionMaxChars;
    private Integer enableLongTermMemory;
    private Integer enablePlan;
    private Integer enableSessionPersistence;
    private String sessionBackend;
    private Integer maxContextTokens;

    @Generated
    public AgentCreateRequest() {
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public String getSysPrompt() {
        return this.sysPrompt;
    }

    @Generated
    public String getModelConfigId() {
        return this.modelConfigId;
    }

    @Generated
    public Integer getMaxIterations() {
        return this.maxIterations;
    }

    @Generated
    public String getFilesystemType() {
        return this.filesystemType;
    }

    @Generated
    public Integer getEnableMemoryFlush() {
        return this.enableMemoryFlush;
    }

    @Generated
    public Integer getEnableMemoryMaintenance() {
        return this.enableMemoryMaintenance;
    }

    @Generated
    public Integer getEnableCompaction() {
        return this.enableCompaction;
    }

    @Generated
    public Integer getCompactionTriggerPct() {
        return this.compactionTriggerPct;
    }

    @Generated
    public Integer getCompactionKeepRecent() {
        return this.compactionKeepRecent;
    }

    @Generated
    public Integer getEnableToolResultEviction() {
        return this.enableToolResultEviction;
    }

    @Generated
    public Integer getToolResultEvictionMaxChars() {
        return this.toolResultEvictionMaxChars;
    }

    @Generated
    public Integer getEnableLongTermMemory() {
        return this.enableLongTermMemory;
    }

    @Generated
    public Integer getEnablePlan() {
        return this.enablePlan;
    }

    @Generated
    public Integer getEnableSessionPersistence() {
        return this.enableSessionPersistence;
    }

    @Generated
    public String getSessionBackend() {
        return this.sessionBackend;
    }

    @Generated
    public Integer getMaxContextTokens() {
        return this.maxContextTokens;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated
    public void setSysPrompt(String sysPrompt) {
        this.sysPrompt = sysPrompt;
    }

    @Generated
    public void setModelConfigId(String modelConfigId) {
        this.modelConfigId = modelConfigId;
    }

    @Generated
    public void setMaxIterations(Integer maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Generated
    public void setFilesystemType(String filesystemType) {
        this.filesystemType = filesystemType;
    }

    @Generated
    public void setEnableMemoryFlush(Integer enableMemoryFlush) {
        this.enableMemoryFlush = enableMemoryFlush;
    }

    @Generated
    public void setEnableMemoryMaintenance(Integer enableMemoryMaintenance) {
        this.enableMemoryMaintenance = enableMemoryMaintenance;
    }

    @Generated
    public void setEnableCompaction(Integer enableCompaction) {
        this.enableCompaction = enableCompaction;
    }

    @Generated
    public void setCompactionTriggerPct(Integer compactionTriggerPct) {
        this.compactionTriggerPct = compactionTriggerPct;
    }

    @Generated
    public void setCompactionKeepRecent(Integer compactionKeepRecent) {
        this.compactionKeepRecent = compactionKeepRecent;
    }

    @Generated
    public void setEnableToolResultEviction(Integer enableToolResultEviction) {
        this.enableToolResultEviction = enableToolResultEviction;
    }

    @Generated
    public void setToolResultEvictionMaxChars(Integer toolResultEvictionMaxChars) {
        this.toolResultEvictionMaxChars = toolResultEvictionMaxChars;
    }

    @Generated
    public void setEnableLongTermMemory(Integer enableLongTermMemory) {
        this.enableLongTermMemory = enableLongTermMemory;
    }

    @Generated
    public void setEnablePlan(Integer enablePlan) {
        this.enablePlan = enablePlan;
    }

    @Generated
    public void setEnableSessionPersistence(Integer enableSessionPersistence) {
        this.enableSessionPersistence = enableSessionPersistence;
    }

    @Generated
    public void setSessionBackend(String sessionBackend) {
        this.sessionBackend = sessionBackend;
    }

    @Generated
    public void setMaxContextTokens(Integer maxContextTokens) {
        this.maxContextTokens = maxContextTokens;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AgentCreateRequest)) {
            return false;
        }
        AgentCreateRequest other = (AgentCreateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$maxIterations = this.getMaxIterations();
        Integer other$maxIterations = other.getMaxIterations();
        if (this$maxIterations == null ? other$maxIterations != null : !((Object)this$maxIterations).equals(other$maxIterations)) {
            return false;
        }
        Integer this$enableMemoryFlush = this.getEnableMemoryFlush();
        Integer other$enableMemoryFlush = other.getEnableMemoryFlush();
        if (this$enableMemoryFlush == null ? other$enableMemoryFlush != null : !((Object)this$enableMemoryFlush).equals(other$enableMemoryFlush)) {
            return false;
        }
        Integer this$enableMemoryMaintenance = this.getEnableMemoryMaintenance();
        Integer other$enableMemoryMaintenance = other.getEnableMemoryMaintenance();
        if (this$enableMemoryMaintenance == null ? other$enableMemoryMaintenance != null : !((Object)this$enableMemoryMaintenance).equals(other$enableMemoryMaintenance)) {
            return false;
        }
        Integer this$enableCompaction = this.getEnableCompaction();
        Integer other$enableCompaction = other.getEnableCompaction();
        if (this$enableCompaction == null ? other$enableCompaction != null : !((Object)this$enableCompaction).equals(other$enableCompaction)) {
            return false;
        }
        Integer this$compactionTriggerPct = this.getCompactionTriggerPct();
        Integer other$compactionTriggerPct = other.getCompactionTriggerPct();
        if (this$compactionTriggerPct == null ? other$compactionTriggerPct != null : !((Object)this$compactionTriggerPct).equals(other$compactionTriggerPct)) {
            return false;
        }
        Integer this$compactionKeepRecent = this.getCompactionKeepRecent();
        Integer other$compactionKeepRecent = other.getCompactionKeepRecent();
        if (this$compactionKeepRecent == null ? other$compactionKeepRecent != null : !((Object)this$compactionKeepRecent).equals(other$compactionKeepRecent)) {
            return false;
        }
        Integer this$enableToolResultEviction = this.getEnableToolResultEviction();
        Integer other$enableToolResultEviction = other.getEnableToolResultEviction();
        if (this$enableToolResultEviction == null ? other$enableToolResultEviction != null : !((Object)this$enableToolResultEviction).equals(other$enableToolResultEviction)) {
            return false;
        }
        Integer this$toolResultEvictionMaxChars = this.getToolResultEvictionMaxChars();
        Integer other$toolResultEvictionMaxChars = other.getToolResultEvictionMaxChars();
        if (this$toolResultEvictionMaxChars == null ? other$toolResultEvictionMaxChars != null : !((Object)this$toolResultEvictionMaxChars).equals(other$toolResultEvictionMaxChars)) {
            return false;
        }
        Integer this$enableLongTermMemory = this.getEnableLongTermMemory();
        Integer other$enableLongTermMemory = other.getEnableLongTermMemory();
        if (this$enableLongTermMemory == null ? other$enableLongTermMemory != null : !((Object)this$enableLongTermMemory).equals(other$enableLongTermMemory)) {
            return false;
        }
        Integer this$enablePlan = this.getEnablePlan();
        Integer other$enablePlan = other.getEnablePlan();
        if (this$enablePlan == null ? other$enablePlan != null : !((Object)this$enablePlan).equals(other$enablePlan)) {
            return false;
        }
        Integer this$enableSessionPersistence = this.getEnableSessionPersistence();
        Integer other$enableSessionPersistence = other.getEnableSessionPersistence();
        if (this$enableSessionPersistence == null ? other$enableSessionPersistence != null : !((Object)this$enableSessionPersistence).equals(other$enableSessionPersistence)) {
            return false;
        }
        Integer this$maxContextTokens = this.getMaxContextTokens();
        Integer other$maxContextTokens = other.getMaxContextTokens();
        if (this$maxContextTokens == null ? other$maxContextTokens != null : !((Object)this$maxContextTokens).equals(other$maxContextTokens)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$sysPrompt = this.getSysPrompt();
        String other$sysPrompt = other.getSysPrompt();
        if (this$sysPrompt == null ? other$sysPrompt != null : !this$sysPrompt.equals(other$sysPrompt)) {
            return false;
        }
        String this$modelConfigId = this.getModelConfigId();
        String other$modelConfigId = other.getModelConfigId();
        if (this$modelConfigId == null ? other$modelConfigId != null : !this$modelConfigId.equals(other$modelConfigId)) {
            return false;
        }
        String this$filesystemType = this.getFilesystemType();
        String other$filesystemType = other.getFilesystemType();
        if (this$filesystemType == null ? other$filesystemType != null : !this$filesystemType.equals(other$filesystemType)) {
            return false;
        }
        String this$sessionBackend = this.getSessionBackend();
        String other$sessionBackend = other.getSessionBackend();
        return !(this$sessionBackend == null ? other$sessionBackend != null : !this$sessionBackend.equals(other$sessionBackend));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AgentCreateRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $maxIterations = this.getMaxIterations();
        result = result * 59 + ($maxIterations == null ? 43 : ((Object)$maxIterations).hashCode());
        Integer $enableMemoryFlush = this.getEnableMemoryFlush();
        result = result * 59 + ($enableMemoryFlush == null ? 43 : ((Object)$enableMemoryFlush).hashCode());
        Integer $enableMemoryMaintenance = this.getEnableMemoryMaintenance();
        result = result * 59 + ($enableMemoryMaintenance == null ? 43 : ((Object)$enableMemoryMaintenance).hashCode());
        Integer $enableCompaction = this.getEnableCompaction();
        result = result * 59 + ($enableCompaction == null ? 43 : ((Object)$enableCompaction).hashCode());
        Integer $compactionTriggerPct = this.getCompactionTriggerPct();
        result = result * 59 + ($compactionTriggerPct == null ? 43 : ((Object)$compactionTriggerPct).hashCode());
        Integer $compactionKeepRecent = this.getCompactionKeepRecent();
        result = result * 59 + ($compactionKeepRecent == null ? 43 : ((Object)$compactionKeepRecent).hashCode());
        Integer $enableToolResultEviction = this.getEnableToolResultEviction();
        result = result * 59 + ($enableToolResultEviction == null ? 43 : ((Object)$enableToolResultEviction).hashCode());
        Integer $toolResultEvictionMaxChars = this.getToolResultEvictionMaxChars();
        result = result * 59 + ($toolResultEvictionMaxChars == null ? 43 : ((Object)$toolResultEvictionMaxChars).hashCode());
        Integer $enableLongTermMemory = this.getEnableLongTermMemory();
        result = result * 59 + ($enableLongTermMemory == null ? 43 : ((Object)$enableLongTermMemory).hashCode());
        Integer $enablePlan = this.getEnablePlan();
        result = result * 59 + ($enablePlan == null ? 43 : ((Object)$enablePlan).hashCode());
        Integer $enableSessionPersistence = this.getEnableSessionPersistence();
        result = result * 59 + ($enableSessionPersistence == null ? 43 : ((Object)$enableSessionPersistence).hashCode());
        Integer $maxContextTokens = this.getMaxContextTokens();
        result = result * 59 + ($maxContextTokens == null ? 43 : ((Object)$maxContextTokens).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $sysPrompt = this.getSysPrompt();
        result = result * 59 + ($sysPrompt == null ? 43 : $sysPrompt.hashCode());
        String $modelConfigId = this.getModelConfigId();
        result = result * 59 + ($modelConfigId == null ? 43 : $modelConfigId.hashCode());
        String $filesystemType = this.getFilesystemType();
        result = result * 59 + ($filesystemType == null ? 43 : $filesystemType.hashCode());
        String $sessionBackend = this.getSessionBackend();
        result = result * 59 + ($sessionBackend == null ? 43 : $sessionBackend.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AgentCreateRequest(name=" + this.getName() + ", description=" + this.getDescription() + ", sysPrompt=" + this.getSysPrompt() + ", modelConfigId=" + this.getModelConfigId() + ", maxIterations=" + this.getMaxIterations() + ", filesystemType=" + this.getFilesystemType() + ", enableMemoryFlush=" + this.getEnableMemoryFlush() + ", enableMemoryMaintenance=" + this.getEnableMemoryMaintenance() + ", enableCompaction=" + this.getEnableCompaction() + ", compactionTriggerPct=" + this.getCompactionTriggerPct() + ", compactionKeepRecent=" + this.getCompactionKeepRecent() + ", enableToolResultEviction=" + this.getEnableToolResultEviction() + ", toolResultEvictionMaxChars=" + this.getToolResultEvictionMaxChars() + ", enableLongTermMemory=" + this.getEnableLongTermMemory() + ", enablePlan=" + this.getEnablePlan() + ", enableSessionPersistence=" + this.getEnableSessionPersistence() + ", sessionBackend=" + this.getSessionBackend() + ", maxContextTokens=" + this.getMaxContextTokens() + ")";
    }
}

