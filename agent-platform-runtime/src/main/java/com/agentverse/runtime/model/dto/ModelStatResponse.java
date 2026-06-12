/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import lombok.Generated;

public class ModelStatResponse {
    private String modelConfigId;
    private String modelName;
    private String providerName;
    private String providerType;
    private Long callCount;
    private Long totalInputTokens;
    private Long totalOutputTokens;

    @Generated
    public ModelStatResponse() {
    }

    @Generated
    public String getModelConfigId() {
        return this.modelConfigId;
    }

    @Generated
    public String getModelName() {
        return this.modelName;
    }

    @Generated
    public String getProviderName() {
        return this.providerName;
    }

    @Generated
    public String getProviderType() {
        return this.providerType;
    }

    @Generated
    public Long getCallCount() {
        return this.callCount;
    }

    @Generated
    public Long getTotalInputTokens() {
        return this.totalInputTokens;
    }

    @Generated
    public Long getTotalOutputTokens() {
        return this.totalOutputTokens;
    }

    @Generated
    public void setModelConfigId(String modelConfigId) {
        this.modelConfigId = modelConfigId;
    }

    @Generated
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Generated
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @Generated
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @Generated
    public void setCallCount(Long callCount) {
        this.callCount = callCount;
    }

    @Generated
    public void setTotalInputTokens(Long totalInputTokens) {
        this.totalInputTokens = totalInputTokens;
    }

    @Generated
    public void setTotalOutputTokens(Long totalOutputTokens) {
        this.totalOutputTokens = totalOutputTokens;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModelStatResponse)) {
            return false;
        }
        ModelStatResponse other = (ModelStatResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$callCount = this.getCallCount();
        Long other$callCount = other.getCallCount();
        if (this$callCount == null ? other$callCount != null : !((Object)this$callCount).equals(other$callCount)) {
            return false;
        }
        Long this$totalInputTokens = this.getTotalInputTokens();
        Long other$totalInputTokens = other.getTotalInputTokens();
        if (this$totalInputTokens == null ? other$totalInputTokens != null : !((Object)this$totalInputTokens).equals(other$totalInputTokens)) {
            return false;
        }
        Long this$totalOutputTokens = this.getTotalOutputTokens();
        Long other$totalOutputTokens = other.getTotalOutputTokens();
        if (this$totalOutputTokens == null ? other$totalOutputTokens != null : !((Object)this$totalOutputTokens).equals(other$totalOutputTokens)) {
            return false;
        }
        String this$modelConfigId = this.getModelConfigId();
        String other$modelConfigId = other.getModelConfigId();
        if (this$modelConfigId == null ? other$modelConfigId != null : !this$modelConfigId.equals(other$modelConfigId)) {
            return false;
        }
        String this$modelName = this.getModelName();
        String other$modelName = other.getModelName();
        if (this$modelName == null ? other$modelName != null : !this$modelName.equals(other$modelName)) {
            return false;
        }
        String this$providerName = this.getProviderName();
        String other$providerName = other.getProviderName();
        if (this$providerName == null ? other$providerName != null : !this$providerName.equals(other$providerName)) {
            return false;
        }
        String this$providerType = this.getProviderType();
        String other$providerType = other.getProviderType();
        return !(this$providerType == null ? other$providerType != null : !this$providerType.equals(other$providerType));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ModelStatResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $callCount = this.getCallCount();
        result = result * 59 + ($callCount == null ? 43 : ((Object)$callCount).hashCode());
        Long $totalInputTokens = this.getTotalInputTokens();
        result = result * 59 + ($totalInputTokens == null ? 43 : ((Object)$totalInputTokens).hashCode());
        Long $totalOutputTokens = this.getTotalOutputTokens();
        result = result * 59 + ($totalOutputTokens == null ? 43 : ((Object)$totalOutputTokens).hashCode());
        String $modelConfigId = this.getModelConfigId();
        result = result * 59 + ($modelConfigId == null ? 43 : $modelConfigId.hashCode());
        String $modelName = this.getModelName();
        result = result * 59 + ($modelName == null ? 43 : $modelName.hashCode());
        String $providerName = this.getProviderName();
        result = result * 59 + ($providerName == null ? 43 : $providerName.hashCode());
        String $providerType = this.getProviderType();
        result = result * 59 + ($providerType == null ? 43 : $providerType.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ModelStatResponse(modelConfigId=" + this.getModelConfigId() + ", modelName=" + this.getModelName() + ", providerName=" + this.getProviderName() + ", providerType=" + this.getProviderType() + ", callCount=" + this.getCallCount() + ", totalInputTokens=" + this.getTotalInputTokens() + ", totalOutputTokens=" + this.getTotalOutputTokens() + ")";
    }
}

