/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.runtime.engine.InterruptBus;
import io.agentscope.harness.agent.HarnessAgent;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalInterruptBus
implements InterruptBus {
    private static final Logger log = LoggerFactory.getLogger(LocalInterruptBus.class);
    private final ConcurrentHashMap<String, HarnessAgent> localAgents = new ConcurrentHashMap();

    @Override
    public void register(String sessionId, HarnessAgent agent) {
        if (sessionId == null || agent == null) {
            return;
        }
        this.localAgents.put(sessionId, agent);
        log.debug("InterruptBus.register: session={}", (Object)sessionId);
    }

    @Override
    public void unregister(String sessionId) {
        if (sessionId == null) {
            return;
        }
        HarnessAgent removed = this.localAgents.remove(sessionId);
        if (removed != null) {
            log.debug("InterruptBus.unregister: session={}", (Object)sessionId);
        }
    }

    @Override
    public void publish(String sessionId) {
        if (sessionId == null) {
            return;
        }
        HarnessAgent agent = this.localAgents.remove(sessionId);
        if (agent == null) {
            log.debug("InterruptBus.publish: session={} not found locally (no-op)", (Object)sessionId);
            return;
        }
        try {
            agent.interrupt();
            log.info("InterruptBus.publish: interrupted session={}", (Object)sessionId);
        }
        catch (Exception e) {
            log.warn("InterruptBus.publish: failed to interrupt session={}: {}", (Object)sessionId, (Object)e.getMessage());
        }
    }

    @Override
    public boolean containsLocal(String sessionId) {
        return sessionId != null && this.localAgents.containsKey(sessionId);
    }

    @Override
    public int localSize() {
        return this.localAgents.size();
    }
}

