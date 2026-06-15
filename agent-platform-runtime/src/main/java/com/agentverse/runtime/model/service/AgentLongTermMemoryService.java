/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.service;

import com.agentverse.runtime.model.entity.AgentLongTermMemory;
import com.agentverse.runtime.model.mapper.AgentLongTermMemoryMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AgentLongTermMemoryService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AgentLongTermMemoryService.class);
    private final AgentLongTermMemoryMapper agentLongTermMemoryMapper;

    public AgentLongTermMemory saveMemory(String agentId, String memoryType, String content) {
        AgentLongTermMemory memory = new AgentLongTermMemory();
        memory.setId(UUID.randomUUID().toString());
        memory.setAgentId(agentId);
        memory.setMemoryType(memoryType);
        memory.setContent(content);
        memory.setCreatedTime(LocalDateTime.now());
        this.agentLongTermMemoryMapper.insert(memory);
        return memory;
    }

    public List<AgentLongTermMemory> getByAgentId(String agentId) {
        QueryWrapper wrapper = new QueryWrapper();
        ((QueryWrapper)wrapper.eq((Object)"agent_id", (Object)agentId)).orderByDesc((Object)"created_time");
        return this.agentLongTermMemoryMapper.selectList((Wrapper)wrapper);
    }

    public void deleteByAgentId(String agentId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq((Object)"agent_id", (Object)agentId);
        this.agentLongTermMemoryMapper.delete((Wrapper)wrapper);
    }

    @Generated
    public AgentLongTermMemoryService(AgentLongTermMemoryMapper agentLongTermMemoryMapper) {
        this.agentLongTermMemoryMapper = agentLongTermMemoryMapper;
    }
}

