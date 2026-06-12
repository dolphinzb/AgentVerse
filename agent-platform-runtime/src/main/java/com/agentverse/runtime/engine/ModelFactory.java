/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.common.enums.ProviderType;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.util.AesEncryptUtil;
import com.agentverse.runtime.model.entity.ModelConfig;
import com.agentverse.runtime.model.entity.ModelProvider;
import com.agentverse.runtime.model.mapper.ModelConfigMapper;
import com.agentverse.runtime.model.mapper.ModelProviderMapper;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.OpenAIChatModel;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ModelFactory {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelFactory.class);
    private final ModelConfigMapper modelConfigMapper;
    private final ModelProviderMapper modelProviderMapper;
    private final ConcurrentHashMap<String, ChatModelBase> modelCache = new ConcurrentHashMap();

    public ChatModelBase getModel(String modelConfigId) {
        return this.modelCache.computeIfAbsent(modelConfigId, this::buildModel);
    }

    public void evictModel(String modelConfigId) {
        this.modelCache.remove(modelConfigId);
        log.info("Evicted model from cache: modelConfigId={}", (Object)modelConfigId);
    }

    public void evictByProvider(String providerId) {
        this.modelCache.keySet().removeIf(configId -> {
            ModelConfig config = (ModelConfig)this.modelConfigMapper.selectById((Serializable)((Object)configId));
            return config != null && providerId.equals(config.getProviderId());
        });
        log.info("Evicted models by provider from cache: providerId={}", (Object)providerId);
    }

    public void evictAll() {
        this.modelCache.clear();
        log.info("Evicted all models from cache");
    }

    private ChatModelBase buildModel(String modelConfigId) {
        log.info("Building ChatModel for modelConfigId: {}", (Object)modelConfigId);
        ModelConfig config = (ModelConfig)this.modelConfigMapper.selectById((Serializable)((Object)modelConfigId));
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)config.getProviderId()));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        if ("disabled".equals(provider.getStatus())) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_DISABLED);
        }
        String apiKey = AesEncryptUtil.decrypt(provider.getApiKeyEncrypted());
        ProviderType providerType = ProviderType.fromCode(provider.getProviderType());
        GenerateOptions options = GenerateOptions.builder().temperature(config.getTemperature()).maxTokens(config.getMaxTokens()).topP(config.getTopP()).build();
        String baseUrl = provider.getBaseUrl() != null ? provider.getBaseUrl() : providerType.getDefaultBaseUrl();
        DashScopeChatModel chatModel = switch (providerType.getCode()) {
            case "dashscope" -> DashScopeChatModel.builder().apiKey(apiKey).modelName(config.getModelName()).baseUrl(baseUrl).stream(true).defaultOptions(options).build();
            case "openai", "deepseek" -> OpenAIChatModel.builder().apiKey(apiKey).modelName(config.getModelName()).baseUrl(baseUrl).stream(true).generateOptions(options).build();
            default -> throw new BizException(ErrorCode.MODEL_PROVIDER_TYPE_UNSUPPORTED);
        };
        log.info("ChatModel built successfully: providerType={}, modelName={}", (Object)providerType.getCode(), (Object)config.getModelName());
        return chatModel;
    }

    @Generated
    public ModelFactory(ModelConfigMapper modelConfigMapper, ModelProviderMapper modelProviderMapper) {
        this.modelConfigMapper = modelConfigMapper;
        this.modelProviderMapper = modelProviderMapper;
    }
}

