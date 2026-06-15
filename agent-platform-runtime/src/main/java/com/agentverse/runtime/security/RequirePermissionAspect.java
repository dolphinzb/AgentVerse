/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.security;

import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.security.RequirePermission;
import com.agentverse.runtime.service.RolePermissionService;
import lombok.Generated;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequirePermissionAspect {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(RequirePermissionAspect.class);
    private final RolePermissionService rolePermissionService;

    @Before(value="@annotation(requirePermission)")
    public void checkPermission(RequirePermission requirePermission) {
        String permCode = requirePermission.value();
        String roleCode = UserContext.getRoleCode();
        if (roleCode == null) {
            log.warn("Permission denied: no user context");
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        if (!this.rolePermissionService.hasPermission(roleCode, permCode)) {
            log.warn("Permission denied: role={}, required={}", (Object)roleCode, (Object)permCode);
            throw new BizException(ErrorCode.PERMISSION_DENIED);
        }
    }

    @Generated
    public RequirePermissionAspect(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }
}

