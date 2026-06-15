/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class ConnectionTestRequest {
    @NotBlank(message="\u4f9b\u5e94\u5546\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u4f9b\u5e94\u5546\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a") String providerType;
    @NotBlank(message="API Key \u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="API Key \u4e0d\u80fd\u4e3a\u7a7a") String apiKey;
    private String baseUrl;

    @Generated
    public ConnectionTestRequest() {
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConnectionTestRequest)) {
            return false;
        }
        ConnectionTestRequest other = (ConnectionTestRequest)o;
        if (!other.canEqual(this)) {
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
        return !(this$baseUrl == null ? other$baseUrl != null : !this$baseUrl.equals(other$baseUrl));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ConnectionTestRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $providerType = this.getProviderType();
        result = result * 59 + ($providerType == null ? 43 : $providerType.hashCode());
        String $apiKey = this.getApiKey();
        result = result * 59 + ($apiKey == null ? 43 : $apiKey.hashCode());
        String $baseUrl = this.getBaseUrl();
        result = result * 59 + ($baseUrl == null ? 43 : $baseUrl.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ConnectionTestRequest(providerType=" + this.getProviderType() + ", apiKey=" + this.getApiKey() + ", baseUrl=" + this.getBaseUrl() + ")";
    }
}

