package com.agentverse.runtime.chat;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储，使用内存 Map 存储会话消息
 * 阶段四实现持久化时替换此实现
 */
@Component
public class SessionStore {

    /**
     * 会话消息存储：sessionId -> 消息列表
     */
    private final Map<String, List<Message>> sessionMessages = new ConcurrentHashMap<>();

    /**
     * 会话元数据：sessionId -> agentId
     */
    private final Map<String, String> sessionAgents = new ConcurrentHashMap<>();

    /**
     * 会话创建时间：sessionId -> createdAt
     */
    private final Map<String, java.time.LocalDateTime> sessionCreatedAt = new ConcurrentHashMap<>();

    /**
     * 创建会话
     */
    public String createSession(String agentId) {
        String sessionId = UUID.randomUUID().toString();
        sessionMessages.put(sessionId, Collections.synchronizedList(new ArrayList<>()));
        sessionAgents.put(sessionId, agentId);
        sessionCreatedAt.put(sessionId, java.time.LocalDateTime.now());
        return sessionId;
    }

    /**
     * 获取会话消息列表
     */
    public List<Message> getSession(String sessionId) {
        return sessionMessages.get(sessionId);
    }

    /**
     * 添加消息
     */
    public void addMessage(String sessionId, Message message) {
        List<Message> messages = sessionMessages.get(sessionId);
        if (messages != null) {
            messages.add(message);
        }
    }

    /**
     * 删除会话
     */
    public void deleteSession(String sessionId) {
        sessionMessages.remove(sessionId);
        sessionAgents.remove(sessionId);
        sessionCreatedAt.remove(sessionId);
    }

    /**
     * 会话是否存在
     */
    public boolean exists(String sessionId) {
        return sessionMessages.containsKey(sessionId);
    }

    /**
     * 获取会话关联的 Agent ID
     */
    public String getAgentId(String sessionId) {
        return sessionAgents.get(sessionId);
    }

    /**
     * 获取会话创建时间
     */
    public java.time.LocalDateTime getCreatedAt(String sessionId) {
        return sessionCreatedAt.get(sessionId);
    }

    /**
     * 获取所有会话 ID
     */
    public Set<String> getAllSessionIds() {
        return sessionMessages.keySet();
    }
}
