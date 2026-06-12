/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Generated;

public class ProviderCreateRequest {
    @NotBlank(message="\u4f9b\u5e94\u5546\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max=128)
    private @NotBlank(message="\u4f9b\u5e94\u5546\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") @Size(max=128) String name;
    @NotBlank(message="\u4f9b\u5e94\u5546\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u4f9b\u5e94\u5546\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a") String providerType;
    @NotBlank(message="API Key \u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="API Key \u4e0d\u80fd\u4e3a\u7a7a") String apiKey;
    private String baseUrl;
    private String customHeaders;

    @Generated
    public ProviderCreateRequest() {
    }

    @Generated
    public String getName() {
        return this.name;
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
    public void setName(String name) {
        this.name = name;
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProviderCreateRequest)) {
            return false;
        }
        ProviderCreateRequest other = (ProviderCreateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
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
        return !(this$customHeaders == null ? other$customHeaders != null : !this$customHeaders.equals(other$customHeaders));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ProviderCreateRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $providerType = this.getProviderType();
        result = result * 59 + ($providerType == null ? 43 : $providerType.hashCode());
        String $apiKey = this.getApiKey();
        result = result * 59 + ($apiKey == null ? 43 : $apiKey.hashCode());
        String $baseUrl = this.getBaseUrl();
        result = result * 59 + ($baseUrl == null ? 43 : $baseUrl.hashCode());
        String $customHeaders = this.getCustomHeaders();
        result = result * 59 + ($customHeaders == null ? 43 : $customHeaders.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ProviderCreateRequest(name=" + this.getName() + ", providerType=" + this.getProviderType() + ", apiKey=" + this.getApiKey() + ", baseUrl=" + this.getBaseUrl() + ", customHeaders=" + this.getCustomHeaders() + ")";
    }
}

