package com.agentverse.runtime.security;

import com.agentverse.common.security.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * RBAC 权限过滤器：WebFilter 层统一鉴权入口
 * 对 /v1/admin/** 路径进行粗粒度角色检查，仅 admin 可访问
 */
@Slf4j
public class RbacPermissionFilter extends OncePerRequestFilter {

    private static final String ADMIN_PATH_PREFIX = "/v1/admin/";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 使用 servletPath 而非 requestURI，避免 context-path 干扰
        String path = request.getServletPath();

        // 仅拦截 /v1/admin/** 路径
        if (path.startsWith(ADMIN_PATH_PREFIX)) {
            String roleCode = UserContext.getRoleCode();

            if (roleCode == null || !"admin".equals(roleCode)) {
                log.warn("RBAC denied: non-admin user accessing admin path: {}", path);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"Access denied: admin role required\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}