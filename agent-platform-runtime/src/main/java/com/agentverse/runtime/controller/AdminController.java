/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.dto.PageResult;
import com.agentverse.common.entity.AuditLog;
import com.agentverse.runtime.mapper.AuditLogMapper;
import com.agentverse.runtime.security.RequirePermission;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.LocalDateTime;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/admin"})
public class AdminController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AuditLogMapper auditLogMapper;

    @RequirePermission(value="admin:audit")
    @GetMapping(value={"/audit-logs"})
    public ResponseEntity<ApiResponse<PageResult<AuditLog>>> listAuditLogs(@RequestParam(defaultValue="1") Integer page, @RequestParam(defaultValue="20") Integer pageSize, @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime, @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime, @RequestParam(required=false) String action, @RequestParam(required=false) Long userId) {
        log.info("Query audit logs: page={}, pageSize={}, action={}, userId={}", new Object[]{page, pageSize, action, userId});
        Page pageParam = new Page((long)page.intValue(), (long)pageSize.intValue());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        if (startTime != null) {
            queryWrapper.ge(AuditLog::getCreatedTime, (Object)startTime);
        }
        if (endTime != null) {
            queryWrapper.le(AuditLog::getCreatedTime, (Object)endTime);
        }
        if (StringUtils.hasText((String)action)) {
            queryWrapper.eq(AuditLog::getAction, (Object)action);
        }
        if (userId != null) {
            queryWrapper.eq(AuditLog::getUserId, (Object)userId);
        }
        queryWrapper.orderByDesc(AuditLog::getCreatedTime);
        Page result = (Page)this.auditLogMapper.selectPage((IPage)pageParam, (Wrapper)queryWrapper);
        PageResult pageResult = new PageResult(result.getRecords(), result.getTotal(), (long)page, (long)pageSize);
        return ResponseEntity.ok(ApiResponse.success(pageResult));
    }

    @Generated
    public AdminController(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }
}

