/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.agentverse.runtime.model.entity.AgentInstance;
import com.agentverse.runtime.model.mapper.AgentInstanceMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.Generated;

@Service
public class AgentInstanceService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AgentInstanceService.class);
    private final AgentInstanceMapper agentInstanceMapper;

    public AgentInstance createInstance(String agentId, String sessionId) {
        AgentInstance instance = new AgentInstance();
        instance.setId(UUID.randomUUID().toString());
        instance.setAgentId(agentId);
        instance.setSessionId(sessionId);
        instance.setStatus("active");
        instance.setStartedAt(LocalDateTime.now());
        instance.setLastActiveAt(LocalDateTime.now());
        this.agentInstanceMapper.insert(instance);
        log.info("Created agent instance: agentId={}, sessionId={}", agentId, sessionId);
        return instance;
    }

    public void stopInstance(String agentId) {
        QueryWrapper<AgentInstance> wrapper = new QueryWrapper<>();
        wrapper.eq("agent_id", agentId).eq("status", "active");
        AgentInstance instance = this.agentInstanceMapper.selectOne(wrapper);
        if (instance != null) {
            instance.setStatus("stopped");
            instance.setLastActiveAt(LocalDateTime.now());
            this.agentInstanceMapper.updateById(instance);
            log.info("Stopped agent instance: agentId={}", agentId);
        }
    }

    public long countActive() {
        QueryWrapper<AgentInstance> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "active");
        return this.agentInstanceMapper.selectCount(wrapper);
    }

    @Generated
    public AgentInstanceService(AgentInstanceMapper agentInstanceMapper) {
        this.agentInstanceMapper = agentInstanceMapper;
    }
}
