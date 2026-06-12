package com.agentverse.runtime.chat;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存会话存储，作为默认实现和 Redis 不可用时的回退
 */
@Component
@Primary
public class InMemorySessionStore implements SessionStore {

    private final Map<String, List<Message>> sessionMessages = new ConcurrentHashMap<>();
    private final Map<String, String> sessionAgents = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> sessionCreatedAt = new ConcurrentHashMap<>();

    @Override
    public String createSession(String agentId) {
        String sessionId = UUID.randomUUID().toString();
        sessionMessages.put(sessionId, Collections.synchronizedList(new ArrayList<>()));
        sessionAgents.put(sessionId, agentId);
        sessionCreatedAt.put(sessionId, LocalDateTime.now());
        return sessionId;
    }

    @Override
    public List<Message> getSession(String sessionId) {
        return sessionMessages.get(sessionId);
    }

    @Override
    public void addMessage(String sessionId, Message message) {
        List<Message> messages = sessionMessages.get(sessionId);
        if (messages != null) {
            messages.add(message);
        }
    }

    @Override
    public void deleteSession(String sessionId) {
        sessionMessages.remove(sessionId);
        sessionAgents.remove(sessionId);
        sessionCreatedAt.remove(sessionId);
    }

    @Override
    public boolean exists(String sessionId) {
        return sessionMessages.containsKey(sessionId);
    }

    @Override
    public String getAgentId(String sessionId) {
        return sessionAgents.get(sessionId);
    }

    @Override
    public LocalDateTime getCreatedAt(String sessionId) {
        return sessionCreatedAt.get(sessionId);
    }

    @Override
    public Set<String> getAllSessionIds() {
        return sessionMessages.keySet();
    }

    @Override
    public List<SessionMetadata> listSessions() {
        return sessionMessages.keySet().stream()
                .map(id -> new SessionMetadata(id, sessionAgents.get(id), sessionCreatedAt.get(id)))
                .filter(m -> m.getAgentId() != null)
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }
}
