/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.security;

import com.agentverse.common.security.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class RbacPermissionFilter
extends OncePerRequestFilter {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(RbacPermissionFilter.class);
    private static final String ADMIN_PATH_PREFIX = "/v1/admin/";

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String roleCode;
        String path = request.getServletPath();
        if (path.startsWith(ADMIN_PATH_PREFIX) && ((roleCode = UserContext.getRoleCode()) == null || !"admin".equals(roleCode))) {
            log.warn("RBAC denied: non-admin user accessing admin path: {}", (Object)path);
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"Access denied: admin role required\"}");
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

