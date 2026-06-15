/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.runtime.engine.TokenUsageHook;
import com.agentverse.runtime.workspace.WorkspaceManager;
import io.agentscope.core.hook.Hook;
import io.agentscope.core.memory.LongTermMemoryMode;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.core.model.Model;
import io.agentscope.core.session.Session;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.filesystem.spec.LocalFilesystemSpec;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;
import io.agentscope.harness.agent.memory.compaction.ToolResultEvictionConfig;
import java.nio.file.Path;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class AgentConfigAssembler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AgentConfigAssembler.class);
    private final WorkspaceManager workspaceManager;
    @Nullable
    @Autowired(required=false)
    private Session agentScopeSession;
    @Autowired
    private TokenUsageHook tokenUsageHook;

    public HarnessAgent assemble(AgentDefinition def, ChatModelBase model, String userId) {
        HarnessAgent.Builder builder = HarnessAgent.builder().name(def.getName()).description(def.getDescription() != null ? def.getDescription() : "").sysPrompt(def.getSysPrompt() != null ? def.getSysPrompt() : "You are a helpful assistant.").maxIters(def.getMaxIterations() != null ? def.getMaxIterations() : 10).model((Model)model);
        try {
            Path workspace = this.workspaceManager.resolve(userId, def.getId());
            builder.workspace(workspace);
            log.debug("Configured workspace: {}", (Object)workspace);
        }
        catch (Exception e) {
            log.warn("Failed to configure workspace for agent: {}, error: {}", (Object)def.getId(), (Object)e.getMessage());
        }
        try {
            String fsType;
            String string = fsType = def.getFilesystemType() != null ? def.getFilesystemType() : "local";
            if ("local".equals(fsType)) {
                Path workspacePath = this.workspaceManager.getPath(userId, def.getId());
                builder.filesystem(new LocalFilesystemSpec());
                log.debug("Configured local filesystem");
            } else {
                log.warn("Unsupported filesystem type: {}, skipping filesystem config", (Object)fsType);
            }
        }
        catch (Exception e) {
            log.warn("Failed to configure filesystem for agent: {}, error: {}", (Object)def.getId(), (Object)e.getMessage());
        }
        if (!this.isTrue(def.getEnableMemoryFlush())) {
            try {
                builder.disableMemoryHooks();
                log.debug("Disabled memory hooks");
            }
            catch (Exception e) {
                log.warn("Failed to disable memory hooks: {}", (Object)e.getMessage());
            }
        }
        if (this.isTrue(def.getEnableCompaction())) {
            try {
                CompactionConfig compactionConfig = CompactionConfig.builder().triggerMessages(def.getCompactionTriggerPct() != null ? def.getCompactionTriggerPct() : 80).keepMessages(def.getCompactionKeepRecent() != null ? def.getCompactionKeepRecent() : 10).build();
                builder.compaction(compactionConfig);
                log.debug("Configured compaction: triggerMessages={}, keepMessages={}", (Object)def.getCompactionTriggerPct(), (Object)def.getCompactionKeepRecent());
            }
            catch (Exception e) {
                log.warn("Failed to configure compaction for agent: {}, error: {}", (Object)def.getId(), (Object)e.getMessage());
            }
        }
        if (this.isTrue(def.getEnableToolResultEviction())) {
            try {
                ToolResultEvictionConfig evictionConfig = ToolResultEvictionConfig.builder().maxResultChars(def.getToolResultEvictionMaxChars() != null ? def.getToolResultEvictionMaxChars() : 4000).build();
                builder.toolResultEviction(evictionConfig);
                log.debug("Configured tool result eviction: maxChars={}", (Object)def.getToolResultEvictionMaxChars());
            }
            catch (Exception e) {
                log.warn("Failed to configure tool result eviction for agent: {}, error: {}", (Object)def.getId(), (Object)e.getMessage());
            }
        }
        if (this.isTrue(def.getEnableLongTermMemory())) {
            try {
                builder.longTermMemoryMode(LongTermMemoryMode.BOTH);
                log.debug("Configured long term memory: mode=BOTH");
            }
            catch (Exception e) {
                log.warn("Failed to configure long term memory for agent: {}, error: {}", (Object)def.getId(), (Object)e.getMessage());
            }
        }
        if (this.isTrue(def.getEnablePlan())) {
            try {
                builder.enablePlan();
                log.debug("Configured plan notebook");
            }
            catch (Exception e) {
                log.warn("Failed to configure plan notebook for agent: {}, error: {}", (Object)def.getId(), (Object)e.getMessage());
            }
        }
        if (!this.isTrue(def.getEnableSessionPersistence())) {
            try {
                builder.disableSessionPersistence();
                log.debug("Disabled session persistence");
            }
            catch (Exception e) {
                log.warn("Failed to disable session persistence: {}", (Object)e.getMessage());
            }
        } else if (this.agentScopeSession != null) {
            try {
                builder.session(this.agentScopeSession);
                log.info("Configured external Session for persistence: {}", (Object)this.agentScopeSession.getClass().getSimpleName());
            }
            catch (Exception e) {
                log.warn("Failed to configure session: {}", (Object)e.getMessage());
            }
        } else {
            log.debug("Session persistence enabled with HarnessAgent default (JsonSession)");
        }
        if (def.getMaxContextTokens() != null) {
            try {
                builder.maxContextTokens(def.getMaxContextTokens().intValue());
                log.debug("Configured max context tokens: {}", (Object)def.getMaxContextTokens());
            }
            catch (Exception e) {
                log.warn("Failed to configure max context tokens: {}, error: {}", (Object)def.getMaxContextTokens(), (Object)e.getMessage());
            }
        }
        try {
            builder.hook((Hook)this.tokenUsageHook);
            log.debug("Registered TokenUsageHook");
        }
        catch (Exception e) {
            log.warn("Failed to register TokenUsageHook: {}", (Object)e.getMessage());
        }
        return builder.build();
    }

    private boolean isTrue(Integer value) {
        return value != null && value == 1;
    }

    @Generated
    public AgentConfigAssembler(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }
}

