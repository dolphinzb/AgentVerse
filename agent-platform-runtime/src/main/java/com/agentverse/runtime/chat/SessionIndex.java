/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.chat;

import com.agentverse.runtime.chat.SessionMeta;
import java.util.List;
import java.util.Optional;

public interface SessionIndex {
    public String create(String var1, String var2);

    public Optional<SessionMeta> get(String var1);

    public boolean exists(String var1);

    public List<SessionMeta> listByAgent(String var1, String var2);

    public void updateStatus(String var1, SessionMeta.SessionStatus var2);

    public void delete(String var1);
}

