/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.service;

import com.agentverse.runtime.model.entity.AgentInstance;
import com.agentverse.runtime.model.mapper.AgentInstanceMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        log.info("Created agent instance: agentId={}, sessionId={}", (Object)agentId, (Object)sessionId);
        return instance;
    }

    public void stopInstance(String agentId) {
        QueryWrapper wrapper = new QueryWrapper();
        ((QueryWrapper)wrapper.eq((Object)"agent_id", (Object)agentId)).eq((Object)"status", (Object)"active");
        AgentInstance instance = (AgentInstance)this.agentInstanceMapper.selectOne((Wrapper)wrapper);
        if (instance != null) {
            instance.setStatus("stopped");
            instance.setLastActiveAt(LocalDateTime.now());
            this.agentInstanceMapper.updateById(instance);
            log.info("Stopped agent instance: agentId={}", (Object)agentId);
        }
    }

    public long countActive() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq((Object)"status", (Object)"active");
        return this.agentInstanceMapper.selectCount((Wrapper)wrapper);
    }

    @Generated
    public AgentInstanceService(AgentInstanceMapper agentInstanceMapper) {
        this.agentInstanceMapper = agentInstanceMapper;
    }
}

