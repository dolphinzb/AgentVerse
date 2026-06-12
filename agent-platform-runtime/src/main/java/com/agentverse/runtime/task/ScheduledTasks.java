/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.task;

import com.agentverse.common.entity.AuditLog;
import com.agentverse.runtime.mapper.AuditLogMapper;
import com.agentverse.runtime.service.TokenBlacklistService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private final TokenBlacklistService tokenBlacklistService;
    private final AuditLogMapper auditLogMapper;

    @Scheduled(cron="0 0 2 * * ?")
    public void cleanExpiredTokenBlacklist() {
        log.info("Scheduled task: cleaning expired token blacklist");
        this.tokenBlacklistService.cleanExpired();
    }

    @Scheduled(cron="0 0 3 * * ?")
    public void cleanExpiredAuditLogs() {
        log.info("Scheduled task: cleaning expired audit logs");
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90L);
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        queryWrapper.lt(AuditLog::getCreatedTime, (Object)ninetyDaysAgo);
        long deleted = this.auditLogMapper.delete((Wrapper)queryWrapper);
        if (deleted > 0L) {
            log.info("Cleaned {} expired audit log entries", (Object)deleted);
        }
    }

    @Generated
    public ScheduledTasks(TokenBlacklistService tokenBlacklistService, AuditLogMapper auditLogMapper) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.auditLogMapper = auditLogMapper;
    }
}

