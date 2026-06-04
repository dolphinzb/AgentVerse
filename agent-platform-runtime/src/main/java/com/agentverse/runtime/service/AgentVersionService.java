package com.agentverse.runtime.service;

import com.agentverse.common.dto.AgentPublishRequest;
import com.agentverse.common.dto.AgentVersionResponse;
import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.common.entity.AgentVersion;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Agent 版本服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentVersionService {

    private final AgentVersionMapper agentVersionMapper;
    private final AgentDefinitionMapper agentDefinitionMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * 发布版本
     */
    @Transactional(rollbackFor = Exception.class)
    public AgentVersionResponse publishVersion(String agentId, AgentPublishRequest request) {
        log.info("Publishing version for agent {}: {}", agentId, request.getVersion());

        // 验证 Agent 存在
        AgentDefinition agent = agentDefinitionMapper.selectById(agentId);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }

        // 数据隔离校验
        checkDataAccess(agent);

        // 检查版本号唯一
        LambdaQueryWrapper<AgentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentVersion::getAgentId, agentId)
                .eq(AgentVersion::getVersion, request.getVersion());
        if (agentVersionMapper.selectCount(wrapper) > 0) {
            throw new BizException(ErrorCode.AGENT_VERSION_DUPLICATE, request.getVersion());
        }

        // 序列化 Agent 配置为 JSON 快照
        String snapshotData = serializeSnapshot(agent);

        // 创建版本记录
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

        // 更新 Agent 的 current_version 和 status
        agent.setCurrentVersion(request.getVersion());
        agent.setStatus("published");
        agent.setUpdatedTime(LocalDateTime.now());
        agentDefinitionMapper.updateById(agent);

        log.info("Version published successfully: {} for agent {}", request.getVersion(), agentId);
        return convertToResponse(version);
    }

    /**
     * 查询版本历史
     */
    public List<AgentVersionResponse> listVersions(String agentId) {
        log.info("Listing versions for agent {}", agentId);

        // 验证 Agent 存在
        AgentDefinition agent = agentDefinitionMapper.selectById(agentId);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }

        LambdaQueryWrapper<AgentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentVersion::getAgentId, agentId)
                .orderByDesc(AgentVersion::getCreatedTime);

        List<AgentVersion> versions = agentVersionMapper.selectList(wrapper);
        return versions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 回滚到指定版本
     */
    @Transactional(rollbackFor = Exception.class)
    public AgentVersionResponse rollbackVersion(String agentId, String targetVersion) {
        log.info("Rolling back agent {} to version {}", agentId, targetVersion);

        // 验证 Agent 存在
        AgentDefinition agent = agentDefinitionMapper.selectById(agentId);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }

        // 数据隔离校验
        checkDataAccess(agent);

        // 查找目标版本
        LambdaQueryWrapper<AgentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentVersion::getAgentId, agentId)
                .eq(AgentVersion::getVersion, targetVersion);
        AgentVersion version = agentVersionMapper.selectOne(wrapper);
        if (version == null) {
            throw new BizException(ErrorCode.AGENT_VERSION_NOT_FOUND, targetVersion);
        }

        // 解析快照数据并更新 Agent
        applySnapshot(agent, version.getSnapshotData());
        agent.setCurrentVersion(targetVersion);
        agent.setUpdatedTime(LocalDateTime.now());
        agentDefinitionMapper.updateById(agent);

        log.info("Agent {} rolled back to version {}", agentId, targetVersion);
        return convertToResponse(version);
    }

    /**
     * 数据隔离校验：非 admin 用户只能操作自己的 Agent
     */
    private void checkDataAccess(AgentDefinition agent) {
        if (!UserContext.isAdmin() && agent.getCreatedBy() != null
                && !agent.getCreatedBy().equals(UserContext.getUserId())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    /**
     * 序列化 Agent 配置为 JSON 快照
     */
    private String serializeSnapshot(AgentDefinition agent) {
        try {
            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("name", agent.getName());
            snapshot.put("description", agent.getDescription());
            snapshot.put("sysPrompt", agent.getSysPrompt());
            snapshot.put("maxIterations", agent.getMaxIterations());
            snapshot.put("workspaceMode", agent.getWorkspaceMode());
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR.getCode(), "Failed to serialize agent snapshot: " + e.getMessage());
        }
    }

    /**
     * 从快照数据恢复 Agent 配置
     */
    @SuppressWarnings("unchecked")
    private void applySnapshot(AgentDefinition agent, String snapshotData) {
        try {
            Map<String, Object> snapshot = objectMapper.readValue(snapshotData, Map.class);
            if (snapshot.containsKey("name")) {
                agent.setName((String) snapshot.get("name"));
            }
            if (snapshot.containsKey("description")) {
                agent.setDescription((String) snapshot.get("description"));
            }
            if (snapshot.containsKey("sysPrompt")) {
                agent.setSysPrompt((String) snapshot.get("sysPrompt"));
            }
            if (snapshot.containsKey("maxIterations")) {
                agent.setMaxIterations(((Number) snapshot.get("maxIterations")).intValue());
            }
            if (snapshot.containsKey("workspaceMode")) {
                agent.setWorkspaceMode((String) snapshot.get("workspaceMode"));
            }
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR.getCode(), "Failed to parse agent snapshot: " + e.getMessage());
        }
    }

    /**
     * 转换为响应对象
     */
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
}
