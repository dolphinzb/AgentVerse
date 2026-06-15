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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 模型快捷添加服务。
 * <p>提供"一键添加模型"功能：自动按 userId 复用或创建 ModelProvider，再写入 ModelConfig。
 */
@Service
@RequiredArgsConstructor
public class ModelManagementService {

    private static final Logger log = LoggerFactory.getLogger(ModelManagementService.class);

    private final ModelProviderMapper modelProviderMapper;
    private final ModelConfigMapper modelConfigMapper;

    /**
     * 一键添加模型。
     */
    @Transactional(rollbackFor = {Exception.class})
    public ModelConfigResponse addModel(ModelAddRequest request) {
        Long userId = UserContext.getUserId();
        ProviderType providerType = ProviderType.fromCode(request.getProviderType());
        ModelProvider provider = findOrCreateProvider(request, providerType, userId);
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            clearOtherDefaults(userId);
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
        modelConfigMapper.insert(config);
        log.info("模型添加成功: providerType={}, model={}, configId={}",
                request.getProviderType(), request.getModelName(), config.getId());
        return convertToResponse(config, provider);
    }

    /**
     * 按 (userId, providerType, apiKey) 三元组查找已有 Provider，否则创建新的。
     */
    private ModelProvider findOrCreateProvider(ModelAddRequest request, ProviderType providerType, Long userId) {
        LambdaQueryWrapper<ModelProvider> query = new LambdaQueryWrapper<>();
        query.eq(BaseEntity::getCreatedBy, userId)
                .eq(ModelProvider::getProviderType, request.getProviderType());
        List<ModelProvider> existingProviders = modelProviderMapper.selectList(query);
        for (ModelProvider p : existingProviders) {
            try {
                String existingApiKey = AesEncryptUtil.decrypt(p.getApiKeyEncrypted());
                if (!existingApiKey.equals(request.getApiKey())) continue;
                log.info("复用已有 Provider: {}", p.getId());
                return p;
            } catch (Exception e) {
                log.warn("解密 Provider {} 的 API Key 失败", p.getId());
            }
        }
        ModelProvider provider = new ModelProvider();
        provider.setName(providerType.getDisplayName());
        provider.setProviderType(request.getProviderType());
        provider.setApiKeyEncrypted(AesEncryptUtil.encrypt(request.getApiKey()));
        provider.setBaseUrl(StringUtils.hasText(request.getBaseUrl()) ? request.getBaseUrl() : providerType.getDefaultBaseUrl());
        provider.setCustomHeaders(request.getCustomHeaders());
        provider.setStatus("active");
        provider.setCreatedBy(userId);
        modelProviderMapper.insert(provider);
        log.info("创建新 Provider: {}", provider.getId());
        return provider;
    }

    /**
     * 清除当前用户其他默认模型标志。
     */
    private void clearOtherDefaults(Long userId) {
        if (userId == null) {
            return;
        }
        LambdaUpdateWrapper<ModelConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BaseEntity::getCreatedBy, userId)
                .eq(ModelConfig::getIsDefault, 1)
                .set(ModelConfig::getIsDefault, 0);
        modelConfigMapper.update(null, updateWrapper);
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
        if (provider != null) {
            response.setProviderName(provider.getName());
            response.setProviderType(provider.getProviderType());
        }
        return response;
    }
}
