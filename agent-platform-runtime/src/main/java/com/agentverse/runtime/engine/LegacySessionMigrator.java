/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agentscope.core.message.ContentBlock;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.session.Session;
import io.agentscope.core.state.SessionKey;
import io.agentscope.core.state.SimpleSessionKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name={"agent.session.backend"}, havingValue="redis")
public class LegacySessionMigrator {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(LegacySessionMigrator.class);
    static final String OLD_KEY_PREFIX = "agentverse:session:";
    static final String OLD_KEY_SUFFIX = ":messages";
    static final String NEW_LIST_KEY = "memory_messages";
    private final StringRedisTemplate redis;
    private final Session newSession;
    private final ObjectMapper objectMapper;
    private final boolean skip;
    private final AtomicBoolean migrated = new AtomicBoolean(false);

    public LegacySessionMigrator(@Nullable StringRedisTemplate redis, @Nullable Session newSession, ObjectMapper objectMapper, @Value(value="${agent.session.legacy-migration.skip:false}") boolean skip) {
        this.redis = redis;
        this.newSession = newSession;
        this.objectMapper = objectMapper;
        this.skip = skip;
    }

    @EventListener(value={ApplicationReadyEvent.class})
    @Async
    public void onAppReady() {
        try {
            int n = this.migrateAll();
            log.info("LegacySessionMigrator done: {} sessions migrated", (Object)n);
        }
        catch (Exception e) {
            log.error("LegacySessionMigrator failed: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    public int migrateAll() {
        if (this.skip) {
            log.debug("LegacySessionMigrator skip flag set, skip SCAN");
            return 0;
        }
        if (this.redis == null || this.newSession == null) {
            log.debug("LegacySessionMigrator: redis or newSession not available, skip");
            return 0;
        }
        if (!this.migrated.compareAndSet(false, true)) {
            log.debug("LegacySessionMigrator already ran, skip");
            return 0;
        }
        String pattern = "agentverse:session:*:messages";
        ScanOptions opts = ScanOptions.scanOptions().match(pattern).count(100L).build();
        int count = 0;
        try (Cursor cursor = this.redis.scan(opts);){
            while (cursor.hasNext()) {
                String oldKey = (String)cursor.next();
                if (oldKey == null || !this.migrateOne(oldKey)) continue;
                ++count;
            }
        }
        catch (Exception e) {
            log.error("LegacySessionMigrator SCAN failed: {}", (Object)e.getMessage(), (Object)e);
        }
        return count;
    }

    private boolean migrateOne(String oldKey) {
        String sessionId = LegacySessionMigrator.extractSessionId(oldKey);
        if (sessionId == null) {
            log.warn("LegacySessionMigrator: cannot extract sessionId from key={}", (Object)oldKey);
            return false;
        }
        try {
            String json = (String)this.redis.opsForValue().get((Object)oldKey);
            if (json == null || json.isEmpty()) {
                this.redis.delete((Object)oldKey);
                return false;
            }
            List legacyList = (List)this.objectMapper.readValue(json, (TypeReference)new TypeReference<List<LegacyMessage>>(){});
            if (legacyList == null || legacyList.isEmpty()) {
                this.redis.delete((Object)oldKey);
                return false;
            }
            ArrayList<Msg> msgs = new ArrayList<Msg>(legacyList.size());
            for (LegacyMessage lm : legacyList) {
                msgs.add(LegacySessionMigrator.toMsg(lm));
            }
            this.newSession.save((SessionKey)SimpleSessionKey.of((String)sessionId), NEW_LIST_KEY, msgs);
            this.redis.delete((Object)oldKey);
            log.info("Migrated legacy session: {} ({} messages)", (Object)sessionId, (Object)msgs.size());
            return true;
        }
        catch (Exception e) {
            log.error("Migrating key={} failed: {}", new Object[]{oldKey, e.getMessage(), e});
            return false;
        }
    }

    static String extractSessionId(String key) {
        if (key == null) {
            return null;
        }
        if (!key.startsWith(OLD_KEY_PREFIX) || !key.endsWith(OLD_KEY_SUFFIX)) {
            return null;
        }
        return key.substring(OLD_KEY_PREFIX.length(), key.length() - OLD_KEY_SUFFIX.length());
    }

    private static Msg toMsg(LegacyMessage lm) {
        MsgRole role;
        try {
            role = MsgRole.valueOf((String)lm.role());
        }
        catch (Exception e) {
            log.warn("Unknown legacy role={}, fallback to USER", (Object)lm.role());
            role = MsgRole.USER;
        }
        return Msg.builder().role(role).content((ContentBlock)TextBlock.builder().text(lm.content() == null ? "" : lm.content()).build()).build();
    }

    public record LegacyMessage(String role, String content, long timestamp) {
        public LegacyMessage {
            if (role == null) {
                role = "USER";
            }
            if (content == null) {
                content = "";
            }
        }
    }
}

