package com.agentverse.runtime.chat;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Redis 实现的会话元数据索引。
 * <p>
 * 仅保存 SessionMeta（不含消息内容），消息内容由 MessageProjector 负责。
 * <p>
 * Key 形式：{@code agentverse:session:idx:<sessionId>}
 */
@Component
public class RedisSessionIndex implements SessionIndex {

    private static final Logger log = LoggerFactory.getLogger(RedisSessionIndex.class);
    private static final String KEY_PREFIX = "agentverse:session:idx:";

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public RedisSessionIndex(StringRedisTemplate redis) {
        this.redis = redis;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public String create(String agentId, String userId) {
        String id = UUID.randomUUID().toString();
        SessionMeta meta = new SessionMeta(id, agentId, userId,
                Instant.now(), SessionMeta.SessionStatus.ACTIVE);
        save(meta);
        return id;
    }

    private void save(SessionMeta meta) {
        try {
            redis.opsForValue().set(KEY_PREFIX + meta.sessionId(),
                    mapper.writeValueAsString(meta));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize SessionMeta", e);
        }
    }

    @Override
    public Optional<SessionMeta> get(String sessionId) {
        String json = redis.opsForValue().get(KEY_PREFIX + sessionId);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.readValue(json, SessionMeta.class));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize SessionMeta for sessionId={}", sessionId, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(String sessionId) {
        return Boolean.TRUE.equals(redis.hasKey(KEY_PREFIX + sessionId));
    }

    @Override
    public List<SessionMeta> listByAgent(String agentId, String userId) {
        Set<String> keys = redis.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        List<SessionMeta> result = new ArrayList<>();
        for (String k : keys) {
            String id = k.substring(KEY_PREFIX.length());
            SessionMeta m = get(id).orElse(null);
            if (m == null)
                continue;
            if (!m.agentId().equals(agentId))
                continue;
            if (userId != null && !m.userId().equals(userId))
                continue;
            result.add(m);
        }
        return result;
    }

    @Override
    public void updateStatus(String sessionId, SessionMeta.SessionStatus status) {
        get(sessionId)
                .ifPresent(m -> save(new SessionMeta(m.sessionId(), m.agentId(), m.userId(), m.createdAt(), status)));
    }

    @Override
    public void delete(String sessionId) {
        redis.delete(KEY_PREFIX + sessionId);
    }
}
