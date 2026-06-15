/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.runtime.model.dto.ModelAddRequest;
import com.agentverse.runtime.model.dto.ModelConfigResponse;
import com.agentverse.runtime.model.service.ModelManagementService;
import com.agentverse.runtime.security.RequirePermission;
import jakarta.validation.Valid;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/models"})
public class ModelManagementController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelManagementController.class);
    private final ModelManagementService modelManagementService;

    @RequirePermission(value="model:create")
    @PostMapping(value={"/add"})
    public ResponseEntity<ApiResponse<ModelConfigResponse>> addModel(@Valid @RequestBody ModelAddRequest request) {
        log.info("\u6dfb\u52a0\u6a21\u578b: providerType={}, modelName={}", (Object)request.getProviderType(), (Object)request.getModelName());
        ModelConfigResponse response = this.modelManagementService.addModel(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Generated
    public ModelManagementController(ModelManagementService modelManagementService) {
        this.modelManagementService = modelManagementService;
    }
}

