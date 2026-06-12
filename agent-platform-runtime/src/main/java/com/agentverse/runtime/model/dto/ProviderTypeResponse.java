/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import java.util.List;
import lombok.Generated;

public class ProviderTypeResponse {
    private String code;
    private String displayName;
    private String defaultBaseUrl;
    private List<String> recommendedModels;

    @Generated
    public ProviderTypeResponse() {
    }

    @Generated
    public String getCode() {
        return this.code;
    }

    @Generated
    public String getDisplayName() {
        return this.displayName;
    }

    @Generated
    public String getDefaultBaseUrl() {
        return this.defaultBaseUrl;
    }

    @Generated
    public List<String> getRecommendedModels() {
        return this.recommendedModels;
    }

    @Generated
    public void setCode(String code) {
        this.code = code;
    }

    @Generated
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Generated
    public void setDefaultBaseUrl(String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    @Generated
    public void setRecommendedModels(List<String> recommendedModels) {
        this.recommendedModels = recommendedModels;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProviderTypeResponse)) {
            return false;
        }
        ProviderTypeResponse other = (ProviderTypeResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$code = this.getCode();
        String other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
            return false;
        }
        String this$defaultBaseUrl = this.getDefaultBaseUrl();
        String other$defaultBaseUrl = other.getDefaultBaseUrl();
        if (this$defaultBaseUrl == null ? other$defaultBaseUrl != null : !this$defaultBaseUrl.equals(other$defaultBaseUrl)) {
            return false;
        }
        List<String> this$recommendedModels = this.getRecommendedModels();
        List<String> other$recommendedModels = other.getRecommendedModels();
        return !(this$recommendedModels == null ? other$recommendedModels != null : !((Object)this$recommendedModels).equals(other$recommendedModels));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ProviderTypeResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        String $defaultBaseUrl = this.getDefaultBaseUrl();
        result = result * 59 + ($defaultBaseUrl == null ? 43 : $defaultBaseUrl.hashCode());
        List<String> $recommendedModels = this.getRecommendedModels();
        result = result * 59 + ($recommendedModels == null ? 43 : ((Object)$recommendedModels).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ProviderTypeResponse(code=" + this.getCode() + ", displayName=" + this.getDisplayName() + ", defaultBaseUrl=" + this.getDefaultBaseUrl() + ", recommendedModels=" + String.valueOf(this.getRecommendedModels()) + ")";
    }
}

