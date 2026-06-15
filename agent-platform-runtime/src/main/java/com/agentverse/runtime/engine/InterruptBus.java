/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import io.agentscope.harness.agent.HarnessAgent;

public interface InterruptBus {
    public void register(String var1, HarnessAgent var2);

    public void unregister(String var1);

    public void publish(String var1);

    public boolean containsLocal(String var1);

    public int localSize();
}

