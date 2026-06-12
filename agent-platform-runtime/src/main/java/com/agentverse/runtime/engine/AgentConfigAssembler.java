package com.agentverse.runtime.engine;

import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.runtime.workspace.WorkspaceManager;
import io.agentscope.core.memory.LongTermMemoryMode;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.filesystem.spec.LocalFilesystemSpec;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;
import io.agentscope.harness.agent.memory.compaction.ToolResultEvictionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Agent 配置组装器，封装 HarnessAgent Builder 调用
 * 隔离 agentscope API 变更，集中管理 Builder 配置逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentConfigAssembler {

    private final WorkspaceManager workspaceManager;

    /**
     * 组装 HarnessAgent
     *
     * @param def    Agent 定义
     * @param model  聊天模型
     * @param userId 用户 ID
     * @return 配置完成的 HarnessAgent
     */
    public HarnessAgent assemble(AgentDefinition def, ChatModelBase model, String userId) {
        HarnessAgent.Builder builder = HarnessAgent.builder()
                .name(def.getName())
                .description(def.getDescription() != null ? def.getDescription() : "")
                .sysPrompt(def.getSysPrompt() != null ? def.getSysPrompt() : "You are a helpful assistant.")
                .maxIters(def.getMaxIterations() != null ? def.getMaxIterations() : 10)
                .model(model);

        // 工作区配置
        try {
            Path workspace = workspaceManager.resolve(userId, def.getId());
            builder.workspace(workspace);
            log.debug("Configured workspace: {}", workspace);
        } catch (Exception e) {
            log.warn("Failed to configure workspace for agent: {}, error: {}", def.getId(), e.getMessage());
        }

        // 文件系统配置
        try {
            String fsType = def.getFilesystemType() != null ? def.getFilesystemType() : "local";
            if ("local".equals(fsType)) {
                Path workspacePath = workspaceManager.getPath(userId, def.getId());
                builder.filesystem(new LocalFilesystemSpec());
                log.debug("Configured local filesystem");
            } else {
                log.warn("Unsupported filesystem type: {}, skipping filesystem config", fsType);
            }
        } catch (Exception e) {
            log.warn("Failed to configure filesystem for agent: {}, error: {}", def.getId(), e.getMessage());
        }

        // 记忆配置
        if (!isTrue(def.getEnableMemoryFlush())) {
            try {
                builder.disableMemoryHooks();
                log.debug("Disabled memory hooks");
            } catch (Exception e) {
                log.warn("Failed to disable memory hooks: {}", e.getMessage());
            }
        }

        // 上下文压缩
        if (isTrue(def.getEnableCompaction())) {
            try {
                CompactionConfig compactionConfig = CompactionConfig.builder()
                        .triggerMessages(def.getCompactionTriggerPct() != null ? def.getCompactionTriggerPct() : 80)
                        .keepMessages(def.getCompactionKeepRecent() != null ? def.getCompactionKeepRecent() : 10)
                        .build();
                builder.compaction(compactionConfig);
                log.debug("Configured compaction: triggerMessages={}, keepMessages={}",
                        def.getCompactionTriggerPct(), def.getCompactionKeepRecent());
            } catch (Exception e) {
                log.warn("Failed to configure compaction for agent: {}, error: {}", def.getId(), e.getMessage());
            }
        }

        // 工具结果驱逐
        if (isTrue(def.getEnableToolResultEviction())) {
            try {
                ToolResultEvictionConfig evictionConfig = ToolResultEvictionConfig.builder()
                        .maxResultChars(def.getToolResultEvictionMaxChars() != null ? def.getToolResultEvictionMaxChars() : 4000)
                        .build();
                builder.toolResultEviction(evictionConfig);
                log.debug("Configured tool result eviction: maxChars={}", def.getToolResultEvictionMaxChars());
            } catch (Exception e) {
                log.warn("Failed to configure tool result eviction for agent: {}, error: {}", def.getId(), e.getMessage());
            }
        }

        // 长期记忆
        if (isTrue(def.getEnableLongTermMemory())) {
            try {
                builder.longTermMemoryMode(LongTermMemoryMode.BOTH);
                log.debug("Configured long term memory: mode=BOTH");
            } catch (Exception e) {
                log.warn("Failed to configure long term memory for agent: {}, error: {}", def.getId(), e.getMessage());
            }
        }

        // 计划执行
        if (isTrue(def.getEnablePlan())) {
            try {
                builder.enablePlan();
                log.debug("Configured plan notebook");
            } catch (Exception e) {
                log.warn("Failed to configure plan notebook for agent: {}, error: {}", def.getId(), e.getMessage());
            }
        }

        // 会话持久化
        if (!isTrue(def.getEnableSessionPersistence())) {
            try {
                builder.disableSessionPersistence();
                log.debug("Disabled session persistence");
            } catch (Exception e) {
                log.warn("Failed to disable session persistence: {}", e.getMessage());
            }
        }

        // 最大上下文 Token 数
        if (def.getMaxContextTokens() != null) {
            try {
                builder.maxContextTokens(def.getMaxContextTokens());
                log.debug("Configured max context tokens: {}", def.getMaxContextTokens());
            } catch (Exception e) {
                log.warn("Failed to configure max context tokens: {}", e.getMessage());
            }
        }

        return builder.build();
    }

    private boolean isTrue(Integer value) {
        return value != null && value == 1;
    }
}
