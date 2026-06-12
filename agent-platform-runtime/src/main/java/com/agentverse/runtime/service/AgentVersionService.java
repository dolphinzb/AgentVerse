package com.agentverse.runtime.service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

/**
 * Agent 版本快照服务。
 * <p>
 * 提供 publish/list/rollback 三个核心操作，版本数据以 JSON 快照形式存储。
 */
@Service
@RequiredArgsConstructor
public class AgentVersionService {

    private static final Logger log = LoggerFactory.getLogger(AgentVersionService.class);

    private final AgentVersionMapper agentVersionMapper;
    private final AgentDefinitionMapper agentDefinitionMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * 发布新版本：序列化当前 AgentDefinition 为快照并存入 agent_version 表。
     */
    @Transactional(rollbackFor = { Exception.class })
    public AgentVersionResponse publishVersion(String agentId, AgentPublishRequest request) {
        log.info("Publishing version for agent {}: {}", agentId, request.getVersion());
        AgentDefinition agent = agentDefinitionMapper.selectById((Serializable) agentId);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        checkDataAccess(agent);
        LambdaQueryWrapper<AgentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentVersion::getAgentId, agentId)
                .eq(AgentVersion::getVersion, request.getVersion());
        if (agentVersionMapper.selectCount(wrapper) > 0L) {
            throw new BizException(ErrorCode.AGENT_VERSION_DUPLICATE, request.getVersion());
        }
        String snapshotData = serializeSnapshot(agent);
        AgentVersion version = new AgentVersion();
        version.setId(UUID.randomUUID().toString());
        version.setAgentId(agentId);
        version.setVersion(request.getVersion());
        version.setSnapshotData(snapshotData);
        version.setChangelog(request.getChangelog());
        version.setCreatedBy(UserContext.getUserId());
        version.setCreatedTime(LocalDateTime.now());
        version.setUpdatedTime(LocalDateTime.now());
        agentVersionMapper.insert(version);
        agent.setCurrentVersion(request.getVersion());
        agent.setStatus("active");
        agent.setUpdatedTime(LocalDateTime.now());
        agentDefinitionMapper.updateById(agent);
        log.info("Version published successfully: {} for agent {}", request.getVersion(), agentId);
        return convertToResponse(version);
    }

    public List<AgentVersionResponse> listVersions(String agentId) {
        log.info("Listing versions for agent {}", agentId);
        AgentDefinition agent = agentDefinitionMapper.selectById((Serializable) agentId);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        LambdaQueryWrapper<AgentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentVersion::getAgentId, agentId)
                .orderByDesc(BaseEntity::getCreatedTime);
        List<AgentVersion> versions = agentVersionMapper.selectList(wrapper);
        return versions.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = { Exception.class })
    public AgentVersionResponse rollbackVersion(String agentId, String targetVersion) {
        log.info("Rolling back agent {} to version {}", agentId, targetVersion);
        AgentDefinition agent = agentDefinitionMapper.selectById((Serializable) agentId);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        checkDataAccess(agent);
        LambdaQueryWrapper<AgentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentVersion::getAgentId, agentId)
                .eq(AgentVersion::getVersion, targetVersion);
        AgentVersion version = agentVersionMapper.selectOne(wrapper);
        if (version == null) {
            throw new BizException(ErrorCode.AGENT_VERSION_NOT_FOUND, targetVersion);
        }
        applySnapshot(agent, version.getSnapshotData());
        agent.setCurrentVersion(targetVersion);
        agent.setUpdatedTime(LocalDateTime.now());
        agentDefinitionMapper.updateById(agent);
        log.info("Agent {} rolled back to version {}", agentId, targetVersion);
        return convertToResponse(version);
    }

    private void checkDataAccess(AgentDefinition agent) {
        if (!UserContext.isAdmin() && agent.getCreatedBy() != null
                && !agent.getCreatedBy().equals(UserContext.getUserId())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private String serializeSnapshot(AgentDefinition agent) {
        try {
            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("name", agent.getName());
            snapshot.put("description", agent.getDescription());
            snapshot.put("sysPrompt", agent.getSysPrompt());
            snapshot.put("maxIterations", agent.getMaxIterations());
            snapshot.put("workspaceMode", agent.getWorkspaceMode());
            snapshot.put("modelConfigId", agent.getModelConfigId());
            snapshot.put("filesystemType", agent.getFilesystemType());
            snapshot.put("enableMemoryFlush", agent.getEnableMemoryFlush());
            snapshot.put("enableMemoryMaintenance", agent.getEnableMemoryMaintenance());
            snapshot.put("enableCompaction", agent.getEnableCompaction());
            snapshot.put("compactionTriggerPct", agent.getCompactionTriggerPct());
            snapshot.put("compactionKeepRecent", agent.getCompactionKeepRecent());
            snapshot.put("enableToolResultEviction", agent.getEnableToolResultEviction());
            snapshot.put("toolResultEvictionMaxChars", agent.getToolResultEvictionMaxChars());
            snapshot.put("enableLongTermMemory", agent.getEnableLongTermMemory());
            snapshot.put("enablePlan", agent.getEnablePlan());
            snapshot.put("enableSessionPersistence", agent.getEnableSessionPersistence());
            snapshot.put("sessionBackend", agent.getSessionBackend());
            snapshot.put("maxContextTokens", agent.getMaxContextTokens());
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "Failed to serialize snapshot: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void applySnapshot(AgentDefinition agent, String snapshotData) {
        try {
            Map<String, Object> snapshot = objectMapper.readValue(snapshotData, Map.class);
            if (snapshot.containsKey("name"))
                agent.setName((String) snapshot.get("name"));
            if (snapshot.containsKey("description"))
                agent.setDescription((String) snapshot.get("description"));
            if (snapshot.containsKey("sysPrompt"))
                agent.setSysPrompt((String) snapshot.get("sysPrompt"));
            if (snapshot.containsKey("maxIterations"))
                agent.setMaxIterations((Integer) snapshot.get("maxIterations"));
            if (snapshot.containsKey("workspaceMode"))
                agent.setWorkspaceMode((String) snapshot.get("workspaceMode"));
            if (snapshot.containsKey("modelConfigId"))
                agent.setModelConfigId((String) snapshot.get("modelConfigId"));
            if (snapshot.containsKey("filesystemType"))
                agent.setFilesystemType((String) snapshot.get("filesystemType"));
            if (snapshot.containsKey("enableMemoryFlush"))
                agent.setEnableMemoryFlush(asInteger(snapshot.get("enableMemoryFlush")));
            if (snapshot.containsKey("enableMemoryMaintenance"))
                agent.setEnableMemoryMaintenance(asInteger(snapshot.get("enableMemoryMaintenance")));
            if (snapshot.containsKey("enableCompaction"))
                agent.setEnableCompaction(asInteger(snapshot.get("enableCompaction")));
            if (snapshot.containsKey("compactionTriggerPct"))
                agent.setCompactionTriggerPct((Integer) snapshot.get("compactionTriggerPct"));
            if (snapshot.containsKey("compactionKeepRecent"))
                agent.setCompactionKeepRecent((Integer) snapshot.get("compactionKeepRecent"));
            if (snapshot.containsKey("enableToolResultEviction"))
                agent.setEnableToolResultEviction(asInteger(snapshot.get("enableToolResultEviction")));
            if (snapshot.containsKey("toolResultEvictionMaxChars"))
                agent.setToolResultEvictionMaxChars((Integer) snapshot.get("toolResultEvictionMaxChars"));
            if (snapshot.containsKey("enableLongTermMemory"))
                agent.setEnableLongTermMemory(asInteger(snapshot.get("enableLongTermMemory")));
            if (snapshot.containsKey("enablePlan"))
                agent.setEnablePlan(asInteger(snapshot.get("enablePlan")));
            if (snapshot.containsKey("enableSessionPersistence"))
                agent.setEnableSessionPersistence(asInteger(snapshot.get("enableSessionPersistence")));
            if (snapshot.containsKey("sessionBackend"))
                agent.setSessionBackend((String) snapshot.get("sessionBackend"));
            if (snapshot.containsKey("maxContextTokens"))
                agent.setMaxContextTokens((Integer) snapshot.get("maxContextTokens"));
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "Failed to apply snapshot: " + e.getMessage());
        }
    }

    private AgentVersionResponse convertToResponse(AgentVersion version) {
        AgentVersionResponse response = new AgentVersionResponse();
        response.setId(version.getId());
        response.setAgentId(version.getAgentId());
        response.setVersion(version.getVersion());
        response.setChangelog(version.getChangelog());
        response.setCreatedTime(version.getCreatedTime());
        response.setUpdatedTime(version.getUpdatedTime());
        return response;
    }

    /**
     * 把快照中读取到的 enable_* 字段安全地转为 Integer。
     * <p>
     * JSON 反序列化时可能是 Boolean、Number 或 String("0"/"1")，统一归一化为 0/1 整数。
     *
     * @param value JSON 中读取的原始值
     * @return 0 或 1；null 时返回 null
     */
    private Integer asInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean b) {
            return b ? 1 : 0;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s) {
            if (s.isEmpty()) {
                return null;
            }
            return "true".equalsIgnoreCase(s) || "1".equals(s) ? 1 : 0;
        }
        return null;
    }
}
