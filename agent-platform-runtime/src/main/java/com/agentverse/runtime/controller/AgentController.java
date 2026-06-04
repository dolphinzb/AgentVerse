package com.agentverse.runtime.controller;

import com.agentverse.common.dto.*;
import com.agentverse.runtime.security.RequirePermission;
import com.agentverse.runtime.service.AgentDefinitionService;
import com.agentverse.runtime.service.AgentVersionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Agent 控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentDefinitionService agentDefinitionService;
    private final AgentVersionService agentVersionService;

    /**
     * 创建 Agent
     */
    @RequirePermission("agent:create")
    @PostMapping
    public ResponseEntity<ApiResponse<AgentResponse>> createAgent(
            @Valid @RequestBody AgentCreateRequest request) {
        log.info("Received create agent request: {}", request.getName());
        AgentResponse agent = agentDefinitionService.createAgent(request);
        return ResponseEntity.ok(ApiResponse.success(agent));
    }

    /**
     * 根据 ID 查询 Agent
     */
    @RequirePermission("agent:read")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AgentResponse>> getAgentById(@PathVariable("id") String id) {
        log.info("Received get agent request: {}", id);
        AgentResponse agent = agentDefinitionService.getAgentById(id);
        return ResponseEntity.ok(ApiResponse.success(agent));
    }

    /**
     * 分页查询 Agent 列表
     */
    @RequirePermission("agent:read")
    @GetMapping
    public ResponseEntity<ApiResponse<AgentListResponse>> listAgents(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "status", required = false) String status) {
        log.info("Received list agents request: page={}, pageSize={}, status={}", page, pageSize, status);
        AgentListResponse result = agentDefinitionService.listAgents(page, pageSize, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 更新 Agent
     */
    @RequirePermission("agent:update")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AgentResponse>> updateAgent(
            @PathVariable("id") String id,
            @Valid @RequestBody AgentUpdateRequest request) {
        log.info("Received update agent request: {}", id);
        AgentResponse agent = agentDefinitionService.updateAgent(id, request);
        return ResponseEntity.ok(ApiResponse.success(agent));
    }

    /**
     * 删除 Agent
     */
    @RequirePermission("agent:delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAgent(@PathVariable("id") String id) {
        log.info("Received delete agent request: {}", id);
        agentDefinitionService.deleteAgent(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 发布版本
     */
    @RequirePermission("agent:publish")
    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<AgentVersionResponse>> publishVersion(
            @PathVariable("id") String id,
            @Valid @RequestBody AgentPublishRequest request) {
        log.info("Received publish version request for agent {}: {}", id, request.getVersion());
        AgentVersionResponse version = agentVersionService.publishVersion(id, request);
        return ResponseEntity.ok(ApiResponse.success(version));
    }

    /**
     * 查询版本历史
     */
    @RequirePermission("agent:read")
    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<List<AgentVersionResponse>>> listVersions(@PathVariable("id") String id) {
        log.info("Received list versions request for agent {}", id);
        List<AgentVersionResponse> versions = agentVersionService.listVersions(id);
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    /**
     * 回滚版本
     */
    @RequirePermission("agent:update")
    @PostMapping("/{id}/rollback")
    public ResponseEntity<ApiResponse<AgentVersionResponse>> rollbackVersion(
            @PathVariable("id") String id,
            @RequestParam("version") String version) {
        log.info("Received rollback request for agent {} to version {}", id, version);
        AgentVersionResponse versionResponse = agentVersionService.rollbackVersion(id, version);
        return ResponseEntity.ok(ApiResponse.success(versionResponse));
    }
}
