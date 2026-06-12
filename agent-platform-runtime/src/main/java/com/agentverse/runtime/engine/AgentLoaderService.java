/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.runtime.engine.AgentConfigAssembler;
import com.agentverse.runtime.engine.ModelFactory;
import com.agentverse.runtime.mapper.AgentDefinitionMapper;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.harness.agent.HarnessAgent;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AgentLoaderService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AgentLoaderService.class);
    private final AgentDefinitionMapper agentDefinitionMapper;
    private final ModelFactory modelFactory;
    private final AgentConfigAssembler agentConfigAssembler;
    private final ConcurrentHashMap<String, HarnessAgent> agentCache = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, String> modelConfigIdCache = new ConcurrentHashMap();

    public HarnessAgent loadAgent(String agentId) {
        return this.agentCache.computeIfAbsent(agentId, this::buildAgent);
    }

    public HarnessAgent loadAgent(String agentId, String userId) {
        return this.agentCache.computeIfAbsent(agentId, id -> this.buildAgentWithUser((String)id, userId));
    }

    public String getModelConfigId(String agentId) {
        return this.modelConfigIdCache.get(agentId);
    }

    public void evictAgent(String agentId) {
        this.agentCache.remove(agentId);
        this.modelConfigIdCache.remove(agentId);
        log.info("Evicted agent from cache: agentId={}", (Object)agentId);
    }

    private HarnessAgent buildAgent(String agentId) {
        return this.buildAgentWithUser(agentId, null);
    }

    private HarnessAgent buildAgentWithUser(String agentId, String userId) {
        log.info("Building HarnessAgent for agentId: {}", (Object)agentId);
        AgentDefinition definition = (AgentDefinition)this.agentDefinitionMapper.selectById((Serializable)((Object)agentId));
        if (definition == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }
        if ("draft".equals(definition.getStatus())) {
            throw new BizException(ErrorCode.AGENT_NOT_PUBLISHED);
        }
        if ("archived".equals(definition.getStatus())) {
            throw new BizException(ErrorCode.AGENT_ARCHIVED);
        }
        ChatModelBase model = this.modelFactory.getModel(definition.getModelConfigId());
        this.modelConfigIdCache.put(agentId, definition.getModelConfigId());
        String effectiveUserId = userId != null ? userId : (definition.getCreatedBy() != null ? definition.getCreatedBy().toString() : "default");
        HarnessAgent agent = this.agentConfigAssembler.assemble(definition, model, effectiveUserId);
        log.info("HarnessAgent built successfully for agentId: {}", (Object)agentId);
        return agent;
    }

    @Generated
    public AgentLoaderService(AgentDefinitionMapper agentDefinitionMapper, ModelFactory modelFactory, AgentConfigAssembler agentConfigAssembler) {
        this.agentDefinitionMapper = agentDefinitionMapper;
        this.modelFactory = modelFactory;
        this.agentConfigAssembler = agentConfigAssembler;
    }
}

