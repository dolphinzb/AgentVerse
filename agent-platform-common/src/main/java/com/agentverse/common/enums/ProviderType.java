/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.enums;

import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;

public enum ProviderType {
    DASHSCOPE("dashscope", "DashScope", "https://dashscope.aliyuncs.com/compatible-mode/v1", List.of("qwen-max", "qwen-plus", "qwen-turbo", "qwen-long")),
    OPENAI("openai", "OpenAI", "https://api.openai.com/v1", List.of("gpt-4o", "gpt-4o-mini", "gpt-4-turbo", "gpt-3.5-turbo")),
    DEEPSEEK("deepseek", "DeepSeek", "https://api.deepseek.com", List.of("deepseek-chat", "deepseek-reasoner", "deepseek-v4-flash", "deepseek-v4-pro"));

    private final String code;
    private final String displayName;
    private final String defaultBaseUrl;
    private final List<String> recommendedModels;

    private ProviderType(String code, String displayName, String defaultBaseUrl, List<String> recommendedModels) {
        this.code = code;
        this.displayName = displayName;
        this.defaultBaseUrl = defaultBaseUrl;
        this.recommendedModels = recommendedModels;
    }

    public static ProviderType fromCode(String code) {
        return Arrays.stream(ProviderType.values()).filter(t -> t.code.equals(code)).findFirst().orElseThrow(() -> new BizException(ErrorCode.MODEL_PROVIDER_TYPE_UNSUPPORTED));
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
}

