/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.chat;

import java.time.Instant;

public record SessionMeta(String sessionId, String agentId, String userId, Instant createdAt, SessionStatus status) {

    public static enum SessionStatus {
        ACTIVE,
        STOPPED,
        DELETED;

    }
}

