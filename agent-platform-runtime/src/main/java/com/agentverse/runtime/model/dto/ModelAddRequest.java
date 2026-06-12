/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Generated;

public class ModelAddRequest {
    @NotBlank(message="\u4f9b\u5e94\u5546\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u4f9b\u5e94\u5546\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a") String providerType;
    @NotBlank(message="API Key \u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="API Key \u4e0d\u80fd\u4e3a\u7a7a") String apiKey;
    private String baseUrl;
    private String customHeaders;
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
    public ModelAddRequest() {
    }

    @Generated
    public String getProviderType() {
        return this.providerType;
    }

    @Generated
    public String getApiKey() {
        return this.apiKey;
    }

    @Generated
    public String getBaseUrl() {
        return this.baseUrl;
    }

    @Generated
    public String getCustomHeaders() {
        return this.customHeaders;
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
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @Generated
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Generated
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Generated
    public void setCustomHeaders(String customHeaders) {
        this.customHeaders = customHeaders;
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
        if (!(o instanceof ModelAddRequest)) {
            return false;
        }
        ModelAddRequest other = (ModelAddRequest)o;
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
        String this$providerType = this.getProviderType();
        String other$providerType = other.getProviderType();
        if (this$providerType == null ? other$providerType != null : !this$providerType.equals(other$providerType)) {
            return false;
        }
        String this$apiKey = this.getApiKey();
        String other$apiKey = other.getApiKey();
        if (this$apiKey == null ? other$apiKey != null : !this$apiKey.equals(other$apiKey)) {
            return false;
        }
        String this$baseUrl = this.getBaseUrl();
        String other$baseUrl = other.getBaseUrl();
        if (this$baseUrl == null ? other$baseUrl != null : !this$baseUrl.equals(other$baseUrl)) {
            return false;
        }
        String this$customHeaders = this.getCustomHeaders();
        String other$customHeaders = other.getCustomHeaders();
        if (this$customHeaders == null ? other$customHeaders != null : !this$customHeaders.equals(other$customHeaders)) {
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
        return other instanceof ModelAddRequest;
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
        String $providerType = this.getProviderType();
        result = result * 59 + ($providerType == null ? 43 : $providerType.hashCode());
        String $apiKey = this.getApiKey();
        result = result * 59 + ($apiKey == null ? 43 : $apiKey.hashCode());
        String $baseUrl = this.getBaseUrl();
        result = result * 59 + ($baseUrl == null ? 43 : $baseUrl.hashCode());
        String $customHeaders = this.getCustomHeaders();
        result = result * 59 + ($customHeaders == null ? 43 : $customHeaders.hashCode());
        String $modelName = this.getModelName();
        result = result * 59 + ($modelName == null ? 43 : $modelName.hashCode());
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ModelAddRequest(providerType=" + this.getProviderType() + ", apiKey=" + this.getApiKey() + ", baseUrl=" + this.getBaseUrl() + ", customHeaders=" + this.getCustomHeaders() + ", modelName=" + this.getModelName() + ", displayName=" + this.getDisplayName() + ", maxTokens=" + this.getMaxTokens() + ", temperature=" + this.getTemperature() + ", topP=" + this.getTopP() + ", isDefault=" + this.getIsDefault() + ")";
    }
}

