/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.runtime.engine.InterruptBus;
import com.agentverse.runtime.engine.LocalInterruptBus;
import io.agentscope.harness.agent.HarnessAgent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

public class RedisInterruptBus
implements InterruptBus {
    private static final Logger log = LoggerFactory.getLogger(RedisInterruptBus.class);
    public static final String DEFAULT_CHANNEL_PREFIX = "agentverse:interrupt:";
    private final LocalInterruptBus localDelegate;
    private final StringRedisTemplate redisTemplate;
    private final String channelPrefix;
    private RedisMessageListenerContainer container;

    public RedisInterruptBus(LocalInterruptBus localDelegate, StringRedisTemplate redisTemplate) {
        this(localDelegate, redisTemplate, DEFAULT_CHANNEL_PREFIX);
    }

    public RedisInterruptBus(LocalInterruptBus localDelegate, StringRedisTemplate redisTemplate, String channelPrefix) {
        this.localDelegate = localDelegate;
        this.redisTemplate = redisTemplate;
        this.channelPrefix = channelPrefix;
    }

    @PostConstruct
    void start() {
        String pattern = this.channelPrefix + "*";
        this.container = new RedisMessageListenerContainer();
        this.container.setConnectionFactory(this.redisTemplate.getConnectionFactory());
        this.container.addMessageListener((MessageListener)new InterruptMessageListener(), (Topic)new PatternTopic(pattern));
        this.container.start();
        log.info("RedisInterruptBus subscribed to pattern={}", (Object)pattern);
    }

    @PreDestroy
    void stop() {
        if (this.container != null) {
            try {
                this.container.stop();
                log.info("RedisInterruptBus listener container stopped");
            }
            catch (Exception e) {
                log.warn("Failed to stop RedisInterruptBus container: {}", (Object)e.getMessage());
            }
        }
    }

    @Override
    public void register(String sessionId, HarnessAgent agent) {
        this.localDelegate.register(sessionId, agent);
    }

    @Override
    public void unregister(String sessionId) {
        this.localDelegate.unregister(sessionId);
    }

    @Override
    public void publish(String sessionId) {
        if (sessionId == null) {
            return;
        }
        this.localDelegate.publish(sessionId);
        try {
            String channel = this.channelPrefix + sessionId;
            this.redisTemplate.convertAndSend(channel, (Object)"1");
            log.debug("RedisInterruptBus.publish: channel={}", (Object)channel);
        }
        catch (Exception e) {
            log.warn("RedisInterruptBus.publish: redis broadcast failed for session={}: {}", (Object)sessionId, (Object)e.getMessage());
        }
    }

    @Override
    public boolean containsLocal(String sessionId) {
        return this.localDelegate.containsLocal(sessionId);
    }

    @Override
    public int localSize() {
        return this.localDelegate.localSize();
    }

    private final class InterruptMessageListener
    implements MessageListener {
        private InterruptMessageListener() {
        }

        public void onMessage(Message message, byte[] pattern) {
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
            if (channel == null || !channel.startsWith(RedisInterruptBus.this.channelPrefix)) {
                return;
            }
            String sessionId = channel.substring(RedisInterruptBus.this.channelPrefix.length());
            if (sessionId.isEmpty()) {
                return;
            }
            log.debug("RedisInterruptBus received interrupt for session={}", (Object)sessionId);
            RedisInterruptBus.this.localDelegate.publish(sessionId);
        }
    }
}

