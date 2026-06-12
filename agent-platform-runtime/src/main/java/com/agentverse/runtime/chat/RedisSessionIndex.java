/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.chat;

import com.agentverse.runtime.chat.SessionIndex;
import com.agentverse.runtime.chat.SessionMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSessionIndex
implements SessionIndex {
    private static final Logger log = LoggerFactory.getLogger(RedisSessionIndex.class);
    private static final String KEY_PREFIX = "agentverse:session:idx:";
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public RedisSessionIndex(StringRedisTemplate redis) {
        this.redis = redis;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule((Module)new JavaTimeModule());
    }

    @Override
    public String create(String agentId, String userId) {
        String id = UUID.randomUUID().toString();
        SessionMeta meta = new SessionMeta(id, agentId, userId, Instant.now(), SessionMeta.SessionStatus.ACTIVE);
        this.save(meta);
        return id;
    }

    private void save(SessionMeta meta) {
        try {
            this.redis.opsForValue().set((Object)(KEY_PREFIX + meta.sessionId()), (Object)this.mapper.writeValueAsString((Object)meta));
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize SessionMeta", e);
        }
    }

    @Override
    public Optional<SessionMeta> get(String sessionId) {
        String json = (String)this.redis.opsForValue().get((Object)(KEY_PREFIX + sessionId));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of((SessionMeta)this.mapper.readValue(json, SessionMeta.class));
        }
        catch (JsonProcessingException e) {
            log.error("Failed to deserialize SessionMeta for sessionId={}", (Object)sessionId, (Object)e);
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(String sessionId) {
        return Boolean.TRUE.equals(this.redis.hasKey((Object)(KEY_PREFIX + sessionId)));
    }

    @Override
    public List<SessionMeta> listByAgent(String agentId, String userId) {
        Set keys = this.redis.keys((Object)"agentverse:session:idx:*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        ArrayList<SessionMeta> result = new ArrayList<SessionMeta>();
        for (String k : keys) {
            String id = k.substring(KEY_PREFIX.length());
            SessionMeta m = this.get(id).orElse(null);
            if (m == null || !m.agentId().equals(agentId) || userId != null && !m.userId().equals(userId)) continue;
            result.add(m);
        }
        return result;
    }

    @Override
    public void updateStatus(String sessionId, SessionMeta.SessionStatus status) {
        this.get(sessionId).ifPresent(m -> this.save(new SessionMeta(m.sessionId(), m.agentId(), m.userId(), m.createdAt(), status)));
    }

    @Override
    public void delete(String sessionId) {
        this.redis.delete((Object)(KEY_PREFIX + sessionId));
    }
}

