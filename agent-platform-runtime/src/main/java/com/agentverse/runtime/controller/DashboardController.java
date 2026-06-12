/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.runtime.dto.DashboardStatsResponse;
import com.agentverse.runtime.mapper.AgentDefinitionMapper;
import com.agentverse.runtime.model.service.AgentInstanceService;
import com.agentverse.runtime.model.service.ChatUsageService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.HashMap;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1/dashboard"})
public class DashboardController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final AgentDefinitionMapper agentDefinitionMapper;
    private final AgentInstanceService agentInstanceService;
    private final ChatUsageService chatUsageService;

    @GetMapping(value={"/stats"})
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();
        HashMap<String, Long> agentCount = new HashMap<String, Long>();
        agentCount.put("draft", 0L);
        agentCount.put("active", 0L);
        agentCount.put("archived", 0L);
        List allAgents = this.agentDefinitionMapper.selectList((Wrapper)new QueryWrapper().eq((Object)"deleted", (Object)0));
        for (AgentDefinition agent : allAgents) {
            String status = agent.getStatus() != null ? agent.getStatus() : "draft";
            agentCount.merge(status, 1L, Long::sum);
        }
        response.setAgentCount(agentCount);
        response.setActiveSessions(this.agentInstanceService.countActive());
        response.setTodayChatCount(0L);
        DashboardStatsResponse.TokenUsage usage = new DashboardStatsResponse.TokenUsage();
        usage.setInputTokens(0L);
        usage.setOutputTokens(0L);
        response.setTokenUsage(usage);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Generated
    public DashboardController(AgentDefinitionMapper agentDefinitionMapper, AgentInstanceService agentInstanceService, ChatUsageService chatUsageService) {
        this.agentDefinitionMapper = agentDefinitionMapper;
        this.agentInstanceService = agentInstanceService;
        this.chatUsageService = chatUsageService;
    }
}

