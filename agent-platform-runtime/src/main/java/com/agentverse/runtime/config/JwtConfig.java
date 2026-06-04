package com.agentverse.runtime.config;

import com.agentverse.common.security.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置：从外部配置读取密钥并初始化 JwtUtils
 */
@Slf4j
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
        JwtUtils.init(jwtSecret);
        log.info("JWT secret initialized from configuration");
    }
}