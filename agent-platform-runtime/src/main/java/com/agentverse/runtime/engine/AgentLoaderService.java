package com.agentverse.runtime.engine;

import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.runtime.mapper.AgentDefinitionMapper;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.harness.agent.HarnessAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent 加载服务，从数据库配置构建可执行的 HarnessAgent 实例
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentLoaderService {

    private final AgentDefinitionMapper agentDefinitionMapper;
    private final ModelFactory modelFactory;

    /**
     * Agent 实例缓存
     */
    private final ConcurrentHashMap<String, HarnessAgent> agentCache = new ConcurrentHashMap<>();

    /**
     * 加载或从缓存获取 HarnessAgent
     */
    public HarnessAgent loadAgent(String agentId) {
        return agentCache.computeIfAbsent(agentId, this::buildAgent);
    }

    /**
     * 从缓存中移除 Agent 实例
     */
    public void evictAgent(String agentId) {
        agentCache.remove(agentId);
    }

    /**
     * 从数据库读取配置并构建 HarnessAgent
     */
    private HarnessAgent buildAgent(String agentId) {
        log.info("Building HarnessAgent for agentId: {}", agentId);

        AgentDefinition definition = agentDefinitionMapper.selectById(agentId);
        if (definition == null) {
            throw new BizException(ErrorCode.AGENT_NOT_FOUND);
        }

        Object model = modelFactory.createModel();

        HarnessAgent.Builder builder = HarnessAgent.builder()
                .name(definition.getName())
                .description(definition.getDescription() != null ? definition.getDescription() : "")
                .sysPrompt(definition.getSysPrompt() != null ? definition.getSysPrompt() : "You are a helpful assistant.")
                .maxIters(definition.getMaxIterations() != null ? definition.getMaxIterations() : 10);

        if (model instanceof DashScopeChatModel dashScopeModel) {
            builder.model(dashScopeModel);
        } else if (model instanceof OpenAIChatModel openAIModel) {
            builder.model(openAIModel);
        }

        HarnessAgent agent = builder.build();
        log.info("HarnessAgent built successfully for agentId: {}", agentId);
        return agent;
    }
}
