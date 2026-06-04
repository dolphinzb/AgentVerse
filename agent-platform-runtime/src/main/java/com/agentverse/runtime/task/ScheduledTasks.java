package com.agentverse.runtime.task;

import com.agentverse.runtime.mapper.AuditLogMapper;
import com.agentverse.runtime.service.TokenBlacklistService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务：清理过期数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final TokenBlacklistService tokenBlacklistService;
    private final AuditLogMapper auditLogMapper;

    /**
     * 每天凌晨 2 点清理过期 Token 黑名单记录
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredTokenBlacklist() {
        log.info("Scheduled task: cleaning expired token blacklist");
        tokenBlacklistService.cleanExpired();
    }

    /**
     * 每天凌晨 3 点清理 90 天前的审计日志
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredAuditLogs() {
        log.info("Scheduled task: cleaning expired audit logs");
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        LambdaQueryWrapper<com.agentverse.common.entity.AuditLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(com.agentverse.common.entity.AuditLog::getCreatedTime, ninetyDaysAgo);
        long deleted = auditLogMapper.delete(queryWrapper);
        if (deleted > 0) {
            log.info("Cleaned {} expired audit log entries", deleted);
        }
    }
}