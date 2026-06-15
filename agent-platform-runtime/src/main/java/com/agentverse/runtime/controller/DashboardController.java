package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.entity.AgentDefinition;
import com.agentverse.runtime.dto.DashboardStatsResponse;
import com.agentverse.runtime.mapper.AgentDefinitionMapper;
import com.agentverse.runtime.model.service.AgentInstanceService;
import com.agentverse.runtime.model.service.ChatUsageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 仪表盘统计接口。
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AgentDefinitionMapper agentDefinitionMapper;
    private final AgentInstanceService agentInstanceService;
    private final ChatUsageService chatUsageService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();
        HashMap<String, Long> agentCount = new HashMap<>();
        agentCount.put("draft", 0L);
        agentCount.put("active", 0L);
        agentCount.put("archived", 0L);
        List<AgentDefinition> allAgents = agentDefinitionMapper.selectList(
                new QueryWrapper<AgentDefinition>().eq("deleted", 0));
        for (AgentDefinition agent : allAgents) {
            String status = agent.getStatus() != null ? agent.getStatus() : "draft";
            agentCount.merge(status, 1L, Long::sum);
        }
        response.setAgentCount(agentCount);
        response.setActiveSessions(agentInstanceService.countActive());
        response.setTodayChatCount(0L);
        DashboardStatsResponse.TokenUsage usage = new DashboardStatsResponse.TokenUsage();
        usage.setInputTokens(0L);
        usage.setOutputTokens(0L);
        response.setTokenUsage(usage);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
