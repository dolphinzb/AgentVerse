/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.chat;

import com.agentverse.runtime.chat.SessionIndex;
import com.agentverse.runtime.chat.SessionMeta;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class InMemorySessionIndex
implements SessionIndex {
    private final Map<String, SessionMeta> store = new ConcurrentHashMap<String, SessionMeta>();

    @Override
    public String create(String agentId, String userId) {
        String id = UUID.randomUUID().toString();
        SessionMeta meta = new SessionMeta(id, agentId, userId, Instant.now(), SessionMeta.SessionStatus.ACTIVE);
        this.store.put(id, meta);
        return id;
    }

    @Override
    public Optional<SessionMeta> get(String sessionId) {
        return Optional.ofNullable(this.store.get(sessionId));
    }

    @Override
    public boolean exists(String sessionId) {
        return this.store.containsKey(sessionId);
    }

    @Override
    public List<SessionMeta> listByAgent(String agentId, String userId) {
        return this.store.values().stream().filter(m -> m.agentId().equals(agentId)).filter(m -> userId == null || m.userId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(String sessionId, SessionMeta.SessionStatus status) {
        SessionMeta old = this.store.get(sessionId);
        if (old != null) {
            this.store.put(sessionId, new SessionMeta(old.sessionId(), old.agentId(), old.userId(), old.createdAt(), status));
        }
    }

    @Override
    public void delete(String sessionId) {
        this.store.remove(sessionId);
    }
}

