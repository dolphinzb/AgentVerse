/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import lombok.Generated;

public class ProviderPresetResponse {
    private String providerType;
    private String displayName;
    private String description;
    private String icon;
    private String defaultBaseUrl;

    @Generated
    public ProviderPresetResponse() {
    }

    @Generated
    public String getProviderType() {
        return this.providerType;
    }

    @Generated
    public String getDisplayName() {
        return this.displayName;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public String getIcon() {
        return this.icon;
    }

    @Generated
    public String getDefaultBaseUrl() {
        return this.defaultBaseUrl;
    }

    @Generated
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @Generated
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Generated
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Generated
    public void setDefaultBaseUrl(String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProviderPresetResponse)) {
            return false;
        }
        ProviderPresetResponse other = (ProviderPresetResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$providerType = this.getProviderType();
        String other$providerType = other.getProviderType();
        if (this$providerType == null ? other$providerType != null : !this$providerType.equals(other$providerType)) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$icon = this.getIcon();
        String other$icon = other.getIcon();
        if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) {
            return false;
        }
        String this$defaultBaseUrl = this.getDefaultBaseUrl();
        String other$defaultBaseUrl = other.getDefaultBaseUrl();
        return !(this$defaultBaseUrl == null ? other$defaultBaseUrl != null : !this$defaultBaseUrl.equals(other$defaultBaseUrl));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ProviderPresetResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $providerType = this.getProviderType();
        result = result * 59 + ($providerType == null ? 43 : $providerType.hashCode());
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $icon = this.getIcon();
        result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
        String $defaultBaseUrl = this.getDefaultBaseUrl();
        result = result * 59 + ($defaultBaseUrl == null ? 43 : $defaultBaseUrl.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ProviderPresetResponse(providerType=" + this.getProviderType() + ", displayName=" + this.getDisplayName() + ", description=" + this.getDescription() + ", icon=" + this.getIcon() + ", defaultBaseUrl=" + this.getDefaultBaseUrl() + ")";
    }
}

