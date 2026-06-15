/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Generated;

public class ModelConfigCreateRequest {
    @NotBlank(message="\u4f9b\u5e94\u5546ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u4f9b\u5e94\u5546ID\u4e0d\u80fd\u4e3a\u7a7a") String providerId;
    @NotBlank(message="\u6a21\u578b\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max=128)
    private @NotBlank(message="\u6a21\u578b\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") @Size(max=128) String modelName;
    @Size(max=128)
    private @Size(max=128) String displayName;
    private Integer maxTokens = 4096;
    private Double temperature = 0.7;
    private Double topP = 0.9;
    private Integer isDefault = 0;

    @Generated
    public ModelConfigCreateRequest() {
    }

    @Generated
    public String getProviderId() {
        return this.providerId;
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
    public void setProviderId(String providerId) {
        this.providerId = providerId;
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModelConfigCreateRequest)) {
            return false;
        }
        ModelConfigCreateRequest other = (ModelConfigCreateRequest)o;
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
        String this$providerId = this.getProviderId();
        String other$providerId = other.getProviderId();
        if (this$providerId == null ? other$providerId != null : !this$providerId.equals(other$providerId)) {
            return false;
        }
        String this$modelName = this.getModelName();
        String other$modelName = other.getModelName();
        if (this$modelName == null ? other$modelName != null : !this$modelName.equals(other$modelName)) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        return !(this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ModelConfigCreateRequest;
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
        String $providerId = this.getProviderId();
        result = result * 59 + ($providerId == null ? 43 : $providerId.hashCode());
        String $modelName = this.getModelName();
        result = result * 59 + ($modelName == null ? 43 : $modelName.hashCode());
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ModelConfigCreateRequest(providerId=" + this.getProviderId() + ", modelName=" + this.getModelName() + ", displayName=" + this.getDisplayName() + ", maxTokens=" + this.getMaxTokens() + ", temperature=" + this.getTemperature() + ", topP=" + this.getTopP() + ", isDefault=" + this.getIsDefault() + ")";
    }
}

