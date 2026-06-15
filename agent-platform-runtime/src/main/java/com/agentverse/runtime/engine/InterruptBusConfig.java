package com.agentverse.runtime.engine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * InterruptBus 装配配置。
 *
 * <p>Bean 装配策略：
 * <ul>
 *   <li>{@link LocalInterruptBus} 始终作为 @Bean 暴露，供 RedisInterruptBus 装饰使用，也是默认实现</li>
 *   <li>{@link InterruptBus} (接口) Bean 在以下两种情况提供：
 *     <ul>
 *       <li>agent.interrupt.backend=redis 且存在 StringRedisTemplate → RedisInterruptBus（跨实例）</li>
 *       <li>否则 → LocalInterruptBus（单实例）</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Configuration
public class InterruptBusConfig {

    @Bean
    public LocalInterruptBus localInterruptBus() {
        return new LocalInterruptBus();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "agent.interrupt.backend", havingValue = "redis")
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean(name = "redisInterruptBus")
    public InterruptBus redisInterruptBus(
            LocalInterruptBus localInterruptBus,
            StringRedisTemplate redisTemplate,
            @Value("${agent.interrupt.redis.channel-prefix:agentverse:interrupt:}") String channelPrefix) {
        return new RedisInterruptBus(localInterruptBus, redisTemplate, channelPrefix);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "redisInterruptBus")
    public InterruptBus defaultInterruptBus(LocalInterruptBus localInterruptBus) {
        return localInterruptBus;
    }
}