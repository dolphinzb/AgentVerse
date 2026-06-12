/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.entity;

import com.agentverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Generated;

@TableName(value="model_provider")
public class ModelProvider
extends BaseEntity {
    @TableField(value="name")
    private String name;
    @TableField(value="provider_type")
    private String providerType;
    @TableField(value="api_key_encrypted")
    private String apiKeyEncrypted;
    @TableField(value="base_url")
    private String baseUrl;
    @TableField(value="custom_headers")
    private String customHeaders;
    @TableField(value="status")
    private String status;

    @Generated
    public ModelProvider() {
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
    public String getApiKeyEncrypted() {
        return this.apiKeyEncrypted;
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
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @Generated
    public void setApiKeyEncrypted(String apiKeyEncrypted) {
        this.apiKeyEncrypted = apiKeyEncrypted;
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

    @Override
    @Generated
    public String toString() {
        return "ModelProvider(name=" + this.getName() + ", providerType=" + this.getProviderType() + ", apiKeyEncrypted=" + this.getApiKeyEncrypted() + ", baseUrl=" + this.getBaseUrl() + ", customHeaders=" + this.getCustomHeaders() + ", status=" + this.getStatus() + ")";
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModelProvider)) {
            return false;
        }
        ModelProvider other = (ModelProvider)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
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
        String this$apiKeyEncrypted = this.getApiKeyEncrypted();
        String other$apiKeyEncrypted = other.getApiKeyEncrypted();
        if (this$apiKeyEncrypted == null ? other$apiKeyEncrypted != null : !this$apiKeyEncrypted.equals(other$apiKeyEncrypted)) {
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

    @Override
    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ModelProvider;
    }

    @Override
    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $providerType = this.getProviderType();
        result = result * 59 + ($providerType == null ? 43 : $providerType.hashCode());
        String $apiKeyEncrypted = this.getApiKeyEncrypted();
        result = result * 59 + ($apiKeyEncrypted == null ? 43 : $apiKeyEncrypted.hashCode());
        String $baseUrl = this.getBaseUrl();
        result = result * 59 + ($baseUrl == null ? 43 : $baseUrl.hashCode());
        String $customHeaders = this.getCustomHeaders();
        result = result * 59 + ($customHeaders == null ? 43 : $customHeaders.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        return result;
    }
}

