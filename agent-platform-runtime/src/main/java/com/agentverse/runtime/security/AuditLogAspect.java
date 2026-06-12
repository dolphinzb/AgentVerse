/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.security;

import com.agentverse.common.entity.AuditLog;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.mapper.AuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.Generated;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLogAspect {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);
    private final AuditLogMapper auditLogMapper;
    private final HttpServletRequest request;

    @Around(value="@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object auditLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String action = this.extractAction(joinPoint);
        Object result = joinPoint.proceed();
        try {
            Long userId = UserContext.getUserId();
            String username = UserContext.getUsername();
            String target = this.extractTarget(joinPoint);
            String detail = this.extractDetail(joinPoint);
            String ip = this.getClientIp();
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setUsername(username);
            auditLog.setAction(action);
            auditLog.setTarget(target);
            auditLog.setDetail(detail);
            auditLog.setIp(ip);
            auditLog.setCreatedTime(LocalDateTime.now());
            this.auditLogMapper.insert(auditLog);
        }
        catch (Exception e) {
            log.warn("Failed to save audit log: {}", (Object)e.getMessage());
        }
        return result;
    }

    private String extractAction(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        if (className.toLowerCase().contains("agent")) {
            if (methodName.startsWith("create")) {
                return "agent:create";
            }
            if (methodName.startsWith("update")) {
                return "agent:update";
            }
            if (methodName.startsWith("delete")) {
                return "agent:delete";
            }
            if (methodName.contains("Publish")) {
                return "agent:publish";
            }
            if (methodName.contains("Rollback")) {
                return "agent:rollback";
            }
            return "agent:unknown";
        }
        if (className.toLowerCase().contains("chat")) {
            if (methodName.startsWith("create")) {
                return "chat:create";
            }
            if (methodName.startsWith("delete")) {
                return "chat:delete";
            }
            return "chat:unknown";
        }
        if (className.toLowerCase().contains("auth")) {
            if (methodName.contains("register")) {
                return "auth:register";
            }
            if (methodName.contains("logout")) {
                return "auth:logout";
            }
            return "auth:unknown";
        }
        return methodName;
    }

    private String extractTarget(ProceedingJoinPoint joinPoint) {
        Object[] args;
        for (Object arg : args = joinPoint.getArgs()) {
            String str;
            if (!(arg instanceof String) || (str = (String)arg).length() >= 100) continue;
            return str;
        }
        return null;
    }

    private String extractDetail(ProceedingJoinPoint joinPoint) {
        Object[] args;
        for (Object arg : args = joinPoint.getArgs()) {
            if (arg == null || arg instanceof String) continue;
            try {
                return arg.toString();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    private String getClientIp() {
        String ip = this.request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = this.request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = this.request.getRemoteAddr();
        }
        return ip;
    }

    @Generated
    public AuditLogAspect(AuditLogMapper auditLogMapper, HttpServletRequest request) {
        this.auditLogMapper = auditLogMapper;
        this.request = request;
    }
}

