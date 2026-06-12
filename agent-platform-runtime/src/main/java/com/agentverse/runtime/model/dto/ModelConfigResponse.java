/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import java.time.LocalDateTime;
import lombok.Generated;

public class ModelConfigResponse {
    private String id;
    private String providerId;
    private String providerName;
    private String providerType;
    private String modelName;
    private String displayName;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Integer isDefault;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @Generated
    public ModelConfigResponse() {
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getProviderId() {
        return this.providerId;
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
    public String getModelName() {
        return this.modelName;
    }

    @Generated
    public String getDisplayName() {
        return this.displayName;
    }

    @Generated
    public Integer getMaxTokens() {
        return this.maxTokens;
    }

    @Generated
    public Double getTemperature() {
        return this.temperature;
    }

    @Generated
    public Double getTopP() {
        return this.topP;
    }

    @Generated
    public Integer getIsDefault() {
        return this.isDefault;
    }

    @Generated
    public String getStatus() {
        return this.status;
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
    public void setProviderId(String providerId) {
        this.providerId = providerId;
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
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Generated
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Generated
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Generated
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @Generated
    public void setTopP(Double topP) {
        this.topP = topP;
    }

    @Generated
    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    @Generated
    public void setStatus(String status) {
        this.status = status;
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
        if (!(o instanceof ModelConfigResponse)) {
            return false;
        }
        ModelConfigResponse other = (ModelConfigResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$maxTokens = this.getMaxTokens();
        Integer other$maxTokens = other.getMaxTokens();
        if (this$maxTokens == null ? other$maxTokens != null : !((Object)this$maxTokens).equals(other$maxTokens)) {
            return false;
        }
        Double this$temperature = this.getTemperature();
        Double other$temperature = other.getTemperature();
        if (this$temperature == null ? other$temperature != null : !((Object)this$temperature).equals(other$temperature)) {
            return false;
        }
        Double this$topP = this.getTopP();
        Double other$topP = other.getTopP();
        if (this$topP == null ? other$topP != null : !((Object)this$topP).equals(other$topP)) {
            return false;
        }
        Integer this$isDefault = this.getIsDefault();
        Integer other$isDefault = other.getIsDefault();
        if (this$isDefault == null ? other$isDefault != null : !((Object)this$isDefault).equals(other$isDefault)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$providerId = this.getProviderId();
        String other$providerId = other.getProviderId();
        if (this$providerId == null ? other$providerId != null : !this$providerId.equals(other$providerId)) {
            return false;
        }
        String this$providerName = this.getProviderName();
        String other$providerName = other.getProviderName();
        if (this$providerName == null ? other$providerName != null : !this$providerName.equals(other$providerName)) {
            return false;
        }
        String this$providerType = this.getProviderType();
        String other$providerType = other.getProviderType();
        if (this$providerType == null ? other$providerType != null : !this$providerType.equals(other$providerType)) {
            return false;
        }
        String this$modelName = this.getModelName();
        String other$modelName = other.getModelName();
        if (this$modelName == null ? other$modelName != null : !this$modelName.equals(other$modelName)) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
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
        return other instanceof ModelConfigResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $maxTokens = this.getMaxTokens();
        result = result * 59 + ($maxTokens == null ? 43 : ((Object)$maxTokens).hashCode());
        Double $temperature = this.getTemperature();
        result = result * 59 + ($temperature == null ? 43 : ((Object)$temperature).hashCode());
        Double $topP = this.getTopP();
        result = result * 59 + ($topP == null ? 43 : ((Object)$topP).hashCode());
        Integer $isDefault = this.getIsDefault();
        result = result * 59 + ($isDefault == null ? 43 : ((Object)$isDefault).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $providerId = this.getProviderId();
        result = result * 59 + ($providerId == null ? 43 : $providerId.hashCode());
        String $providerName = this.getProviderName();
        result = result * 59 + ($providerName == null ? 43 : $providerName.hashCode());
        String $providerType = this.getProviderType();
        result = result * 59 + ($providerType == null ? 43 : $providerType.hashCode());
        String $modelName = this.getModelName();
        result = result * 59 + ($modelName == null ? 43 : $modelName.hashCode());
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        LocalDateTime $updatedTime = this.getUpdatedTime();
        result = result * 59 + ($updatedTime == null ? 43 : ((Object)$updatedTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ModelConfigResponse(id=" + this.getId() + ", providerId=" + this.getProviderId() + ", providerName=" + this.getProviderName() + ", providerType=" + this.getProviderType() + ", modelName=" + this.getModelName() + ", displayName=" + this.getDisplayName() + ", maxTokens=" + this.getMaxTokens() + ", temperature=" + this.getTemperature() + ", topP=" + this.getTopP() + ", isDefault=" + this.getIsDefault() + ", status=" + this.getStatus() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ", updatedTime=" + String.valueOf(this.getUpdatedTime()) + ")";
    }
}

