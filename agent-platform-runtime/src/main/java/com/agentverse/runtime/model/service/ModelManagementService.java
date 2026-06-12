/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.service;

import com.agentverse.common.entity.BaseEntity;
import com.agentverse.common.enums.ProviderType;
import com.agentverse.common.security.UserContext;
import com.agentverse.common.util.AesEncryptUtil;
import com.agentverse.runtime.model.dto.ModelAddRequest;
import com.agentverse.runtime.model.dto.ModelConfigResponse;
import com.agentverse.runtime.model.entity.ModelConfig;
import com.agentverse.runtime.model.entity.ModelProvider;
import com.agentverse.runtime.model.mapper.ModelConfigMapper;
import com.agentverse.runtime.model.mapper.ModelProviderMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ModelManagementService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelManagementService.class);
    private final ModelProviderMapper modelProviderMapper;
    private final ModelConfigMapper modelConfigMapper;

    @Transactional(rollbackFor={Exception.class})
    public ModelConfigResponse addModel(ModelAddRequest request) {
        Long userId = UserContext.getUserId();
        ProviderType providerType = ProviderType.fromCode(request.getProviderType());
        ModelProvider provider = this.findOrCreateProvider(request, providerType, userId);
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            this.clearOtherDefaults(userId);
        }
        ModelConfig config = new ModelConfig();
        config.setProviderId(provider.getId());
        config.setModelName(request.getModelName());
        config.setDisplayName(request.getDisplayName());
        config.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 4096);
        config.setTemperature(request.getTemperature() != null ? request.getTemperature() : 0.7);
        config.setTopP(request.getTopP() != null ? request.getTopP() : 0.9);
        config.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        config.setStatus("active");
        config.setCreatedBy(userId);
        this.modelConfigMapper.insert(config);
        log.info("\u6a21\u578b\u6dfb\u52a0\u6210\u529f: providerType={}, model={}, configId={}", new Object[]{request.getProviderType(), request.getModelName(), config.getId()});
        return this.convertToResponse(config, provider);
    }

    private ModelProvider findOrCreateProvider(ModelAddRequest request, ProviderType providerType, Long userId) {
        LambdaQueryWrapper query = new LambdaQueryWrapper();
        ((LambdaQueryWrapper)query.eq(BaseEntity::getCreatedBy, (Object)userId)).eq(ModelProvider::getProviderType, (Object)request.getProviderType());
        List existingProviders = this.modelProviderMapper.selectList((Wrapper)query);
        for (ModelProvider p : existingProviders) {
            try {
                String existingApiKey = AesEncryptUtil.decrypt(p.getApiKeyEncrypted());
                if (!existingApiKey.equals(request.getApiKey())) continue;
                log.info("\u590d\u7528\u5df2\u6709 Provider: {}", (Object)p.getId());
                return p;
            }
            catch (Exception e) {
                log.warn("\u89e3\u5bc6 Provider {} \u7684 API Key \u5931\u8d25", (Object)p.getId());
            }
        }
        ModelProvider provider = new ModelProvider();
        provider.setName(providerType.getDisplayName());
        provider.setProviderType(request.getProviderType());
        provider.setApiKeyEncrypted(AesEncryptUtil.encrypt(request.getApiKey()));
        provider.setBaseUrl(StringUtils.hasText((String)request.getBaseUrl()) ? request.getBaseUrl() : providerType.getDefaultBaseUrl());
        provider.setCustomHeaders(request.getCustomHeaders());
        provider.setStatus("active");
        provider.setCreatedBy(userId);
        this.modelProviderMapper.insert(provider);
        log.info("\u521b\u5efa\u65b0 Provider: {}", (Object)provider.getId());
        return provider;
    }

    private void clearOtherDefaults(Long userId) {
        if (userId == null) {
            return;
        }
        LambdaUpdateWrapper updateWrapper = new LambdaUpdateWrapper();
        ((LambdaUpdateWrapper)((LambdaUpdateWrapper)updateWrapper.eq(BaseEntity::getCreatedBy, (Object)userId)).eq(ModelConfig::getIsDefault, (Object)1)).set(ModelConfig::getIsDefault, (Object)0);
        this.modelConfigMapper.update(null, (Wrapper)updateWrapper);
    }

    private ModelConfigResponse convertToResponse(ModelConfig config, ModelProvider provider) {
        ModelConfigResponse response = new ModelConfigResponse();
        response.setId(config.getId());
        response.setProviderId(config.getProviderId());
        response.setModelName(config.getModelName());
        response.setDisplayName(config.getDisplayName());
        response.setMaxTokens(config.getMaxTokens());
        response.setTemperature(config.getTemperature());
        response.setTopP(config.getTopP());
        response.setIsDefault(config.getIsDefault());
        response.setStatus(config.getStatus());
        response.setCreatedTime(config.getCreatedTime());
        response.setUpdatedTime(config.getUpdatedTime());
        if (provider != null) {
            response.setProviderName(provider.getName());
            response.setProviderType(provider.getProviderType());
        }
        return response;
    }

    @Generated
    public ModelManagementService(ModelProviderMapper modelProviderMapper, ModelConfigMapper modelConfigMapper) {
        this.modelProviderMapper = modelProviderMapper;
        this.modelConfigMapper = modelConfigMapper;
    }
}

