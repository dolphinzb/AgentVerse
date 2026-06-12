/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.model.dto.ModelStatResponse;
import com.agentverse.runtime.model.service.ChatUsageService;
import com.agentverse.runtime.security.RequirePermission;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/model-stats"})
public class ModelStatsController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ModelStatsController.class);
    private final ChatUsageService chatUsageService;

    @RequirePermission(value="model:read")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ModelStatResponse>>> getModelStats() {
        log.info("\u83b7\u53d6\u6a21\u578b\u7528\u91cf\u7edf\u8ba1");
        Long userId = UserContext.isAdmin() ? null : UserContext.getUserId();
        List<ModelStatResponse> stats = this.chatUsageService.getStats(userId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Generated
    public ModelStatsController(ChatUsageService chatUsageService) {
        this.chatUsageService = chatUsageService;
    }
}

