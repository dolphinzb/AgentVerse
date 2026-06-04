package com.agentverse.runtime.security;

import com.agentverse.common.security.JwtUtils;
import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器：拦截请求、解析 Token、校验黑名单、注入 UserContext
 */
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthFilter(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    private static final String[] PUBLIC_PATHS = {
            "/v1/auth/login",
            "/v1/auth/register"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 公开路径放行
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取 Token
        String token = extractToken(request);
        if (token == null) {
            sendUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }

        // 校验 Token 有效性
        if (!JwtUtils.validateToken(token)) {
            sendUnauthorized(response, "Token is invalid or expired");
            return;
        }

        // 校验 Token 是否在黑名单中
        if (tokenBlacklistService.isBlacklisted(token)) {
            sendUnauthorized(response, "Token has been revoked");
            return;
        }

        // 解析 Token 并注入 UserContext
        try {
            Long userId = JwtUtils.getUserId(token);
            String username = JwtUtils.getUsername(token);
            String roleCode = JwtUtils.getRoleCode(token);

            UserContext.set(new UserContext.UserInfo(userId, username, roleCode));
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath)) {
                return true;
            }
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}