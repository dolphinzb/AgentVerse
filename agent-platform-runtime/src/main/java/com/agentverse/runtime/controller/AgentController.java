/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.controller;

import com.agentverse.common.dto.AgentCreateRequest;
import com.agentverse.common.dto.AgentListResponse;
import com.agentverse.common.dto.AgentPublishRequest;
import com.agentverse.common.dto.AgentResponse;
import com.agentverse.common.dto.AgentUpdateRequest;
import com.agentverse.common.dto.AgentVersionResponse;
import com.agentverse.common.dto.ApiResponse;
import com.agentverse.runtime.security.RequirePermission;
import com.agentverse.runtime.service.AgentDefinitionService;
import com.agentverse.runtime.service.AgentVersionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/agents"})
public class AgentController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AgentController.class);
    private final AgentDefinitionService agentDefinitionService;
    private final AgentVersionService agentVersionService;

    @RequirePermission(value="agent:create")
    @PostMapping
    public ResponseEntity<ApiResponse<AgentResponse>> createAgent(@Valid @RequestBody AgentCreateRequest request) {
        log.info("Received create agent request: {}", (Object)request.getName());
        AgentResponse agent = this.agentDefinitionService.createAgent(request);
        return ResponseEntity.ok(ApiResponse.success(agent));
    }

    @RequirePermission(value="agent:read")
    @GetMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<AgentResponse>> getAgentById(@PathVariable(value="id") String id) {
        log.info("Received get agent request: {}", (Object)id);
        AgentResponse agent = this.agentDefinitionService.getAgentById(id);
        return ResponseEntity.ok(ApiResponse.success(agent));
    }

    @RequirePermission(value="agent:read")
    @GetMapping
    public ResponseEntity<ApiResponse<AgentListResponse>> listAgents(@RequestParam(name="page", defaultValue="1") Integer page, @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, @RequestParam(name="status", required=false) String status) {
        log.info("Received list agents request: page={}, pageSize={}, status={}", new Object[]{page, pageSize, status});
        AgentListResponse result = this.agentDefinitionService.listAgents(page, pageSize, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @RequirePermission(value="agent:update")
    @PutMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<AgentResponse>> updateAgent(@PathVariable(value="id") String id, @Valid @RequestBody AgentUpdateRequest request) {
        log.info("Received update agent request: {}", (Object)id);
        AgentResponse agent = this.agentDefinitionService.updateAgent(id, request);
        return ResponseEntity.ok(ApiResponse.success(agent));
    }

    @RequirePermission(value="agent:delete")
    @DeleteMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<Void>> deleteAgent(@PathVariable(value="id") String id) {
        log.info("Received delete agent request: {}", (Object)id);
        this.agentDefinitionService.deleteAgent(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @RequirePermission(value="agent:publish")
    @PostMapping(value={"/{id}/publish"})
    public ResponseEntity<ApiResponse<AgentVersionResponse>> publishVersion(@PathVariable(value="id") String id, @Valid @RequestBody AgentPublishRequest request) {
        log.info("Received publish version request for agent {}: {}", (Object)id, (Object)request.getVersion());
        AgentVersionResponse version = this.agentVersionService.publishVersion(id, request);
        return ResponseEntity.ok(ApiResponse.success(version));
    }

    @RequirePermission(value="agent:read")
    @GetMapping(value={"/{id}/versions"})
    public ResponseEntity<ApiResponse<List<AgentVersionResponse>>> listVersions(@PathVariable(value="id") String id) {
        log.info("Received list versions request for agent {}", (Object)id);
        List<AgentVersionResponse> versions = this.agentVersionService.listVersions(id);
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    @RequirePermission(value="agent:update")
    @PostMapping(value={"/{id}/rollback"})
    public ResponseEntity<ApiResponse<AgentVersionResponse>> rollbackVersion(@PathVariable(value="id") String id, @RequestParam(value="version") String version) {
        log.info("Received rollback request for agent {} to version {}", (Object)id, (Object)version);
        AgentVersionResponse versionResponse = this.agentVersionService.rollbackVersion(id, version);
        return ResponseEntity.ok(ApiResponse.success(versionResponse));
    }

    @RequirePermission(value="agent:publish")
    @PostMapping(value={"/{id}/lifecycle/publish"})
    public ResponseEntity<ApiResponse<Void>> publishAgent(@PathVariable(value="id") String id) {
        log.info("Received publish agent request: {}", (Object)id);
        this.agentDefinitionService.publishAgent(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @RequirePermission(value="agent:archive")
    @PostMapping(value={"/{id}/lifecycle/archive"})
    public ResponseEntity<ApiResponse<Void>> archiveAgent(@PathVariable(value="id") String id) {
        log.info("Received archive agent request: {}", (Object)id);
        this.agentDefinitionService.archiveAgent(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @RequirePermission(value="agent:publish")
    @PostMapping(value={"/{id}/lifecycle/reactivate"})
    public ResponseEntity<ApiResponse<Void>> reactivateAgent(@PathVariable(value="id") String id) {
        log.info("Received reactivate agent request: {}", (Object)id);
        this.agentDefinitionService.reactivateAgent(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Generated
    public AgentController(AgentDefinitionService agentDefinitionService, AgentVersionService agentVersionService) {
        this.agentDefinitionService = agentDefinitionService;
        this.agentVersionService = agentVersionService;
    }
}

