/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Generated;

public class ProviderUpdateRequest {
    @Size(max=128)
    private @Size(max=128) String name;
    private String apiKey;
    private String baseUrl;
    private String customHeaders;
    private String status;

    @Generated
    public ProviderUpdateRequest() {
    }

    @Generated
    public String getName() {
        return this.name;
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
    public String getStatus() {
        return this.status;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
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
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProviderUpdateRequest)) {
            return false;
        }
        ProviderUpdateRequest other = (ProviderUpdateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
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
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !this$status.equals(other$status));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ProviderUpdateRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $apiKey = this.getApiKey();
        result = result * 59 + ($apiKey == null ? 43 : $apiKey.hashCode());
        String $baseUrl = this.getBaseUrl();
        result = result * 59 + ($baseUrl == null ? 43 : $baseUrl.hashCode());
        String $customHeaders = this.getCustomHeaders();
        result = result * 59 + ($customHeaders == null ? 43 : $customHeaders.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ProviderUpdateRequest(name=" + this.getName() + ", apiKey=" + this.getApiKey() + ", baseUrl=" + this.getBaseUrl() + ", customHeaders=" + this.getCustomHeaders() + ", status=" + this.getStatus() + ")";
    }
}

