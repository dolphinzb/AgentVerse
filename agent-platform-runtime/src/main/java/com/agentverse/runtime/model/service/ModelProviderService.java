/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.service;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.agentverse.common.config.ProviderPresets;
import com.agentverse.common.entity.BaseEntity;
import com.agentverse.common.enums.ProviderType;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.common.util.AesEncryptUtil;
import com.agentverse.runtime.model.dto.ConnectionTestRequest;
import com.agentverse.runtime.model.dto.ConnectionTestResult;
import com.agentverse.runtime.model.dto.ProviderCreateRequest;
import com.agentverse.runtime.model.dto.ProviderPresetResponse;
import com.agentverse.runtime.model.dto.ProviderResponse;
import com.agentverse.runtime.model.dto.ProviderTypeResponse;
import com.agentverse.runtime.model.dto.ProviderUpdateRequest;
import com.agentverse.runtime.model.entity.ModelProvider;
import com.agentverse.runtime.model.mapper.ModelProviderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Generated;

@Service
public class ModelProviderService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelProviderService.class);
    private final ModelProviderMapper modelProviderMapper;

    @Transactional(rollbackFor = { Exception.class })
    public ProviderResponse createProvider(ProviderCreateRequest request) {
        log.info("创建模型供应商: {}", (Object) request.getName());
        ProviderType providerType = ProviderType.fromCode(request.getProviderType());
        ModelProvider provider = new ModelProvider();
        provider.setName(request.getName());
        provider.setProviderType(request.getProviderType());
        try {
            provider.setApiKeyEncrypted(AesEncryptUtil.encrypt(request.getApiKey()));
        } catch (Exception e) {
            log.error("API Key 加密失败", (Throwable) e);
            throw new BizException(ErrorCode.API_KEY_ENCRYPTION_ERROR);
        }
        provider.setBaseUrl(StringUtils.hasText((String) request.getBaseUrl()) ? request.getBaseUrl()
                : providerType.getDefaultBaseUrl());
        provider.setCustomHeaders(request.getCustomHeaders());
        provider.setStatus("active");
        provider.setCreatedBy(UserContext.getUserId());
        this.modelProviderMapper.insert(provider);
        log.info("模型供应商创建成功: {}", (Object) provider.getId());
        return this.convertToResponse(provider);
    }

    public ProviderResponse getProviderById(String id) {
        log.info("查询模型供应商详情: {}", (Object) id);
        ModelProvider provider = (ModelProvider) this.modelProviderMapper.selectById((Serializable) ((Object) id));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        this.checkDataAccess(provider);
        return this.convertToResponse(provider);
    }

    public Page<ProviderResponse> listProviders(Integer page, Integer pageSize, String status) {
        Long userId;
        log.info("查询模型供应商列表: page={}, pageSize={}, status={}", page,
                pageSize, status);
        Page<ModelProvider> pageParam = new Page<>(page.longValue(), pageSize.longValue());
        LambdaQueryWrapper<ModelProvider> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(ModelProvider::getStatus, status);
        }
        if (!UserContext.isAdmin() && (userId = UserContext.getUserId()) != null) {
            queryWrapper.eq(BaseEntity::getCreatedBy, userId);
        }
        queryWrapper.orderByDesc(BaseEntity::getCreatedTime);
        Page<ModelProvider> result = this.modelProviderMapper.selectPage(pageParam, queryWrapper);
        Page<ProviderResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(this::convertToResponse).collect(Collectors.toList()));
        return responsePage;
    }

    @Transactional(rollbackFor = { Exception.class })
    public ProviderResponse updateProvider(String id, ProviderUpdateRequest request) {
        log.info("更新模型供应商: {}", (Object) id);
        ModelProvider provider = (ModelProvider) this.modelProviderMapper.selectById((Serializable) ((Object) id));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        this.checkDataAccess(provider);
        if (StringUtils.hasText((String) request.getName())) {
            provider.setName(request.getName());
        }
        if (StringUtils.hasText((String) request.getApiKey())) {
            try {
                provider.setApiKeyEncrypted(AesEncryptUtil.encrypt(request.getApiKey()));
            } catch (Exception e) {
                log.error("API Key 加密失败", (Throwable) e);
                throw new BizException(ErrorCode.API_KEY_ENCRYPTION_ERROR);
            }
        }
        if (StringUtils.hasText((String) request.getBaseUrl())) {
            provider.setBaseUrl(request.getBaseUrl());
        }
        if (request.getCustomHeaders() != null) {
            provider.setCustomHeaders(request.getCustomHeaders());
        }
        if (StringUtils.hasText((String) request.getStatus())) {
            provider.setStatus(request.getStatus());
        }
        this.modelProviderMapper.updateById(provider);
        log.info("模型供应商更新成功: {}", (Object) id);
        return this.convertToResponse(provider);
    }

    @Transactional(rollbackFor = { Exception.class })
    public void deleteProvider(String id) {
        log.info("删除模型供应商: {}", (Object) id);
        ModelProvider provider = (ModelProvider) this.modelProviderMapper.selectById((Serializable) ((Object) id));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        this.checkDataAccess(provider);
        long configCount = this.countModelConfigsByProviderId(id);
        if (configCount > 0L) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_IN_USE);
        }
        this.modelProviderMapper.deleteById((Serializable) ((Object) id));
        log.info("模型供应商删除成功: {}", (Object) id);
    }

    public ConnectionTestResult testConnection(String id) {
        String apiKey;
        log.info("测试模型供应商连接: {}", (Object) id);
        ModelProvider provider = (ModelProvider) this.modelProviderMapper.selectById((Serializable) ((Object) id));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        this.checkDataAccess(provider);
        try {
            apiKey = AesEncryptUtil.decrypt(provider.getApiKeyEncrypted());
        } catch (Exception e) {
            log.error("API Key 解密失败", (Throwable) e);
            return ConnectionTestResult.fail("API Key 解密失败");
        }
        ProviderType providerType = ProviderType.fromCode(provider.getProviderType());
        String baseUrl = StringUtils.hasText((String) provider.getBaseUrl()) ? provider.getBaseUrl()
                : providerType.getDefaultBaseUrl();
        return this.doTestConnection(providerType, baseUrl, apiKey, null);
    }

    public ConnectionTestResult testConnectionDirect(ConnectionTestRequest request) {
        log.info("直接测试连接: providerType={}, modelName={}", (Object) request.getProviderType(),
                (Object) request.getModelName());
        ProviderType providerType = ProviderType.fromCode(request.getProviderType());
        String baseUrl = StringUtils.hasText((String) request.getBaseUrl()) ? request.getBaseUrl()
                : providerType.getDefaultBaseUrl();
        return this.doTestConnection(providerType, baseUrl, request.getApiKey(), request.getModelName());
    }

    private ConnectionTestResult doTestConnection(ProviderType providerType, String baseUrl, String apiKey,
            String modelName) {
        try {
            return switch (providerType) {
                case DASHSCOPE -> this.testDashScopeConnection(baseUrl, apiKey, modelName);
                case OPENAI, DEEPSEEK -> this.testOpenAICompatibleConnection(baseUrl, apiKey, modelName);
            };
        } catch (Exception e) {
            log.error("连接测试异常: providerType={}, error={}", providerType, e.getMessage());
            return ConnectionTestResult.fail("连接测试失败: " + e.getMessage());
        }
    }

    public List<ProviderTypeResponse> getTypes() {
        return Arrays.stream(ProviderType.values()).map(type -> {
            ProviderTypeResponse response = new ProviderTypeResponse();
            response.setCode(type.getCode());
            response.setDisplayName(type.getDisplayName());
            response.setDefaultBaseUrl(type.getDefaultBaseUrl());
            response.setRecommendedModels(type.getRecommendedModels());
            return response;
        }).collect(Collectors.toList());
    }

    public List<ProviderPresetResponse> getPresets() {
        return ProviderPresets.PRESETS.stream().map(preset -> {
            ProviderPresetResponse response = new ProviderPresetResponse();
            response.setProviderType(preset.getProviderType().getCode());
            response.setDisplayName(preset.getDisplayName());
            response.setDescription(preset.getDescription());
            response.setIcon(preset.getIcon());
            response.setDefaultBaseUrl(preset.getProviderType().getDefaultBaseUrl());
            return response;
        }).collect(Collectors.toList());
    }

    private void checkDataAccess(ModelProvider provider) {
        if (UserContext.isAdmin()) {
            return;
        }
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null || provider.getCreatedBy() == null
                || !provider.getCreatedBy().equals(currentUserId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private long countModelConfigsByProviderId(String providerId) {
        return this.modelProviderMapper.countModelConfigsByProviderId(providerId);
    }

    private ConnectionTestResult testDashScopeConnection(String baseUrl, String apiKey, String modelName) {
        try {
            String url = baseUrl + "/chat/completions";
            // 使用传入的模型名称，若未指定则使用默认值 qwen-turbo
            String model = StringUtils.hasText(modelName) ? modelName : "qwen-turbo";
            String requestBody = "{\n    \"model\": \"" + model
                    + "\",\n    \"messages\": [{\"role\": \"user\", \"content\": \"hi\"}],\n    \"max_tokens\": 1\n}\n";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(15L)).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ConnectionTestResult.ok("DashScope 连接成功");
            }
            return ConnectionTestResult.fail("DashScope 返回状态码: " + response.statusCode());
        } catch (Exception e) {
            return ConnectionTestResult.fail("DashScope 连接失败: " + e.getMessage());
        }
    }

    private ConnectionTestResult testOpenAICompatibleConnection(String baseUrl, String apiKey, String modelName) {
        try {
            // 使用 chat/completions 接口测试，与 DashScope 保持一致
            String url = baseUrl + "/chat/completions";
            // 使用传入的模型名称，若未指定则使用默认值 gpt-3.5-turbo
            String model = StringUtils.hasText(modelName) ? modelName : "gpt-3.5-turbo";
            String requestBody = "{\n    \"model\": \"" + model
                    + "\",\n    \"messages\": [{\"role\": \"user\", \"content\": \"hi\"}],\n    \"max_tokens\": 1\n}\n";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(15L)).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ConnectionTestResult.ok("连接成功");
            }
            return ConnectionTestResult.fail("API 返回状态码: " + response.statusCode());
        } catch (Exception e) {
            return ConnectionTestResult.fail("连接失败: " + e.getMessage());
        }
    }

    private ProviderResponse convertToResponse(ModelProvider provider) {
        ProviderResponse response = new ProviderResponse();
        response.setId(provider.getId());
        response.setName(provider.getName());
        response.setProviderType(provider.getProviderType());
        try {
            ProviderType providerType = ProviderType.fromCode(provider.getProviderType());
            response.setProviderTypeName(providerType.getDisplayName());
        } catch (BizException e) {
            response.setProviderTypeName(provider.getProviderType());
        }
        response.setBaseUrl(provider.getBaseUrl());
        response.setCustomHeaders(provider.getCustomHeaders());
        response.setStatus(provider.getStatus());
        response.setCreatedTime(provider.getCreatedTime());
        response.setUpdatedTime(provider.getUpdatedTime());
        return response;
    }

    @Generated
    public ModelProviderService(ModelProviderMapper modelProviderMapper) {
        this.modelProviderMapper = modelProviderMapper;
    }
}
