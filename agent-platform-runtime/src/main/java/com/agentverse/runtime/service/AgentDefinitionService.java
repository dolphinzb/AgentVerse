/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.service;

import com.agentverse.common.dto.AgentCreateRequest;
import com.agentverse.common.dto.AgentListResponse;
import com.agentverse.common.dto.AgentResponse;
import com.agentverse.common.dto.AgentUpdateRequest;
import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.common.entity.BaseEntity;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.engine.AgentLoaderService;
import com.agentverse.runtime.mapper.AgentDefinitionMapper;
import com.agentverse.runtime.model.entity.ModelConfig;
import com.agentverse.runtime.model.entity.ModelProvider;
import com.agentverse.runtime.model.mapper.ModelConfigMapper;
import com.agentverse.runtime.model.mapper.ModelProviderMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AgentDefinitionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AgentDefinitionService.class);
    private final AgentDefinitionMapper agentDefinitionMapper;
    private final ModelConfigMapper modelConfigMapper;
    private final ModelProviderMapper modelProviderMapper;
    private final AgentLoaderService agentLoaderService;

    @Transactional(rollbackFor={Exception.class})
    public AgentResponse createAgent(AgentCreateRequest request) {
        log.info("Creating agent: {}", (Object)request.getName());
        this.validateModelConfig(request.getModelConfigId());
        AgentDefinition agent = new AgentDefinition();
        agent.setId(UUID.randomUUID().toString());
        agent.setName(request.getName());
        agent.setDescription(request.getDescription());
        agent.setSysPrompt(request.getSysPrompt());
        agent.setMaxIterations(request.getMaxIterations() != null ? request.getMaxIterations() : 10);
        agent.setWorkspaceMode("isolated");
        agent.setStatus("draft");
        agent.setModelConfigId(request.getModelConfigId());
        agent.setFilesystemType(request.getFilesystemType());
        agent.setEnableMemoryFlush(request.getEnableMemoryFlush());
        agent.setEnableMemoryMaintenance(request.getEnableMemoryMaintenance());
        agent.setEnableCompaction(request.getEnableCompaction());
        agent.setCompactionTriggerPct(request.getCompactionTriggerPct());
        agent.setCompactionKeepRecent(request.getCompactionKeepRecent());
        agent.setEnableToolResultEviction(request.getEnableToolResultEviction());
        agent.setToolResultEvictionMaxChars(request.getToolResultEvictionMaxChars());
        agent.setEnableLongTermMemory(request.getEnableLongTermMemory());
        agent.setEnablePlan(request.getEnablePlan());
        agent.setEnableSessionPersistence(request.getEnableSessionPersistence());
        agent.setSessionBackend(request.getSessionBackend());
        agent.setMaxContextTokens(request.getMaxContextTokens());
        agent.setCreatedTime(LocalDateTime.now());
        agent.setUpdatedTime(LocalDateTime.now());
        agent.setCreatedBy(UserContext.getUserId());
        this.agentDefinitionMapper.insert(agent);
        log.info("Agent created successfully: {}", (Object)agent.getId());
        return this.convertToResponse(agent);
    }

    public AgentResponse getAgentById(String id) {
        log.info("Getting agent by id: {}", (Object)id);
        AgentDefinition agent = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)id));
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        this.checkDataAccess(agent);
        return this.convertToResponse(agent);
    }

    public AgentListResponse listAgents(Integer page, Integer pageSize, String status) {
        log.info("Listing agents: page={}, pageSize={}, status={}", page, pageSize, status);
        Page<AgentDefinition> pageParam = new Page<>(page.longValue(), pageSize.longValue());
        LambdaQueryWrapper<AgentDefinition> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(AgentDefinition::getStatus, status);
        }
        Long userId;
        if (!UserContext.isAdmin() && (userId = UserContext.getUserId()) != null) {
            queryWrapper.eq(BaseEntity::getCreatedBy, userId.toString());
        }
        queryWrapper.orderByDesc(BaseEntity::getCreatedTime);
        Page<AgentDefinition> result = this.agentDefinitionMapper.selectPage(pageParam, queryWrapper);
        List<AgentResponse> agents = result.getRecords().stream().map(this::convertToResponse).collect(Collectors.toList());
        return new AgentListResponse(agents, result.getTotal(), page, pageSize);
    }

    @Transactional(rollbackFor={Exception.class})
    public AgentResponse updateAgent(String id, AgentUpdateRequest request) {
        log.info("Updating agent: {}", (Object)id);
        AgentDefinition agent = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)id));
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        this.checkDataAccess(agent);
        if (StringUtils.hasText((String)request.getName())) {
            agent.setName(request.getName());
        }
        if (request.getDescription() != null) {
            agent.setDescription(request.getDescription());
        }
        if (StringUtils.hasText((String)request.getSysPrompt())) {
            agent.setSysPrompt(request.getSysPrompt());
        }
        if (request.getMaxIterations() != null) {
            agent.setMaxIterations(request.getMaxIterations());
        }
        if (StringUtils.hasText((String)request.getWorkspaceMode())) {
            agent.setWorkspaceMode(request.getWorkspaceMode());
        }
        if (StringUtils.hasText((String)request.getModelConfigId())) {
            this.validateModelConfig(request.getModelConfigId());
            agent.setModelConfigId(request.getModelConfigId());
        }
        if (StringUtils.hasText((String)request.getFilesystemType())) {
            agent.setFilesystemType(request.getFilesystemType());
        }
        if (request.getEnableMemoryFlush() != null) {
            agent.setEnableMemoryFlush(request.getEnableMemoryFlush());
        }
        if (request.getEnableMemoryMaintenance() != null) {
            agent.setEnableMemoryMaintenance(request.getEnableMemoryMaintenance());
        }
        if (request.getEnableCompaction() != null) {
            agent.setEnableCompaction(request.getEnableCompaction());
        }
        if (request.getCompactionTriggerPct() != null) {
            agent.setCompactionTriggerPct(request.getCompactionTriggerPct());
        }
        if (request.getCompactionKeepRecent() != null) {
            agent.setCompactionKeepRecent(request.getCompactionKeepRecent());
        }
        if (request.getEnableToolResultEviction() != null) {
            agent.setEnableToolResultEviction(request.getEnableToolResultEviction());
        }
        if (request.getToolResultEvictionMaxChars() != null) {
            agent.setToolResultEvictionMaxChars(request.getToolResultEvictionMaxChars());
        }
        if (request.getEnableLongTermMemory() != null) {
            agent.setEnableLongTermMemory(request.getEnableLongTermMemory());
        }
        if (request.getEnablePlan() != null) {
            agent.setEnablePlan(request.getEnablePlan());
        }
        if (request.getEnableSessionPersistence() != null) {
            agent.setEnableSessionPersistence(request.getEnableSessionPersistence());
        }
        if (StringUtils.hasText((String)request.getSessionBackend())) {
            agent.setSessionBackend(request.getSessionBackend());
        }
        if (request.getMaxContextTokens() != null) {
            agent.setMaxContextTokens(request.getMaxContextTokens());
        }
        agent.setUpdatedTime(LocalDateTime.now());
        this.agentDefinitionMapper.updateById(agent);
        this.agentLoaderService.evictAgent(id);
        log.info("Agent updated successfully: {}", (Object)id);
        return this.convertToResponse(agent);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteAgent(String id) {
        log.info("Deleting agent: {}", (Object)id);
        AgentDefinition agent = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)id));
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        this.checkDataAccess(agent);
        this.agentDefinitionMapper.deleteById((Serializable)((Object)id));
        log.info("Agent deleted successfully: {}", (Object)id);
    }

    public void publishAgent(String agentId) {
        AgentDefinition def = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)agentId));
        if (def == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        if (!"draft".equals(def.getStatus())) {
            throw new BizException(ErrorCode.AGENT_ALREADY_PUBLISHED);
        }
        if (def.getModelConfigId() == null || def.getModelConfigId().isEmpty()) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        def.setStatus("active");
        this.agentDefinitionMapper.updateById(def);
    }

    public void archiveAgent(String agentId) {
        AgentDefinition def = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)agentId));
        if (def == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        if (!"active".equals(def.getStatus())) {
            throw new BizException(ErrorCode.AGENT_NOT_ACTIVE);
        }
        def.setStatus("archived");
        this.agentDefinitionMapper.updateById(def);
        this.agentLoaderService.evictAgent(agentId);
    }

    public void reactivateAgent(String agentId) {
        AgentDefinition def = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)agentId));
        if (def == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        if (!"archived".equals(def.getStatus())) {
            throw new BizException(ErrorCode.AGENT_NOT_ACTIVE);
        }
        def.setStatus("active");
        this.agentDefinitionMapper.updateById(def);
    }

    private void checkDataAccess(AgentDefinition agent) {
        if (UserContext.isAdmin()) {
            return;
        }
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null || agent.getCreatedBy() == null || !agent.getCreatedBy().equals(currentUserId.toString())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private void validateModelConfig(String modelConfigId) {
        Long currentUserId;
        ModelConfig config = (ModelConfig)this.modelConfigMapper.selectById((Serializable)((Object)modelConfigId));
        if (config == null) {
            throw new BizException(ErrorCode.MODEL_CONFIG_NOT_FOUND);
        }
        if (!(UserContext.isAdmin() || (currentUserId = UserContext.getUserId()) != null && config.getCreatedBy() != null && config.getCreatedBy().equals(currentUserId.toString()))) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private AgentResponse convertToResponse(AgentDefinition agent) {
        ModelConfig config;
        AgentResponse response = new AgentResponse();
        response.setId(agent.getId());
        response.setName(agent.getName());
        response.setDescription(agent.getDescription());
        response.setSysPrompt(agent.getSysPrompt());
        response.setMaxIterations(agent.getMaxIterations());
        response.setWorkspaceMode(agent.getWorkspaceMode());
        response.setStatus(agent.getStatus());
        response.setCurrentVersion(agent.getCurrentVersion());
        response.setModelConfigId(agent.getModelConfigId());
        response.setFilesystemType(agent.getFilesystemType());
        response.setEnableMemoryFlush(agent.getEnableMemoryFlush());
        response.setEnableMemoryMaintenance(agent.getEnableMemoryMaintenance());
        response.setEnableCompaction(agent.getEnableCompaction());
        response.setCompactionTriggerPct(agent.getCompactionTriggerPct());
        response.setCompactionKeepRecent(agent.getCompactionKeepRecent());
        response.setEnableToolResultEviction(agent.getEnableToolResultEviction());
        response.setToolResultEvictionMaxChars(agent.getToolResultEvictionMaxChars());
        response.setEnableLongTermMemory(agent.getEnableLongTermMemory());
        response.setEnablePlan(agent.getEnablePlan());
        response.setEnableSessionPersistence(agent.getEnableSessionPersistence());
        response.setSessionBackend(agent.getSessionBackend());
        response.setMaxContextTokens(agent.getMaxContextTokens());
        response.setCreatedTime(agent.getCreatedTime());
        response.setUpdatedTime(agent.getUpdatedTime());
        if (StringUtils.hasText((String)agent.getModelConfigId()) && (config = (ModelConfig)this.modelConfigMapper.selectById((Serializable)((Object)agent.getModelConfigId()))) != null) {
            response.setModelName(config.getModelName());
            ModelProvider provider = (ModelProvider)this.modelProviderMapper.selectById((Serializable)((Object)config.getProviderId()));
            if (provider != null) {
                response.setProviderName(provider.getName());
            }
        }
        return response;
    }

    @Generated
    public AgentDefinitionService(AgentDefinitionMapper agentDefinitionMapper, ModelConfigMapper modelConfigMapper, ModelProviderMapper modelProviderMapper, AgentLoaderService agentLoaderService) {
        this.agentDefinitionMapper = agentDefinitionMapper;
        this.modelConfigMapper = modelConfigMapper;
        this.modelProviderMapper = modelProviderMapper;
        this.agentLoaderService = agentLoaderService;
    }
}

