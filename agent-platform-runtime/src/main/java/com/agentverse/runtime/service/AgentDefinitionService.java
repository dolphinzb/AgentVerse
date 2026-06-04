package com.agentverse.runtime.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.agentverse.common.dto.AgentCreateRequest;
import com.agentverse.common.dto.AgentListResponse;
import com.agentverse.common.dto.AgentResponse;
import com.agentverse.common.dto.AgentUpdateRequest;
import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.mapper.AgentDefinitionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent 定义服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentDefinitionService {

    private final AgentDefinitionMapper agentDefinitionMapper;

    /**
     * 创建 Agent
     */
    @Transactional(rollbackFor = Exception.class)
    public AgentResponse createAgent(AgentCreateRequest request) {
        log.info("Creating agent: {}", request.getName());

        AgentDefinition agent = new AgentDefinition();
        agent.setId(UUID.randomUUID().toString());
        agent.setName(request.getName());
        agent.setDescription(request.getDescription());
        agent.setSysPrompt(request.getSysPrompt());
        agent.setMaxIterations(request.getMaxIterations() != null ? request.getMaxIterations() : 10);
        agent.setWorkspaceMode("isolated");
        agent.setStatus("draft");
        agent.setCreatedTime(LocalDateTime.now());
        agent.setUpdatedTime(LocalDateTime.now());
        agent.setCreatedBy(UserContext.getUserId());

        agentDefinitionMapper.insert(agent);

        log.info("Agent created successfully: {}", agent.getId());
        return convertToResponse(agent);
    }

    /**
     * 根据 ID 查询 Agent（含数据隔离）
     */
    public AgentResponse getAgentById(String id) {
        log.info("Getting agent by id: {}", id);

        AgentDefinition agent = agentDefinitionMapper.selectById(id);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }

        // 数据隔离：非 admin 只能查看自己的 Agent
        checkDataAccess(agent);

        return convertToResponse(agent);
    }

    /**
     * 分页查询 Agent 列表（含数据隔离）
     */
    public AgentListResponse listAgents(Integer page, Integer pageSize, String status) {
        log.info("Listing agents: page={}, pageSize={}, status={}", page, pageSize, status);

        Page<AgentDefinition> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<AgentDefinition> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(status)) {
            queryWrapper.eq(AgentDefinition::getStatus, status);
        }

        // 数据隔离：非 admin 只能看到自己的 Agent
        if (!UserContext.isAdmin()) {
            // created_by 是 VARCHAR(64)，需要转换为字符串比较
            Long userId = UserContext.getUserId();
            if (userId != null) {
                queryWrapper.eq(AgentDefinition::getCreatedBy, userId.toString());
            }
        }

        queryWrapper.orderByDesc(AgentDefinition::getCreatedTime);

        Page<AgentDefinition> result = agentDefinitionMapper.selectPage(pageParam, queryWrapper);

        List<AgentResponse> agents = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new AgentListResponse(agents, result.getTotal(), page, pageSize);
    }

    /**
     * 更新 Agent（含数据隔离）
     */
    @Transactional(rollbackFor = Exception.class)
    public AgentResponse updateAgent(String id, AgentUpdateRequest request) {
        log.info("Updating agent: {}", id);

        AgentDefinition agent = agentDefinitionMapper.selectById(id);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }

        checkDataAccess(agent);

        if (StringUtils.hasText(request.getName())) {
            agent.setName(request.getName());
        }
        if (request.getDescription() != null) {
            agent.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getSysPrompt())) {
            agent.setSysPrompt(request.getSysPrompt());
        }
        if (request.getMaxIterations() != null) {
            agent.setMaxIterations(request.getMaxIterations());
        }
        if (StringUtils.hasText(request.getWorkspaceMode())) {
            agent.setWorkspaceMode(request.getWorkspaceMode());
        }

        agent.setUpdatedTime(LocalDateTime.now());
        agentDefinitionMapper.updateById(agent);

        log.info("Agent updated successfully: {}", id);
        return convertToResponse(agent);
    }

    /**
     * 删除 Agent（含数据隔离）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAgent(String id) {
        log.info("Deleting agent: {}", id);

        AgentDefinition agent = agentDefinitionMapper.selectById(id);
        if (agent == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }

        checkDataAccess(agent);

        agentDefinitionMapper.deleteById(id);
        log.info("Agent deleted successfully: {}", id);
    }

    /**
     * 数据隔离校验：非 admin 用户只能操作自己的 Agent
     * createdBy 为 null 的记录也拒绝非 admin 访问
     */
    private void checkDataAccess(AgentDefinition agent) {
        if (UserContext.isAdmin()) {
            return;
        }
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null || agent.getCreatedBy() == null
                || !agent.getCreatedBy().equals(currentUserId.toString())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    /**
     * 转换为响应对象
     */
    private AgentResponse convertToResponse(AgentDefinition agent) {
        AgentResponse response = new AgentResponse();
        response.setId(agent.getId());
        response.setName(agent.getName());
        response.setDescription(agent.getDescription());
        response.setSysPrompt(agent.getSysPrompt());
        response.setMaxIterations(agent.getMaxIterations());
        response.setWorkspaceMode(agent.getWorkspaceMode());
        response.setStatus(agent.getStatus());
        response.setCurrentVersion(agent.getCurrentVersion());
        response.setCreatedTime(agent.getCreatedTime());
        response.setUpdatedTime(agent.getUpdatedTime());
        return response;
    }
}
