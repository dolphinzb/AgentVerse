/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.config;

import com.agentverse.common.security.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);
    @Value(value="${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
        JwtUtils.init(this.jwtSecret);
        log.info("JWT secret initialized from configuration");
    }
}

