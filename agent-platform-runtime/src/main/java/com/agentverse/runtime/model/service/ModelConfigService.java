package com.agentverse.runtime.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.agentverse.common.entity.BaseEntity;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.model.dto.ConnectionTestResult;
import com.agentverse.runtime.model.dto.ModelConfigCreateRequest;
import com.agentverse.runtime.model.dto.ModelConfigResponse;
import com.agentverse.runtime.model.dto.ModelConfigUpdateRequest;
import com.agentverse.runtime.model.entity.ModelConfig;
import com.agentverse.runtime.model.entity.ModelProvider;
import com.agentverse.runtime.model.mapper.ModelConfigMapper;
import com.agentverse.runtime.model.mapper.ModelProviderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

/**
 * 模型配置服务。
 * <p>
 * 负责 ModelConfig 的 CRUD、默认模型切换、用户级隔离。
 */
@Service
@RequiredArgsConstructor
public class ModelConfigService {

    private static final Logger log = LoggerFactory.getLogger(ModelConfigService.class);

    private final ModelConfigMapper modelConfigMapper;
    private final ModelProviderMapper modelProviderMapper;
    private final ModelProviderService modelProviderService;

    @Transactional(rollbackFor = { Exception.class })
    public ModelConfigResponse createModelConfig(ModelConfigCreateRequest request) {
        log.info("创建模型配置: providerId={}, modelName={}", request.getProviderId(), request.getModelName());
        ModelProvider provider = modelProviderMapper.selectById(request.getProviderId());
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
            clearOtherDefaults(UserContext.getUserId());
        }
        modelConfigMapper.insert(config);
        log.info("模型配置创建成功: {}", config.getId());
        return convertToResponse(config, provider);
    }

    public ModelConfigResponse getModelConfigById(String id) {
        log.info("查询模型配置: {}", id);
        ModelConfig config = modelConfigMapper.selectById(id);
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        checkDataAccess(config);
        ModelProvider provider = modelProviderMapper.selectById(config.getProviderId());
        return convertToResponse(config, provider);
    }

    public Page<ModelConfigResponse> listModelConfigs(Integer page, Integer pageSize, String providerId) {
        Long userId;
        log.info("查询模型配置列表: page={}, pageSize={}, providerId={}", page, pageSize, providerId);
        Page<ModelConfig> pageParam = new Page<>(page.longValue(), pageSize.longValue());
        LambdaQueryWrapper<ModelConfig> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(providerId)) {
            queryWrapper.eq(ModelConfig::getProviderId, providerId);
        }
        if (!UserContext.isAdmin() && (userId = UserContext.getUserId()) != null) {
            queryWrapper.eq(BaseEntity::getCreatedBy, userId);
        }
        queryWrapper.orderByDesc(BaseEntity::getCreatedTime);
        Page<ModelConfig> result = modelConfigMapper.selectPage(pageParam, queryWrapper);
        Page<ModelConfigResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(config -> {
            ModelProvider p = modelProviderMapper.selectById(config.getProviderId());
            return convertToResponse(config, p);
        }).collect(Collectors.toList()));
        return responsePage;
    }

    @Transactional(rollbackFor = { Exception.class })
    public ModelConfigResponse updateModelConfig(String id, ModelConfigUpdateRequest request) {
        log.info("更新模型配置: {}", id);
        ModelConfig config = modelConfigMapper.selectById(id);
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        checkDataAccess(config);
        if (request.getDisplayName() != null) {
            config.setDisplayName(request.getDisplayName());
        }
        if (request.getModelName() != null) {
            config.setModelName(request.getModelName());
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
        if (StringUtils.hasText(request.getStatus())) {
            config.setStatus(request.getStatus());
        }
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            clearOtherDefaults(config.getCreatedBy());
        }
        if (request.getIsDefault() != null) {
            config.setIsDefault(request.getIsDefault());
        }
        modelConfigMapper.updateById(config);
        log.info("模型配置更新成功: {}", id);
        ModelProvider provider = modelProviderMapper.selectById(config.getProviderId());
        return convertToResponse(config, provider);
    }

    @Transactional(rollbackFor = { Exception.class })
    public void deleteModelConfig(String id) {
        log.info("删除模型配置: {}", id);
        ModelConfig config = modelConfigMapper.selectById(id);
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        checkDataAccess(config);
        int agentCount = modelConfigMapper.countAgentsByModelConfigId(id);
        if (agentCount > 0) {
            throw new BizException(ErrorCode.MODEL_CONFIG_IN_USE);
        }
        modelConfigMapper.deleteById(id);
        log.info("模型配置删除成功: {}", id);
    }

    public List<ModelConfig> listByProviderId(String providerId) {
        LambdaQueryWrapper<ModelConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ModelConfig::getProviderId, providerId);
        return modelConfigMapper.selectList(queryWrapper);
    }

    /**
     * 通过模型配置 ID 测试连接。
     * <p>
     * 根据 modelConfigId 查询 ModelConfig 和关联的 ModelProvider，
     * 使用配置的 modelName 进行连接测试。
     *
     * @param configId 模型配置 ID
     * @return 连接测试结果
     */
    public ConnectionTestResult testConnectionByConfigId(String configId) {
        log.info("测试模型配置连接: configId={}", configId);

        // 查询模型配置
        ModelConfig config = modelConfigMapper.selectById(configId);
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        checkDataAccess(config);

        // 查询关联的供应商
        ModelProvider provider = modelProviderMapper.selectById(config.getProviderId());
        if (provider == null) {
            throw new BizException(ErrorCode.MODEL_PROVIDER_NOT_FOUND);
        }

        // 调用供应商服务的测试方法，传入 modelName
        return modelProviderService.testConnection(provider.getId(), config.getModelName());
    }

    public ModelConfigResponse getDefaultModel() {
        log.info("获取当前用户的默认模型配置");
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
        LambdaQueryWrapper<ModelConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseEntity::getCreatedBy, userId);
        queryWrapper.eq(ModelConfig::getIsDefault, 1);
        queryWrapper.eq(ModelConfig::getStatus, "active");
        queryWrapper.last("LIMIT 1");
        ModelConfig config = modelConfigMapper.selectOne(queryWrapper);
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        ModelProvider provider = modelProviderMapper.selectById(config.getProviderId());
        return convertToResponse(config, provider);
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

    /**
     * 实体转响应 DTO。
     * <p>
     * 将 {@link ModelConfig} 与关联的 {@link ModelProvider} 合并为前端展示用的
     * {@link ModelConfigResponse}。
     *
     * @param config   模型配置实体
     * @param provider 关联的模型提供方（可为 null）
     * @return 响应 DTO
     */
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

    /**
     * 清除当前用户其他默认模型标志（保留指定 userId 的）。
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
}
