/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.service;

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
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ModelProviderService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelProviderService.class);
    private final ModelProviderMapper modelProviderMapper;

    @Transactional(rollbackFor={Exception.class})
    public ProviderResponse createProvider(ProviderCreateRequest request) {
        log.info("\u521b\u5efa\u6a21\u578b\u4f9b\u5e94\u5546: {}", (Object)request.getName());
        ProviderType providerType = ProviderType.fromCode(request.getProviderType());
        ModelProvider provider = new ModelProvider();
        provider.setName(request.getName());
        provider.setProviderType(request.getProviderType());
        try {
            provider.setApiKeyEncrypted(AesEncryptUtil.encrypt(request.getApiKey()));
        }
        catch (Exception e) {
            log.error("API Key \u52a0\u5bc6\u5931\u8d25", (Throwable)e);
            throw new BizException(ErrorCode.API_KEY_ENCRYPTION_ERROR);
        }
        provider.setBaseUrl(StringUtils.hasText((String)request.getBaseUrl()) ? request.getBaseUrl() : providerType.getDefaultBaseUrl());
        provider.setCustomHeaders(request.getCustomHeaders());
        provider.setStatus("active");
        provider.setCreatedBy(UserContext.getUserId());
        this.modelProviderMapper.insert(provider);
        log.info("\u6a21\u578b\u4f9b\u5e94\u5546\u521b\u5efa\u6210\u529f: {}", (Object)provider.getId());
        return this.convertToResponse(provider);
    }

    public ProviderResponse getProviderById(String id) {
        log.info("\u67e5\u8be2\u6a21\u578b\u4f9b\u5e94\u5546: {}", (Object)id);
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)id));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        this.checkDataAccess(provider);
        return this.convertToResponse(provider);
    }

    public Page<ProviderResponse> listProviders(Integer page, Integer pageSize, String status) {
        Long userId;
        log.info("\u67e5\u8be2\u6a21\u578b\u4f9b\u5e94\u5546\u5217\u8868: page={}, pageSize={}, status={}", new Object[]{page, pageSize, status});
        Page pageParam = new Page((long)page.intValue(), (long)pageSize.intValue());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        if (StringUtils.hasText((String)status)) {
            queryWrapper.eq(ModelProvider::getStatus, (Object)status);
        }
        if (!UserContext.isAdmin() && (userId = UserContext.getUserId()) != null) {
            queryWrapper.eq(BaseEntity::getCreatedBy, (Object)userId);
        }
        queryWrapper.orderByDesc(BaseEntity::getCreatedTime);
        Page result = (Page)this.modelProviderMapper.selectPage((IPage)pageParam, (Wrapper)queryWrapper);
        Page responsePage = new Page(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(this::convertToResponse).collect(Collectors.toList()));
        return responsePage;
    }

    @Transactional(rollbackFor={Exception.class})
    public ProviderResponse updateProvider(String id, ProviderUpdateRequest request) {
        log.info("\u66f4\u65b0\u6a21\u578b\u4f9b\u5e94\u5546: {}", (Object)id);
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)id));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        this.checkDataAccess(provider);
        if (StringUtils.hasText((String)request.getName())) {
            provider.setName(request.getName());
        }
        if (StringUtils.hasText((String)request.getApiKey())) {
            try {
                provider.setApiKeyEncrypted(AesEncryptUtil.encrypt(request.getApiKey()));
            }
            catch (Exception e) {
                log.error("API Key \u52a0\u5bc6\u5931\u8d25", (Throwable)e);
                throw new BizException(ErrorCode.API_KEY_ENCRYPTION_ERROR);
            }
        }
        if (StringUtils.hasText((String)request.getBaseUrl())) {
            provider.setBaseUrl(request.getBaseUrl());
        }
        if (request.getCustomHeaders() != null) {
            provider.setCustomHeaders(request.getCustomHeaders());
        }
        if (StringUtils.hasText((String)request.getStatus())) {
            provider.setStatus(request.getStatus());
        }
        this.modelProviderMapper.updateById(provider);
        log.info("\u6a21\u578b\u4f9b\u5e94\u5546\u66f4\u65b0\u6210\u529f: {}", (Object)id);
        return this.convertToResponse(provider);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteProvider(String id) {
        log.info("\u5220\u9664\u6a21\u578b\u4f9b\u5e94\u5546: {}", (Object)id);
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)id));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        this.checkDataAccess(provider);
        long configCount = this.countModelConfigsByProviderId(id);
        if (configCount > 0L) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_IN_USE);
        }
        this.modelProviderMapper.deleteById((Serializable)((Object)id));
        log.info("\u6a21\u578b\u4f9b\u5e94\u5546\u5220\u9664\u6210\u529f: {}", (Object)id);
    }

    public ConnectionTestResult testConnection(String id) {
        String apiKey;
        log.info("\u6d4b\u8bd5\u6a21\u578b\u4f9b\u5e94\u5546\u8fde\u63a5: {}", (Object)id);
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)id));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        this.checkDataAccess(provider);
        try {
            apiKey = AesEncryptUtil.decrypt(provider.getApiKeyEncrypted());
        }
        catch (Exception e) {
            log.error("API Key \u89e3\u5bc6\u5931\u8d25", (Throwable)e);
            return ConnectionTestResult.fail("API Key \u89e3\u5bc6\u5931\u8d25");
        }
        ProviderType providerType = ProviderType.fromCode(provider.getProviderType());
        String baseUrl = StringUtils.hasText((String)provider.getBaseUrl()) ? provider.getBaseUrl() : providerType.getDefaultBaseUrl();
        return this.doTestConnection(providerType, baseUrl, apiKey);
    }

    public ConnectionTestResult testConnectionDirect(ConnectionTestRequest request) {
        log.info("\u76f4\u63a5\u6d4b\u8bd5\u8fde\u63a5: providerType={}", (Object)request.getProviderType());
        ProviderType providerType = ProviderType.fromCode(request.getProviderType());
        String baseUrl = StringUtils.hasText((String)request.getBaseUrl()) ? request.getBaseUrl() : providerType.getDefaultBaseUrl();
        return this.doTestConnection(providerType, baseUrl, request.getApiKey());
    }

    private ConnectionTestResult doTestConnection(ProviderType providerType, String baseUrl, String apiKey) {
        try {
            return switch (providerType) {
                default -> throw new IncompatibleClassChangeError();
                case ProviderType.DASHSCOPE -> this.testDashScopeConnection(baseUrl, apiKey);
                case ProviderType.OPENAI, ProviderType.DEEPSEEK -> this.testOpenAICompatibleConnection(baseUrl, apiKey);
            };
        }
        catch (Exception e) {
            log.error("\u8fde\u63a5\u6d4b\u8bd5\u5f02\u5e38: providerType={}, error={}", (Object)providerType, (Object)e.getMessage());
            return ConnectionTestResult.fail("\u8fde\u63a5\u6d4b\u8bd5\u5931\u8d25: " + e.getMessage());
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
        if (currentUserId == null || provider.getCreatedBy() == null || !provider.getCreatedBy().equals(currentUserId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private long countModelConfigsByProviderId(String providerId) {
        return this.modelProviderMapper.countModelConfigsByProviderId(providerId);
    }

    private ConnectionTestResult testDashScopeConnection(String baseUrl, String apiKey) {
        try {
            String url = baseUrl + "/chat/completions";
            String requestBody = "{\n    \"model\": \"qwen-turbo\",\n    \"messages\": [{\"role\": \"user\", \"content\": \"hi\"}],\n    \"max_tokens\": 1\n}\n";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey).timeout(Duration.ofSeconds(15L)).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ConnectionTestResult.ok("DashScope \u8fde\u63a5\u6210\u529f");
            }
            return ConnectionTestResult.fail("DashScope \u8fd4\u56de\u72b6\u6001\u7801: " + response.statusCode());
        }
        catch (Exception e) {
            return ConnectionTestResult.fail("DashScope \u8fde\u63a5\u5931\u8d25: " + e.getMessage());
        }
    }

    private ConnectionTestResult testOpenAICompatibleConnection(String baseUrl, String apiKey) {
        try {
            String url = baseUrl + "/models";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Authorization", "Bearer " + apiKey).timeout(Duration.ofSeconds(15L)).GET().build();
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ConnectionTestResult.ok("\u8fde\u63a5\u6210\u529f\uff0c\u6a21\u578b\u5217\u8868\u83b7\u53d6\u6b63\u5e38");
            }
            return ConnectionTestResult.fail("API \u8fd4\u56de\u72b6\u6001\u7801: " + response.statusCode());
        }
        catch (Exception e) {
            return ConnectionTestResult.fail("\u8fde\u63a5\u5931\u8d25: " + e.getMessage());
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
        }
        catch (BizException e) {
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

