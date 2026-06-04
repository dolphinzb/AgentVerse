package com.agentverse.runtime.config;

import com.agentverse.runtime.security.AccessLogFilter;
import com.agentverse.runtime.security.JwtAuthFilter;
import com.agentverse.runtime.security.RbacPermissionFilter;
import com.agentverse.runtime.service.TokenBlacklistService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置
 * 只通过 FilterRegistrationBean 显式注册，避免 @Component 导致的重复注册
 */
@Configuration
public class FilterConfig {

    /**
     * 注册 JWT 认证过滤器（最高优先级，order=1）
     */
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(
            TokenBlacklistService tokenBlacklistService) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthFilter(tokenBlacklistService));
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 注册 RBAC 权限过滤器（order=2，在 JWT 认证之后）
     */
    @Bean
    public FilterRegistrationBean<RbacPermissionFilter> rbacPermissionFilterRegistration() {
        FilterRegistrationBean<RbacPermissionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RbacPermissionFilter());
        registration.addUrlPatterns("/v1/admin/*");
        registration.setOrder(2);
        return registration;
    }

    /**
     * 注册访问日志过滤器（order=3）
     */
    @Bean
    public FilterRegistrationBean<AccessLogFilter> accessLogFilterRegistration() {
        FilterRegistrationBean<AccessLogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AccessLogFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(3);
        return registration;
    }
}