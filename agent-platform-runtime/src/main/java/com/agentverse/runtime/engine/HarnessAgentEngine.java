/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.runtime.engine.AgentLoaderService;
import com.agentverse.runtime.engine.InterruptBus;
import io.agentscope.core.agent.Event;
import io.agentscope.core.agent.EventType;
import io.agentscope.core.agent.EventType;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.harness.agent.HarnessAgent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class HarnessAgentEngine {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(HarnessAgentEngine.class);
    private final AgentLoaderService agentLoaderService;
    private final InterruptBus interruptBus;
    private final ConcurrentHashMap<String, List<Msg>> sessionMemory = new ConcurrentHashMap();

    public Msg executeChat(String agentId, String sessionId, String userMessage) {
        log.info("Executing chat: agentId={}, sessionId={}", (Object)agentId, (Object)sessionId);
        HarnessAgent agent = this.agentLoaderService.loadAgent(agentId);
        RuntimeContext context = this.buildContext(agentId, sessionId);
        Msg userMsg = Msg.builder().textContent(userMessage).build();
        try {
            this.interruptBus.register(sessionId, agent);
            this.appendMemory(sessionId, userMsg);
            Msg response = (Msg)agent.call(userMsg, context).block();
            if (response != null) {
                this.appendMemory(sessionId, response);
            }
            Msg msg = response;
            return msg;
        }
        catch (Exception e) {
            log.error("Chat execution failed: agentId={}, sessionId={}", new Object[]{agentId, sessionId, e});
            throw new RuntimeException("Chat execution failed: " + e.getMessage(), e);
        }
        finally {
            this.interruptBus.unregister(sessionId);
        }
    }

    public Flux<String> streamChat(String agentId, String sessionId, String userMessage) {
        log.info("Streaming chat: agentId={}, sessionId={}", (Object)agentId, (Object)sessionId);
        HarnessAgent agent = this.agentLoaderService.loadAgent(agentId);
        RuntimeContext context = this.buildContext(agentId, sessionId);
        Msg userMsg = Msg.builder().textContent(userMessage).build();
        this.appendMemory(sessionId, userMsg);
        this.interruptBus.register(sessionId, agent);
        return agent.stream(userMsg, context)
                .<String>handle((event, sink) -> {
                    String text = this.extractContent(event);
                    if (text != null && !text.isEmpty()) {
                        sink.next(text);
                    }
                }).doOnComplete(() -> {
            this.interruptBus.unregister(sessionId);
            log.info("Stream completed: sessionId={}", (Object)sessionId);
        }).doOnError(e -> {
            this.interruptBus.unregister(sessionId);
            log.error("Stream error: sessionId={}", (Object)sessionId, e);
        }).doOnCancel(() -> {
            this.interruptBus.unregister(sessionId);
            log.info("Stream cancelled: sessionId={}", (Object)sessionId);
        });
    }

    private RuntimeContext buildContext(String agentId, String sessionId) {
        RuntimeContext.Builder builder = RuntimeContext.builder().sessionId(sessionId);
        String modelConfigId = this.agentLoaderService.getModelConfigId(agentId);
        if (modelConfigId != null) {
            builder.put("modelConfigId", (Object)modelConfigId);
        }
        return builder.build();
    }

    public boolean interruptChat(String sessionId) {
        log.info("Interrupting chat: sessionId={}", (Object)sessionId);
        boolean wasLocal = this.interruptBus.containsLocal(sessionId);
        this.interruptBus.publish(sessionId);
        log.info("Chat interrupt published: sessionId={} (localHit={})", (Object)sessionId, (Object)wasLocal);
        return wasLocal;
    }

    public List<Msg> getMemory(String agentId, String sessionId) {
        List<Msg> snapshot;
        if (agentId == null) {
            log.debug("getMemory called with null agentId for sessionId={}", (Object)sessionId);
        }
        return (snapshot = this.sessionMemory.get(sessionId)) == null ? Collections.emptyList() : List.copyOf(snapshot);
    }

    private void appendMemory(String sessionId, Msg msg) {
        if (sessionId == null || msg == null) {
            return;
        }
        this.sessionMemory.compute(sessionId, (k, old) -> {
            ArrayList<Msg> base = old == null ? new ArrayList<Msg>(2) : new ArrayList(old);
            base.add(msg);
            return List.copyOf(base);
        });
    }

    String extractContent(Event event) {
        if (event == null || event.getMessage() == null) {
            return null;
        }
        // 只下发最终回答（AGENT_RESULT 且 isLast），过滤 REASONING / TOOL_RESULT
        // / HINT / SUMMARY 等事件。原因：每个事件的 Msg.getTextContent() 返回
        // 累积的完整文本（非增量），若全量 emit，前端会把同一回答拼接出 N 遍。
        // 思考 / 工具 / 总结的可视化在 Step1 OpenSpec 中通过 event: 字段分发实现。
        EventType type = event.getType();
        if (type != EventType.AGENT_RESULT) {
            return null;
        }
        if (!event.isLast()) {
            return null;
        }
        return event.getMessage().getTextContent();
    }

    Map<String, List<Msg>> debugSessionMemory() {
        return Map.copyOf(this.sessionMemory);
    }

    @Generated
    public HarnessAgentEngine(AgentLoaderService agentLoaderService, InterruptBus interruptBus) {
        this.agentLoaderService = agentLoaderService;
        this.interruptBus = interruptBus;
    }
}

