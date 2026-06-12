/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.controller;

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
@RequestMapping(value={"/v1/model-providers"})
public class ModelProviderController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelProviderController.class);
    private final ModelProviderService modelProviderService;

    @RequirePermission(value="model:read")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProviderResponse>>> listProviders(@RequestParam(name="page", defaultValue="1") Integer page, @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, @RequestParam(name="status", required=false) String status) {
        log.info("\u67e5\u8be2\u6a21\u578b\u4f9b\u5e94\u5546\u5217\u8868: page={}, pageSize={}, status={}", new Object[]{page, pageSize, status});
        Page<ProviderResponse> result = this.modelProviderService.listProviders(page, pageSize, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @RequirePermission(value="model:read")
    @GetMapping(value={"/types"})
    public ResponseEntity<ApiResponse<List<ProviderTypeResponse>>> getTypes() {
        log.info("\u83b7\u53d6\u6240\u6709\u4f9b\u5e94\u5546\u7c7b\u578b");
        List<ProviderTypeResponse> types = this.modelProviderService.getTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @RequirePermission(value="model:read")
    @GetMapping(value={"/presets"})
    public ResponseEntity<ApiResponse<List<ProviderPresetResponse>>> getPresets() {
        log.info("\u83b7\u53d6\u9884\u8bbe\u4f9b\u5e94\u5546\u5217\u8868");
        List<ProviderPresetResponse> presets = this.modelProviderService.getPresets();
        return ResponseEntity.ok(ApiResponse.success(presets));
    }

    @RequirePermission(value="model:read")
    @GetMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<ProviderResponse>> getProviderById(@PathVariable(value="id") String id) {
        log.info("\u67e5\u8be2\u6a21\u578b\u4f9b\u5e94\u5546: {}", (Object)id);
        ProviderResponse provider = this.modelProviderService.getProviderById(id);
        return ResponseEntity.ok(ApiResponse.success(provider));
    }

    @RequirePermission(value="model:create")
    @PostMapping
    public ResponseEntity<ApiResponse<ProviderResponse>> createProvider(@Valid @RequestBody ProviderCreateRequest request) {
        log.info("\u521b\u5efa\u6a21\u578b\u4f9b\u5e94\u5546: {}", (Object)request.getName());
        ProviderResponse provider = this.modelProviderService.createProvider(request);
        return ResponseEntity.ok(ApiResponse.success(provider));
    }

    @RequirePermission(value="model:update")
    @PutMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<ProviderResponse>> updateProvider(@PathVariable(value="id") String id, @Valid @RequestBody ProviderUpdateRequest request) {
        log.info("\u66f4\u65b0\u6a21\u578b\u4f9b\u5e94\u5546: {}", (Object)id);
        ProviderResponse provider = this.modelProviderService.updateProvider(id, request);
        return ResponseEntity.ok(ApiResponse.success(provider));
    }

    @RequirePermission(value="model:delete")
    @DeleteMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<Void>> deleteProvider(@PathVariable(value="id") String id) {
        log.info("\u5220\u9664\u6a21\u578b\u4f9b\u5e94\u5546: {}", (Object)id);
        this.modelProviderService.deleteProvider(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @RequirePermission(value="model:update")
    @PostMapping(value={"/{id}/test"})
    public ResponseEntity<ApiResponse<ConnectionTestResult>> testConnection(@PathVariable(value="id") String id) {
        log.info("\u6d4b\u8bd5\u6a21\u578b\u4f9b\u5e94\u5546\u8fde\u63a5: {}", (Object)id);
        ConnectionTestResult result = this.modelProviderService.testConnection(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @RequirePermission(value="model:create")
    @PostMapping(value={"/test-connection"})
    public ResponseEntity<ApiResponse<ConnectionTestResult>> testConnectionDirect(@Valid @RequestBody ConnectionTestRequest request) {
        log.info("\u76f4\u63a5\u6d4b\u8bd5\u8fde\u63a5: providerType={}", (Object)request.getProviderType());
        ConnectionTestResult result = this.modelProviderService.testConnectionDirect(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Generated
    public ModelProviderController(ModelProviderService modelProviderService) {
        this.modelProviderService = modelProviderService;
    }
}

