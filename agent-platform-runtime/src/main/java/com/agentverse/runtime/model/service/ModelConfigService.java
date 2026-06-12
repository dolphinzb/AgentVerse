/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.service;

import com.agentverse.common.entity.BaseEntity;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.model.dto.ModelConfigCreateRequest;
import com.agentverse.runtime.model.dto.ModelConfigResponse;
import com.agentverse.runtime.model.dto.ModelConfigUpdateRequest;
import com.agentverse.runtime.model.entity.ModelConfig;
import com.agentverse.runtime.model.entity.ModelProvider;
import com.agentverse.runtime.model.mapper.ModelConfigMapper;
import com.agentverse.runtime.model.mapper.ModelProviderMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ModelConfigService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelConfigService.class);
    private final ModelConfigMapper modelConfigMapper;
    private final ModelProviderMapper modelProviderMapper;

    @Transactional(rollbackFor={Exception.class})
    public ModelConfigResponse createModelConfig(ModelConfigCreateRequest request) {
        log.info("\u521b\u5efa\u6a21\u578b\u914d\u7f6e: providerId={}, modelName={}", (Object)request.getProviderId(), (Object)request.getModelName());
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)request.getProviderId()));
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }
        ModelConfig config = new ModelConfig();
        config.setProviderId(request.getProviderId());
        config.setModelName(request.getModelName());
        config.setDisplayName(request.getDisplayName());
        config.setMaxTokens(request.getMaxTokens());
        config.setTemperature(request.getTemperature());
        config.setTopP(request.getTopP());
        config.setIsDefault(request.getIsDefault());
        config.setStatus("active");
        config.setCreatedBy(UserContext.getUserId());
        if (config.getIsDefault() != null && config.getIsDefault() == 1) {
            this.clearOtherDefaults(UserContext.getUserId());
        }
        this.modelConfigMapper.insert(config);
        log.info("\u6a21\u578b\u914d\u7f6e\u521b\u5efa\u6210\u529f: {}", (Object)config.getId());
        return this.convertToResponse(config, provider);
    }

    public ModelConfigResponse getModelConfigById(String id) {
        log.info("\u67e5\u8be2\u6a21\u578b\u914d\u7f6e: {}", (Object)id);
        ModelConfig config = (ModelConfig)this.modelConfigMapper.selectById((Serializable)((Object)id));
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        this.checkDataAccess(config);
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)config.getProviderId()));
        return this.convertToResponse(config, provider);
    }

    public Page<ModelConfigResponse> listModelConfigs(Integer page, Integer pageSize, String providerId) {
        Long userId;
        log.info("\u67e5\u8be2\u6a21\u578b\u914d\u7f6e\u5217\u8868: page={}, pageSize={}, providerId={}", new Object[]{page, pageSize, providerId});
        Page pageParam = new Page((long)page.intValue(), (long)pageSize.intValue());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        if (StringUtils.hasText((String)providerId)) {
            queryWrapper.eq(ModelConfig::getProviderId, (Object)providerId);
        }
        if (!UserContext.isAdmin() && (userId = UserContext.getUserId()) != null) {
            queryWrapper.eq(BaseEntity::getCreatedBy, (Object)userId);
        }
        queryWrapper.orderByDesc(BaseEntity::getCreatedTime);
        Page result = (Page)this.modelConfigMapper.selectPage((IPage)pageParam, (Wrapper)queryWrapper);
        Page responsePage = new Page(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(config -> {
            ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)config.getProviderId()));
            return this.convertToResponse((ModelConfig)config, provider);
        }).collect(Collectors.toList()));
        return responsePage;
    }

    @Transactional(rollbackFor={Exception.class})
    public ModelConfigResponse updateModelConfig(String id, ModelConfigUpdateRequest request) {
        log.info("\u66f4\u65b0\u6a21\u578b\u914d\u7f6e: {}", (Object)id);
        ModelConfig config = (ModelConfig)this.modelConfigMapper.selectById((Serializable)((Object)id));
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        this.checkDataAccess(config);
        if (request.getDisplayName() != null) {
            config.setDisplayName(request.getDisplayName());
        }
        if (request.getMaxTokens() != null) {
            config.setMaxTokens(request.getMaxTokens());
        }
        if (request.getTemperature() != null) {
            config.setTemperature(request.getTemperature());
        }
        if (request.getTopP() != null) {
            config.setTopP(request.getTopP());
        }
        if (StringUtils.hasText((String)request.getStatus())) {
            config.setStatus(request.getStatus());
        }
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            this.clearOtherDefaults(config.getCreatedBy());
        }
        if (request.getIsDefault() != null) {
            config.setIsDefault(request.getIsDefault());
        }
        this.modelConfigMapper.updateById(config);
        log.info("\u6a21\u578b\u914d\u7f6e\u66f4\u65b0\u6210\u529f: {}", (Object)id);
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)config.getProviderId()));
        return this.convertToResponse(config, provider);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteModelConfig(String id) {
        log.info("\u5220\u9664\u6a21\u578b\u914d\u7f6e: {}", (Object)id);
        ModelConfig config = (ModelConfig)this.modelConfigMapper.selectById((Serializable)((Object)id));
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        this.checkDataAccess(config);
        int agentCount = this.modelConfigMapper.countAgentsByModelConfigId(id);
        if (agentCount > 0) {
            throw new BizException(ErrorCode.MODEL_CONFIG_IN_USE);
        }
        this.modelConfigMapper.deleteById((Serializable)((Object)id));
        log.info("\u6a21\u578b\u914d\u7f6e\u5220\u9664\u6210\u529f: {}", (Object)id);
    }

    public List<ModelConfig> listByProviderId(String providerId) {
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ModelConfig::getProviderId, (Object)providerId);
        return this.modelConfigMapper.selectList((Wrapper)queryWrapper);
    }

    public ModelConfigResponse getDefaultModel() {
        log.info("\u83b7\u53d6\u5f53\u524d\u7528\u6237\u7684\u9ed8\u8ba4\u6a21\u578b\u914d\u7f6e");
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(BaseEntity::getCreatedBy, (Object)userId);
        queryWrapper.eq(ModelConfig::getIsDefault, (Object)1);
        queryWrapper.eq(ModelConfig::getStatus, (Object)"active");
        queryWrapper.last("LIMIT 1");
        ModelConfig config = (ModelConfig)this.modelConfigMapper.selectOne((Wrapper)queryWrapper);
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)config.getProviderId()));
        return this.convertToResponse(config, provider);
    }

    private void checkDataAccess(ModelConfig config) {
        if (UserContext.isAdmin()) {
            return;
        }
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null || config.getCreatedBy() == null || !config.getCreatedBy().equals(currentUserId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
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
    public ModelConfigService(ModelConfigMapper modelConfigMapper, ModelProviderMapper modelProviderMapper) {
        this.modelConfigMapper = modelConfigMapper;
        this.modelProviderMapper = modelProviderMapper;
    }
}

