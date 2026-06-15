/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.runtime.model.dto.ModelConfigCreateRequest;
import com.agentverse.runtime.model.dto.ModelConfigResponse;
import com.agentverse.runtime.model.dto.ModelConfigUpdateRequest;
import com.agentverse.runtime.model.service.ModelConfigService;
import com.agentverse.runtime.security.RequirePermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
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
@RequestMapping(value={"/v1/model-configs"})
public class ModelConfigController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelConfigController.class);
    private final ModelConfigService modelConfigService;

    @RequirePermission(value="model:read")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ModelConfigResponse>>> listModelConfigs(@RequestParam(name="page", defaultValue="1") Integer page, @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, @RequestParam(name="providerId", required=false) String providerId) {
        log.info("\u67e5\u8be2\u6a21\u578b\u914d\u7f6e\u5217\u8868: page={}, pageSize={}, providerId={}", new Object[]{page, pageSize, providerId});
        Page<ModelConfigResponse> result = this.modelConfigService.listModelConfigs(page, pageSize, providerId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @RequirePermission(value="model:read")
    @GetMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<ModelConfigResponse>> getModelConfigById(@PathVariable(value="id") String id) {
        log.info("\u67e5\u8be2\u6a21\u578b\u914d\u7f6e: {}", (Object)id);
        ModelConfigResponse config = this.modelConfigService.getModelConfigById(id);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @RequirePermission(value="model:create")
    @PostMapping
    public ResponseEntity<ApiResponse<ModelConfigResponse>> createModelConfig(@Valid @RequestBody ModelConfigCreateRequest request) {
        log.info("\u521b\u5efa\u6a21\u578b\u914d\u7f6e: providerId={}, modelName={}", (Object)request.getProviderId(), (Object)request.getModelName());
        ModelConfigResponse config = this.modelConfigService.createModelConfig(request);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @RequirePermission(value="model:update")
    @PutMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<ModelConfigResponse>> updateModelConfig(@PathVariable(value="id") String id, @Valid @RequestBody ModelConfigUpdateRequest request) {
        log.info("\u66f4\u65b0\u6a21\u578b\u914d\u7f6e: {}", (Object)id);
        ModelConfigResponse config = this.modelConfigService.updateModelConfig(id, request);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @RequirePermission(value="model:delete")
    @DeleteMapping(value={"/{id}"})
    public ResponseEntity<ApiResponse<Void>> deleteModelConfig(@PathVariable(value="id") String id) {
        log.info("\u5220\u9664\u6a21\u578b\u914d\u7f6e: {}", (Object)id);
        this.modelConfigService.deleteModelConfig(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @RequirePermission(value="model:read")
    @GetMapping(value={"/default"})
    public ResponseEntity<ApiResponse<ModelConfigResponse>> getDefaultModel() {
        log.info("\u83b7\u53d6\u5f53\u524d\u7528\u6237\u7684\u9ed8\u8ba4\u6a21\u578b\u914d\u7f6e");
        ModelConfigResponse config = this.modelConfigService.getDefaultModel();
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @Generated
    public ModelConfigController(ModelConfigService modelConfigService) {
        this.modelConfigService = modelConfigService;
    }
}

