/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.config;

import com.agentverse.runtime.security.AccessLogFilter;
import com.agentverse.runtime.security.JwtAuthFilter;
import com.agentverse.runtime.security.RbacPermissionFilter;
import com.agentverse.runtime.service.TokenBlacklistService;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(TokenBlacklistService tokenBlacklistService) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter((Filter)new JwtAuthFilter(tokenBlacklistService));
        registration.addUrlPatterns(new String[]{"/*"});
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RbacPermissionFilter> rbacPermissionFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter((Filter)new RbacPermissionFilter());
        registration.addUrlPatterns(new String[]{"/v1/admin/*"});
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AccessLogFilter> accessLogFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter((Filter)new AccessLogFilter());
        registration.addUrlPatterns(new String[]{"/*"});
        registration.setOrder(3);
        return registration;
    }
}

