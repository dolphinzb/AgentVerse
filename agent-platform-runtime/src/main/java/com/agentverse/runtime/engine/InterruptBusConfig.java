/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.runtime.engine.InterruptBus;
import com.agentverse.runtime.engine.LocalInterruptBus;
import com.agentverse.runtime.engine.RedisInterruptBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class InterruptBusConfig {
    @Bean
    public LocalInterruptBus localInterruptBus() {
        return new LocalInterruptBus();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name={"agent.interrupt.backend"}, havingValue="redis")
    @ConditionalOnMissingBean(name={"redisInterruptBus"})
    public InterruptBus redisInterruptBus(LocalInterruptBus localInterruptBus, StringRedisTemplate redisTemplate, @Value(value="${agent.interrupt.redis.channel-prefix:agentverse:interrupt:}") String channelPrefix) {
        return new RedisInterruptBus(localInterruptBus, redisTemplate, channelPrefix);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name={"redisInterruptBus"})
    public InterruptBus defaultInterruptBus(LocalInterruptBus localInterruptBus) {
        return localInterruptBus;
    }
}

