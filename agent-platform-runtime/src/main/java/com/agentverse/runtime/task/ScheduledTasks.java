package com.agentverse.runtime.task;

import com.agentverse.common.entity.AuditLog;
import com.agentverse.runtime.mapper.AuditLogMapper;
import com.agentverse.runtime.service.TokenBlacklistService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务。
 * <p>清理过期的 token 黑名单与 90 天前的审计日志。
 */
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final TokenBlacklistService tokenBlacklistService;
    private final AuditLogMapper auditLogMapper;

    /** 每天 02:00 清理过期 token 黑名单。 */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredTokenBlacklist() {
        log.info("Scheduled task: cleaning expired token blacklist");
        tokenBlacklistService.cleanExpired();
    }

    /** 每天 03:00 清理 90 天前的审计日志。 */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredAuditLogs() {
        log.info("Scheduled task: cleaning expired audit logs");
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90L);
        LambdaQueryWrapper<AuditLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(AuditLog::getCreatedTime, ninetyDaysAgo);
        long deleted = auditLogMapper.delete(queryWrapper);
        if (deleted > 0L) {
            log.info("Cleaned {} expired audit log entries", deleted);
        }
    }
}
