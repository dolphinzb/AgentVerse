/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import java.time.LocalDateTime;
import lombok.Generated;

public class ProviderResponse {
    private String id;
    private String name;
    private String providerType;
    private String providerTypeName;
    private String baseUrl;
    private String customHeaders;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @Generated
    public ProviderResponse() {
    }

    @Generated
    public String getId() {
        return this.id;
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
    public String getProviderTypeName() {
        return this.providerTypeName;
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
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @Generated
    public void setProviderTypeName(String providerTypeName) {
        this.providerTypeName = providerTypeName;
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
        if (!(o instanceof ProviderResponse)) {
            return false;
        }
        ProviderResponse other = (ProviderResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
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
        String this$providerTypeName = this.getProviderTypeName();
        String other$providerTypeName = other.getProviderTypeName();
        if (this$providerTypeName == null ? other$providerTypeName != null : !this$providerTypeName.equals(other$providerTypeName)) {
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
        return other instanceof ProviderResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $providerType = this.getProviderType();
        result = result * 59 + ($providerType == null ? 43 : $providerType.hashCode());
        String $providerTypeName = this.getProviderTypeName();
        result = result * 59 + ($providerTypeName == null ? 43 : $providerTypeName.hashCode());
        String $baseUrl = this.getBaseUrl();
        result = result * 59 + ($baseUrl == null ? 43 : $baseUrl.hashCode());
        String $customHeaders = this.getCustomHeaders();
        result = result * 59 + ($customHeaders == null ? 43 : $customHeaders.hashCode());
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
        return "ProviderResponse(id=" + this.getId() + ", name=" + this.getName() + ", providerType=" + this.getProviderType() + ", providerTypeName=" + this.getProviderTypeName() + ", baseUrl=" + this.getBaseUrl() + ", customHeaders=" + this.getCustomHeaders() + ", status=" + this.getStatus() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ", updatedTime=" + String.valueOf(this.getUpdatedTime()) + ")";
    }
}

