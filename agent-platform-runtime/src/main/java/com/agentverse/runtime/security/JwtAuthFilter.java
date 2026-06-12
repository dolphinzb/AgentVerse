/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.security;

import com.agentverse.common.security.JwtUtils;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.service.TokenBlacklistService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthFilter
extends OncePerRequestFilter {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final TokenBlacklistService tokenBlacklistService;
    private static final String[] PUBLIC_PATHS = new String[]{"/v1/auth/login", "/v1/auth/register"};

    public JwtAuthFilter(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if (this.isPublicPath(path)) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        String token = this.extractToken(request);
        if (token == null) {
            this.sendUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }
        if (!JwtUtils.validateToken(token)) {
            this.sendUnauthorized(response, "Token is invalid or expired");
            return;
        }
        if (this.tokenBlacklistService.isBlacklisted(token)) {
            this.sendUnauthorized(response, "Token has been revoked");
            return;
        }
        try {
            Long userId = JwtUtils.getUserId(token);
            String username = JwtUtils.getUsername(token);
            String roleCode = JwtUtils.getRoleCode(token);
            UserContext.set(new UserContext.UserInfo(userId, username, roleCode));
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            UserContext.clear();
        }
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (!path.equals(publicPath)) continue;
            return true;
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText((String)bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}

