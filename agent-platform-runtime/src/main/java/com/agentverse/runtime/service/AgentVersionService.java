/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.service;

import com.agentverse.common.dto.AgentPublishRequest;
import com.agentverse.common.dto.AgentVersionResponse;
import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.common.entity.AgentVersion;
import com.agentverse.common.entity.BaseEntity;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.mapper.AgentDefinitionMapper;
import com.agentverse.runtime.mapper.AgentVersionMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentVersionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AgentVersionService.class);
    private final AgentVersionMapper agentVersionMapper;
    private final AgentDefinitionMapper agentDefinitionMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule((Module)new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Transactional(rollbackFor={Exception.class})
    public AgentVersionResponse publishVersion(String agentId, AgentPublishRequest request) {
        log.info("Publishing version for agent {}: {}", (Object)agentId, (Object)request.getVersion());
        AgentDefinition agent = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)agentId));
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        this.checkDataAccess(agent);
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper();
        ((LambdaQueryWrapper)wrapper.eq(AgentVersion::getAgentId, (Object)agentId)).eq(AgentVersion::getVersion, (Object)request.getVersion());
        if (this.agentVersionMapper.selectCount((Wrapper)wrapper) > 0L) {
            throw new BizException(ErrorCode.AGENT_VERSION_DUPLICATE, request.getVersion());
        }
        String snapshotData = this.serializeSnapshot(agent);
        AgentVersion version = new AgentVersion();
        version.setId(UUID.randomUUID().toString());
        version.setAgentId(agentId);
        version.setVersion(request.getVersion());
        version.setSnapshotData(snapshotData);
        version.setChangelog(request.getChangelog());
        version.setCreatedBy(UserContext.getUserId());
        version.setCreatedTime(LocalDateTime.now());
        version.setUpdatedTime(LocalDateTime.now());
        this.agentVersionMapper.insert(version);
        agent.setCurrentVersion(request.getVersion());
        agent.setStatus("active");
        agent.setUpdatedTime(LocalDateTime.now());
        this.agentDefinitionMapper.updateById(agent);
        log.info("Version published successfully: {} for agent {}", (Object)request.getVersion(), (Object)agentId);
        return this.convertToResponse(version);
    }

    public List<AgentVersionResponse> listVersions(String agentId) {
        log.info("Listing versions for agent {}", (Object)agentId);
        AgentDefinition agent = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)agentId));
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper();
        ((LambdaQueryWrapper)wrapper.eq(AgentVersion::getAgentId, (Object)agentId)).orderByDesc(BaseEntity::getCreatedTime);
        List versions = this.agentVersionMapper.selectList((Wrapper)wrapper);
        return versions.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional(rollbackFor={Exception.class})
    public AgentVersionResponse rollbackVersion(String agentId, String targetVersion) {
        log.info("Rolling back agent {} to version {}", (Object)agentId, (Object)targetVersion);
        AgentDefinition agent = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)agentId));
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        this.checkDataAccess(agent);
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper();
        ((LambdaQueryWrapper)wrapper.eq(AgentVersion::getAgentId, (Object)agentId)).eq(AgentVersion::getVersion, (Object)targetVersion);
        AgentVersion version = (AgentVersion)this.agentVersionMapper.selectOne((Wrapper)wrapper);
        if (version == null) {
            throw new BizException(ErrorCode.AGENT_VERSION_NOT_FOUND, targetVersion);
        }
        this.applySnapshot(agent, version.getSnapshotData());
        agent.setCurrentVersion(targetVersion);
        agent.setUpdatedTime(LocalDateTime.now());
        this.agentDefinitionMapper.updateById(agent);
        log.info("Agent {} rolled back to version {}", (Object)agentId, (Object)targetVersion);
        return this.convertToResponse(version);
    }

    private void checkDataAccess(AgentDefinition agent) {
        if (!UserContext.isAdmin() && agent.getCreatedBy() != null && !agent.getCreatedBy().equals(UserContext.getUserId())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private String serializeSnapshot(AgentDefinition agent) {
        try {
            HashMap<String, Object> snapshot = new HashMap<String, Object>();
            snapshot.put("name", agent.getName());
            snapshot.put("description", agent.getDescription());
            snapshot.put("sysPrompt", agent.getSysPrompt());
            snapshot.put("maxIterations", agent.getMaxIterations());
            snapshot.put("workspaceMode", agent.getWorkspaceMode());
            return objectMapper.writeValueAsString(snapshot);
        }
        catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR.getCode(), "Failed to serialize agent snapshot: " + e.getMessage());
        }
    }

    private void applySnapshot(AgentDefinition agent, String snapshotData) {
        try {
            Map snapshot = (Map)objectMapper.readValue(snapshotData, Map.class);
            if (snapshot.containsKey("name")) {
                agent.setName((String)snapshot.get("name"));
            }
            if (snapshot.containsKey("description")) {
                agent.setDescription((String)snapshot.get("description"));
            }
            if (snapshot.containsKey("sysPrompt")) {
                agent.setSysPrompt((String)snapshot.get("sysPrompt"));
            }
            if (snapshot.containsKey("maxIterations")) {
                agent.setMaxIterations(((Number)snapshot.get("maxIterations")).intValue());
            }
            if (snapshot.containsKey("workspaceMode")) {
                agent.setWorkspaceMode((String)snapshot.get("workspaceMode"));
            }
        }
        catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR.getCode(), "Failed to parse agent snapshot: " + e.getMessage());
        }
    }

    private AgentVersionResponse convertToResponse(AgentVersion version) {
        AgentVersionResponse response = new AgentVersionResponse();
        response.setId(version.getId());
        response.setAgentId(version.getAgentId());
        response.setVersion(version.getVersion());
        response.setSnapshotData(version.getSnapshotData());
        response.setChangelog(version.getChangelog());
        response.setCreatedTime(version.getCreatedTime());
        response.setUpdatedTime(version.getUpdatedTime());
        return response;
    }

    @Generated
    public AgentVersionService(AgentVersionMapper agentVersionMapper, AgentDefinitionMapper agentDefinitionMapper) {
        this.agentVersionMapper = agentVersionMapper;
        this.agentDefinitionMapper = agentDefinitionMapper;
    }
}

