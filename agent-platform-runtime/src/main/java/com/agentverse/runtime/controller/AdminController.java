package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.dto.PageResult;
import com.agentverse.common.entity.AuditLog;
import com.agentverse.runtime.mapper.AuditLogMapper;
import com.agentverse.runtime.security.RequirePermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理后台控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuditLogMapper auditLogMapper;

    /**
     * 查询审计日志（分页 + 筛选）
     */
    @RequirePermission("admin:audit")
    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<PageResult<AuditLog>>> listAuditLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long userId) {

        log.info("Query audit logs: page={}, pageSize={}, action={}, userId={}", page, pageSize, action, userId);

        Page<AuditLog> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<AuditLog> queryWrapper = new LambdaQueryWrapper<>();

        if (startTime != null) {
            queryWrapper.ge(AuditLog::getCreatedTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(AuditLog::getCreatedTime, endTime);
        }
        if (StringUtils.hasText(action)) {
            queryWrapper.eq(AuditLog::getAction, action);
        }
        if (userId != null) {
            queryWrapper.eq(AuditLog::getUserId, userId);
        }

        queryWrapper.orderByDesc(AuditLog::getCreatedTime);

        Page<AuditLog> result = auditLogMapper.selectPage(pageParam, queryWrapper);

        PageResult<AuditLog> pageResult = new PageResult<>(
                result.getRecords(), result.getTotal(), (long) page, (long) pageSize);
        return ResponseEntity.ok(ApiResponse.success(pageResult));
    }
}