/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.entity;

import com.agentverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Generated;

@TableName(value="model_config")
public class ModelConfig
extends BaseEntity {
    @TableField(value="provider_id")
    private String providerId;
    @TableField(value="model_name")
    private String modelName;
    @TableField(value="display_name")
    private String displayName;
    @TableField(value="max_tokens")
    private Integer maxTokens;
    @TableField(value="temperature")
    private Double temperature;
    @TableField(value="top_p")
    private Double topP;
    @TableField(value="is_default")
    private Integer isDefault;
    @TableField(value="status")
    private String status;

    @Generated
    public ModelConfig() {
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
    public String getStatus() {
        return this.status;
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
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    @Generated
    public String toString() {
        return "ModelConfig(providerId=" + this.getProviderId() + ", modelName=" + this.getModelName() + ", displayName=" + this.getDisplayName() + ", maxTokens=" + this.getMaxTokens() + ", temperature=" + this.getTemperature() + ", topP=" + this.getTopP() + ", isDefault=" + this.getIsDefault() + ", status=" + this.getStatus() + ")";
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModelConfig)) {
            return false;
        }
        ModelConfig other = (ModelConfig)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
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
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !this$status.equals(other$status));
    }

    @Override
    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ModelConfig;
    }

    @Override
    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
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
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        return result;
    }
}

