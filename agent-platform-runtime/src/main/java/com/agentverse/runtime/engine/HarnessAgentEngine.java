package com.agentverse.runtime.engine;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.agentscope.core.agent.Event;
import io.agentscope.core.agent.EventType;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.harness.agent.HarnessAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * HarnessAgent 引擎，提供 Agent 对话执行能力
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HarnessAgentEngine {

    private final AgentLoaderService agentLoaderService;

    /**
     * 正在执行的会话映射，用于中断
     */
    private final ConcurrentHashMap<String, HarnessAgent> runningAgents = new ConcurrentHashMap<>();

    /**
     * 执行同步对话
     */
    public String executeChat(String agentId, String sessionId, String userMessage) {
        log.info("Executing chat: agentId={}, sessionId={}", agentId, sessionId);

        HarnessAgent agent = agentLoaderService.loadAgent(agentId);

        RuntimeContext context = RuntimeContext.builder()
                .sessionId(sessionId)
                .build();

        Msg userMsg = Msg.builder()
                .textContent(userMessage)
                .build();

        try {
            runningAgents.put(sessionId, agent);
            Msg response = agent.call(userMsg, context).block();
            return response != null ? response.getTextContent() : "";
        } catch (Exception e) {
            log.error("Chat execution failed: agentId={}, sessionId={}", agentId, sessionId, e);
            throw new RuntimeException("Chat execution failed: " + e.getMessage(), e);
        } finally {
            runningAgents.remove(sessionId);
        }
    }

    /**
     * 执行流式对话，返回 Flux<String>
     */
    public Flux<String> streamChat(String agentId, String sessionId, String userMessage) {
        log.info("Streaming chat: agentId={}, sessionId={}", agentId, sessionId);

        HarnessAgent agent = agentLoaderService.loadAgent(agentId);

        RuntimeContext context = RuntimeContext.builder()
                .sessionId(sessionId)
                .build();

        Msg userMsg = Msg.builder()
                .textContent(userMessage)
                .build();

        runningAgents.put(sessionId, agent);

        return agent.stream(userMsg, context)
                .filter(event -> event != null && event.getMessage() != null && isValidEventType(event.getType()))
                .map(this::extractContent)
                .filter(content -> content != null && !content.isEmpty())
                .doOnComplete(() -> {
                    runningAgents.remove(sessionId);
                    log.info("Stream completed: sessionId={}", sessionId);
                })
                .doOnError(e -> {
                    runningAgents.remove(sessionId);
                    log.error("Stream error: sessionId={}", sessionId, e);
                })
                .doOnCancel(() -> {
                    runningAgents.remove(sessionId);
                    log.info("Stream cancelled: sessionId={}", sessionId);
                });
    }

    /**
     * 中断正在执行的对话
     */
    public boolean interruptChat(String sessionId) {
        log.info("Interrupting chat: sessionId={}", sessionId);
        HarnessAgent agent = runningAgents.remove(sessionId);
        if (agent != null) {
            agent.interrupt();
            log.info("Chat interrupted: sessionId={}", sessionId);
            return true;
        }
        return false;
    }

    /**
     * 从事件中提取文本内容
     */
    private String extractContent(Event event) {
        if (event == null || event.getMessage() == null) {
            return null;
        }
        EventType type = event.getType();
        if (type == EventType.REASONING || type == EventType.SUMMARY) {
            return event.getMessage().getTextContent();
        }
        if (type == EventType.HINT) {
            return event.getMessage().getTextContent();
        }
        return null;
    }

    private boolean isValidEventType(EventType type) {
        return type == EventType.REASONING || type == EventType.SUMMARY || type == EventType.HINT;
    }
}
