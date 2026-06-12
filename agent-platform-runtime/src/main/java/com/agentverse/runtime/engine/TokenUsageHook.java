/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.runtime.model.service.ChatUsageService;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.hook.Hook;
import io.agentscope.core.hook.HookEvent;
import io.agentscope.core.hook.PostCallEvent;
import io.agentscope.core.hook.PostReasoningEvent;
import io.agentscope.core.hook.RuntimeContextAware;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.ChatUsage;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class TokenUsageHook
implements Hook,
RuntimeContextAware {
    private static final Logger log = LoggerFactory.getLogger(TokenUsageHook.class);
    public static final String ATTR_MODEL_CONFIG_ID = "modelConfigId";
    private final ChatUsageService chatUsageService;
    private final ConcurrentHashMap<String, SessionUsage> accumulators = new ConcurrentHashMap();
    private volatile RuntimeContext currentContext;

    public TokenUsageHook(ChatUsageService chatUsageService) {
        this.chatUsageService = chatUsageService;
    }

    public void setRuntimeContext(RuntimeContext context) {
        this.currentContext = context;
        if (context == null) {
            // empty if block
        }
    }

    public <T extends HookEvent> Mono<T> onEvent(T event) {
        if (event instanceof PostReasoningEvent) {
            PostReasoningEvent e = (PostReasoningEvent)event;
            this.accumulate(e.getReasoningMessage());
            return Mono.just(event);
        }
        if (event instanceof PostCallEvent) {
            PostCallEvent e = (PostCallEvent)event;
            return this.persistOnCall(event);
        }
        return Mono.just(event);
    }

    private <T extends HookEvent> Mono<T> persistOnCall(T event) {
        String sessionId = this.currentSessionId();
        if (sessionId == null) {
            log.warn("PostCallEvent with null RuntimeContext.sessionId, skip saveUsage");
            return Mono.just(event);
        }
        SessionUsage usage = this.accumulators.get(sessionId);
        if ((usage == null || !usage.hasUsage) && event instanceof PostCallEvent) {
            ChatUsage u;
            PostCallEvent e = (PostCallEvent)event;
            ChatUsage chatUsage = u = e.getFinalMessage() != null ? e.getFinalMessage().getChatUsage() : null;
            if (u != null) {
                usage = usage == null ? new SessionUsage() : usage;
                usage.totalInputTokens += (long)u.getInputTokens();
                usage.totalOutputTokens += (long)u.getOutputTokens();
                usage.hasUsage = true;
                this.accumulators.put(sessionId, usage);
            }
        }
        if (usage == null || !usage.hasUsage) {
            log.warn("No ChatUsage for session={}, skip saveUsage", (Object)sessionId);
            this.accumulators.remove(sessionId);
            return Mono.just(event);
        }
        long in = usage.totalInputTokens;
        long out = usage.totalOutputTokens;
        String modelConfigId = this.currentModelConfigId();
        this.accumulators.remove(sessionId);
        return Mono.fromCallable(() -> {
            this.chatUsageService.saveUsage(sessionId, modelConfigId, in, out);
            return event;
        }).subscribeOn(Schedulers.boundedElastic()).onErrorResume(ex -> {
            log.error("saveUsage failed for session={}", (Object)sessionId, ex);
            return Mono.just((Object)event);
        });
    }

    private void accumulate(Msg msg) {
        String sessionId = this.currentSessionId();
        if (sessionId == null || msg == null) {
            return;
        }
        ChatUsage u = msg.getChatUsage();
        if (u != null) {
            this.accumulators.compute(sessionId, (k, old) -> {
                SessionUsage s = old == null ? new SessionUsage() : old;
                s.hasUsage = true;
                s.totalInputTokens += (long)u.getInputTokens();
                s.totalOutputTokens += (long)u.getOutputTokens();
                return s;
            });
        }
    }

    private String currentSessionId() {
        RuntimeContext ctx = this.currentContext;
        return ctx == null ? null : ctx.getSessionId();
    }

    private String currentModelConfigId() {
        RuntimeContext ctx = this.currentContext;
        if (ctx == null) {
            return null;
        }
        Object v = ctx.get(ATTR_MODEL_CONFIG_ID);
        return v == null ? null : v.toString();
    }

    private static final class SessionUsage {
        long totalInputTokens = 0L;
        long totalOutputTokens = 0L;
        boolean hasUsage = false;

        private SessionUsage() {
        }
    }
}

