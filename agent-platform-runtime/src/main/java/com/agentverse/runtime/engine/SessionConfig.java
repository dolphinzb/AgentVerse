/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import io.agentscope.core.session.Session;
import io.agentscope.core.session.redis.RedisSession;
import io.lettuce.core.RedisClient;
import java.time.Duration;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name={"agent.session.backend"}, havingValue="redis")
public class SessionConfig
implements DisposableBean {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SessionConfig.class);
    @Value(value="${agent.session.redis.key-prefix:agentverse:session:}")
    private String keyPrefix;
    @Value(value="${agent.session.redis.command-timeout-ms:2000}")
    private long commandTimeoutMs;
    private RedisClient redisClient;

    @Bean
    public Session agentScopeSession(@Value(value="${spring.data.redis.host:localhost}") String host, @Value(value="${spring.data.redis.port:6379}") int port, @Value(value="${spring.data.redis.password:}") String password) {
        String auth = password == null || password.isEmpty() ? "" : password + "@";
        String uri = String.format("redis://%s%s:%d/0", auth, host, port);
        log.info("Building Redis-backed AgentScope Session: uri=redis://{}@{}:{}/0 (keyPrefix={}, commandTimeout={}ms)", new Object[]{auth.isEmpty() ? "<no-auth>" : "***", host, port, this.keyPrefix, this.commandTimeoutMs});
        this.redisClient = RedisClient.create((String)uri);
        this.redisClient.setDefaultTimeout(Duration.ofMillis(this.commandTimeoutMs));
        return RedisSession.builder().lettuceClient(this.redisClient).keyPrefix(this.keyPrefix).build();
    }

    public void destroy() {
        if (this.redisClient != null) {
            try {
                this.redisClient.shutdown();
                log.info("Redis client shut down");
            }
            catch (Exception e) {
                log.warn("Failed to shutdown Redis client cleanly: {}", (Object)e.getMessage());
            }
        }
    }
}

