/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Generated;

public class ModelConfigUpdateRequest {
    @Size(max = 128)
    private @Size(max = 128) String displayName;
    @Size(max = 128)
    private @Size(max = 128) String modelName;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Integer isDefault;
    private String status;

    @Generated
    public ModelConfigUpdateRequest() {
    }

    @Generated
    public String getDisplayName() {
        return this.displayName;
    }

    @Generated
    public String getModelName() {
        return this.modelName;
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
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Generated
    public void setModelName(String modelName) {
        this.modelName = modelName;
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModelConfigUpdateRequest)) {
            return false;
        }
        ModelConfigUpdateRequest other = (ModelConfigUpdateRequest) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$maxTokens = this.getMaxTokens();
        Integer other$maxTokens = other.getMaxTokens();
        if (this$maxTokens == null ? other$maxTokens != null : !((Object) this$maxTokens).equals(other$maxTokens)) {
            return false;
        }
        Double this$temperature = this.getTemperature();
        Double other$temperature = other.getTemperature();
        if (this$temperature == null ? other$temperature != null
                : !((Object) this$temperature).equals(other$temperature)) {
            return false;
        }
        Double this$topP = this.getTopP();
        Double other$topP = other.getTopP();
        if (this$topP == null ? other$topP != null : !((Object) this$topP).equals(other$topP)) {
            return false;
        }
        Integer this$isDefault = this.getIsDefault();
        Integer other$isDefault = other.getIsDefault();
        if (this$isDefault == null ? other$isDefault != null : !((Object) this$isDefault).equals(other$isDefault)) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !this$status.equals(other$status));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ModelConfigUpdateRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $maxTokens = this.getMaxTokens();
        result = result * 59 + ($maxTokens == null ? 43 : ((Object) $maxTokens).hashCode());
        Double $temperature = this.getTemperature();
        result = result * 59 + ($temperature == null ? 43 : ((Object) $temperature).hashCode());
        Double $topP = this.getTopP();
        result = result * 59 + ($topP == null ? 43 : ((Object) $topP).hashCode());
        Integer $isDefault = this.getIsDefault();
        result = result * 59 + ($isDefault == null ? 43 : ((Object) $isDefault).hashCode());
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ModelConfigUpdateRequest(displayName=" + this.getDisplayName() + ", maxTokens=" + this.getMaxTokens()
                + ", temperature=" + this.getTemperature() + ", topP=" + this.getTopP() + ", isDefault="
                + this.getIsDefault() + ", status=" + this.getStatus() + ")";
    }
}
