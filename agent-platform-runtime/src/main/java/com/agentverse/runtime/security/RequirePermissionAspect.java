package com.agentverse.runtime.security;

import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 权限校验 AOP 切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequirePermissionAspect {

    private final RolePermissionService rolePermissionService;

    @Before("@annotation(requirePermission)")
    public void checkPermission(RequirePermission requirePermission) {
        String permCode = requirePermission.value();
        String roleCode = UserContext.getRoleCode();

        if (roleCode == null) {
            log.warn("Permission denied: no user context");
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        if (!rolePermissionService.hasPermission(roleCode, permCode)) {
            log.warn("Permission denied: role={}, required={}", roleCode, permCode);
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }
}