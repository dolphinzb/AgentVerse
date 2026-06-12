package com.agentverse.runtime.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 会话存储
 */
@Slf4j
@Component
@ConditionalOnBean(RedisTemplate.class)
public class RedisSessionStore implements SessionStore {

    private static final String KEY_PREFIX = "agentverse:session:";
    private static final String MESSAGES_SUFFIX = ":messages";
    private static final String META_SUFFIX = ":meta";
    private static final long DEFAULT_TTL_HOURS = 24;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisSessionStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String createSession(String agentId) {
        String sessionId = UUID.randomUUID().toString();
        Map<String, String> meta = new HashMap<>();
        meta.put("agentId", agentId);
        meta.put("createdAt", LocalDateTime.now().toString());

        redisTemplate.opsForHash().putAll(KEY_PREFIX + sessionId + META_SUFFIX, meta);
        redisTemplate.expire(KEY_PREFIX + sessionId + META_SUFFIX, DEFAULT_TTL_HOURS, TimeUnit.HOURS);

        log.debug("Created Redis session: {}", sessionId);
        return sessionId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> getSession(String sessionId) {
        Object raw = redisTemplate.opsForValue().get(KEY_PREFIX + sessionId + MESSAGES_SUFFIX);
        if (raw == null) return null;
        try {
            return objectMapper.convertValue(raw, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, Message.class));
        } catch (Exception e) {
            log.error("Failed to deserialize messages for session: {}", sessionId, e);
            return null;
        }
    }

    @Override
    public void addMessage(String sessionId, Message message) {
        List<Message> messages = getSession(sessionId);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        redisTemplate.opsForValue().set(KEY_PREFIX + sessionId + MESSAGES_SUFFIX, messages, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void deleteSession(String sessionId) {
        redisTemplate.delete(KEY_PREFIX + sessionId + MESSAGES_SUFFIX);
        redisTemplate.delete(KEY_PREFIX + sessionId + META_SUFFIX);
    }

    @Override
    public boolean exists(String sessionId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + sessionId + META_SUFFIX));
    }

    @Override
    public String getAgentId(String sessionId) {
        Object val = redisTemplate.opsForHash().get(KEY_PREFIX + sessionId + META_SUFFIX, "agentId");
        return val != null ? val.toString() : null;
    }

    @Override
    public LocalDateTime getCreatedAt(String sessionId) {
        Object val = redisTemplate.opsForHash().get(KEY_PREFIX + sessionId + META_SUFFIX, "createdAt");
        return val != null ? LocalDateTime.parse(val.toString()) : null;
    }

    @Override
    public Set<String> getAllSessionIds() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*" + META_SUFFIX);
        if (keys == null) return Collections.emptySet();
        return keys.stream()
                .map(k -> k.substring(KEY_PREFIX.length(), k.indexOf(META_SUFFIX)))
                .collect(Collectors.toSet());
    }

    @Override
    public List<SessionMetadata> listSessions() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*" + META_SUFFIX);
        if (keys == null) return Collections.emptyList();
        return keys.stream()
                .map(k -> {
                    String sessionId = k.substring(KEY_PREFIX.length(), k.indexOf(META_SUFFIX));
                    String agentId = getAgentId(sessionId);
                    LocalDateTime createdAt = getCreatedAt(sessionId);
                    return new SessionMetadata(sessionId, agentId, createdAt);
                })
                .filter(m -> m.getAgentId() != null)
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }
}
