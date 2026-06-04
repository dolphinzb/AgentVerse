package com.agentverse.runtime.security;

import com.agentverse.common.entity.AuditLog;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.mapper.AuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审计日志 AOP 切面：拦截所有写操作并记录
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogMapper auditLogMapper;
    private final HttpServletRequest request;

    /**
     * 拦截所有 @PostMapping、@PutMapping、@DeleteMapping 方法
     */
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object auditLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String action = extractAction(joinPoint);
        Object result = joinPoint.proceed();

        try {
            Long userId = UserContext.getUserId();
            String username = UserContext.getUsername();
            String target = extractTarget(joinPoint);
            String detail = extractDetail(joinPoint);
            String ip = getClientIp();

            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setUsername(username);
            auditLog.setAction(action);
            auditLog.setTarget(target);
            auditLog.setDetail(detail);
            auditLog.setIp(ip);
            auditLog.setCreatedTime(LocalDateTime.now());

            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.warn("Failed to save audit log: {}", e.getMessage());
        }

        return result;
    }

    private String extractAction(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        if (className.toLowerCase().contains("agent")) {
            if (methodName.startsWith("create")) return "agent:create";
            if (methodName.startsWith("update")) return "agent:update";
            if (methodName.startsWith("delete")) return "agent:delete";
            if (methodName.contains("Publish")) return "agent:publish";
            if (methodName.contains("Rollback")) return "agent:rollback";
            return "agent:unknown";
        }
        if (className.toLowerCase().contains("chat")) {
            if (methodName.startsWith("create")) return "chat:create";
            if (methodName.startsWith("delete")) return "chat:delete";
            return "chat:unknown";
        }
        if (className.toLowerCase().contains("auth")) {
            if (methodName.contains("register")) return "auth:register";
            if (methodName.contains("logout")) return "auth:logout";
            return "auth:unknown";
        }

        return methodName;
    }

    private String extractTarget(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof String str && (str.length() < 100)) {
                return str;
            }
        }
        return null;
    }

    private String extractDetail(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null && !(arg instanceof String)) {
                try {
                    return arg.toString();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return null;
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}