/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.controller;

import java.util.List;

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

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.runtime.model.dto.ConnectionTestRequest;
import com.agentverse.runtime.model.dto.ConnectionTestResult;
import com.agentverse.runtime.model.dto.ProviderCreateRequest;
import com.agentverse.runtime.model.dto.ProviderPresetResponse;
import com.agentverse.runtime.model.dto.ProviderResponse;
import com.agentverse.runtime.model.dto.ProviderTypeResponse;
import com.agentverse.runtime.model.dto.ProviderUpdateRequest;
import com.agentverse.runtime.model.service.ModelProviderService;
import com.agentverse.runtime.security.RequirePermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.validation.Valid;
import lombok.Generated;

@RestController
@RequestMapping(value = { "/v1/model-providers" })
public class ModelProviderController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelProviderController.class);
    private final ModelProviderService modelProviderService;

    @RequirePermission(value = "model:read")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProviderResponse>>> listProviders(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "status", required = false) String status) {
        log.info("查询模型供应商列表: page={}, pageSize={}, status={}", new Object[] { page, pageSize, status });
        Page<ProviderResponse> result = this.modelProviderService.listProviders(page, pageSize, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @RequirePermission(value = "model:read")
    @GetMapping(value = { "/types" })
    public ResponseEntity<ApiResponse<List<ProviderTypeResponse>>> getTypes() {
        log.info("获取所有供应商类型");
        List<ProviderTypeResponse> types = this.modelProviderService.getTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @RequirePermission(value = "model:read")
    @GetMapping(value = { "/presets" })
    public ResponseEntity<ApiResponse<List<ProviderPresetResponse>>> getPresets() {
        log.info("获取预设供应商列表");
        List<ProviderPresetResponse> presets = this.modelProviderService.getPresets();
        return ResponseEntity.ok(ApiResponse.success(presets));
    }

    @RequirePermission(value = "model:read")
    @GetMapping(value = { "/{id}" })
    public ResponseEntity<ApiResponse<ProviderResponse>> getProviderById(@PathVariable(value = "id") String id) {
        log.info("查询模型供应商: {}", (Object) id);
        ProviderResponse provider = this.modelProviderService.getProviderById(id);
        return ResponseEntity.ok(ApiResponse.success(provider));
    }

    @RequirePermission(value = "model:create")
    @PostMapping
    public ResponseEntity<ApiResponse<ProviderResponse>> createProvider(
            @Valid @RequestBody ProviderCreateRequest request) {
        log.info("创建模型供应商: {}", (Object) request.getName());
        ProviderResponse provider = this.modelProviderService.createProvider(request);
        return ResponseEntity.ok(ApiResponse.success(provider));
    }

    @RequirePermission(value = "model:update")
    @PutMapping(value = { "/{id}" })
    public ResponseEntity<ApiResponse<ProviderResponse>> updateProvider(@PathVariable(value = "id") String id,
            @Valid @RequestBody ProviderUpdateRequest request) {
        log.info("更新模型供应商: {}", (Object) id);
        ProviderResponse provider = this.modelProviderService.updateProvider(id, request);
        return ResponseEntity.ok(ApiResponse.success(provider));
    }

    @RequirePermission(value = "model:delete")
    @DeleteMapping(value = { "/{id}" })
    public ResponseEntity<ApiResponse<Void>> deleteProvider(@PathVariable(value = "id") String id) {
        log.info("删除模型供应商: {}", (Object) id);
        this.modelProviderService.deleteProvider(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @RequirePermission(value = "model:update")
    @PostMapping(value = { "/{id}/test" })
    public ResponseEntity<ApiResponse<ConnectionTestResult>> testConnection(@PathVariable(value = "id") String id) {
        log.info("测试模型供应商连接: {}", (Object) id);
        ConnectionTestResult result = this.modelProviderService.testConnection(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @RequirePermission(value = "model:create")
    @PostMapping(value = { "/test-connection" })
    public ResponseEntity<ApiResponse<ConnectionTestResult>> testConnectionDirect(
            @Valid @RequestBody ConnectionTestRequest request) {
        log.info("直接测试连接: providerType={}", (Object) request.getProviderType());
        ConnectionTestResult result = this.modelProviderService.testConnectionDirect(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Generated
    public ModelProviderController(ModelProviderService modelProviderService) {
        this.modelProviderService = modelProviderService;
    }
}
