package com.agentverse.runtime.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.agentscope.core.message.Msg;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.session.Session;
import io.agentscope.core.state.SimpleSessionKey;
import lombok.RequiredArgsConstructor;

/**
 * 旧版会话数据迁移器。
 * <p>
 * 应用启动时把 {@code agentverse:session:<id>:messages} 格式的旧 Key 迁移到新版 Session 后端。
 */
@Component
@ConditionalOnProperty(name = { "agent.session.backend" }, havingValue = "redis")
@RequiredArgsConstructor
public class LegacySessionMigrator {

    private static final Logger log = LoggerFactory.getLogger(LegacySessionMigrator.class);
    static final String OLD_KEY_PREFIX = "agentverse:session:";
    static final String OLD_KEY_SUFFIX = ":messages";
    static final String NEW_LIST_KEY = "memory_messages";

    private final @Nullable StringRedisTemplate redis;
    private final @Nullable Session newSession;
    private final ObjectMapper objectMapper;
    private final @Value("${agent.session.legacy-migration.skip:false}") boolean skip;
    private final AtomicBoolean migrated = new AtomicBoolean(false);

    @EventListener({ ApplicationReadyEvent.class })
    @Async
    public void onAppReady() {
        try {
            int n = migrateAll();
            log.info("LegacySessionMigrator done: {} sessions migrated", n);
        } catch (Exception e) {
            log.error("LegacySessionMigrator failed: {}", e.getMessage(), e);
        }
    }

    public int migrateAll() {
        if (skip) {
            log.debug("LegacySessionMigrator skip flag set, skip SCAN");
            return 0;
        }
        if (redis == null || newSession == null) {
            log.debug("LegacySessionMigrator: redis or newSession not available, skip");
            return 0;
        }
        if (!migrated.compareAndSet(false, true)) {
            log.debug("LegacySessionMigrator already ran, skip");
            return 0;
        }
        String pattern = "agentverse:session:*:messages";
        ScanOptions opts = ScanOptions.scanOptions().match(pattern).count(100L).build();
        int count = 0;
        try (Cursor<String> cursor = redis.scan(opts)) {
            while (cursor.hasNext()) {
                String oldKey = cursor.next();
                if (oldKey == null || !migrateOne(oldKey))
                    continue;
                ++count;
            }
        } catch (Exception e) {
            log.error("LegacySessionMigrator SCAN failed: {}", e.getMessage(), e);
        }
        return count;
    }

    private boolean migrateOne(String oldKey) {
        String sessionId = extractSessionId(oldKey);
        if (sessionId == null) {
            log.warn("LegacySessionMigrator: cannot extract sessionId from key={}", oldKey);
            return false;
        }
        try {
            String json = redis.opsForValue().get(oldKey);
            if (json == null || json.isEmpty()) {
                redis.delete(oldKey);
                return false;
            }
            List<LegacyMessage> legacyList = objectMapper.readValue(json,
                    new TypeReference<List<LegacyMessage>>() {
                    });
            if (legacyList == null || legacyList.isEmpty()) {
                redis.delete(oldKey);
                return false;
            }
            ArrayList<Msg> msgs = new ArrayList<>(legacyList.size());
            for (LegacyMessage lm : legacyList) {
                msgs.add(toMsg(lm));
            }
            newSession.save(SimpleSessionKey.of(sessionId), NEW_LIST_KEY, msgs);
            redis.delete(oldKey);
            return true;
        } catch (Exception e) {
            log.error("LegacySessionMigrator migrateOne failed for key={}: {}", oldKey, e.getMessage(), e);
            return false;
        }
    }

    private static String extractSessionId(String key) {
        if (key == null || !key.startsWith(OLD_KEY_PREFIX) || !key.endsWith(OLD_KEY_SUFFIX)) {
            return null;
        }
        return key.substring(OLD_KEY_PREFIX.length(), key.length() - OLD_KEY_SUFFIX.length());
    }

    private static Msg toMsg(LegacyMessage lm) {
        // 旧消息仅保留角色与文本，构造为新版 TextBlock + Msg
        String role = lm.getRole() == null ? "user" : lm.getRole();
        TextBlock tb = TextBlock.builder().text(lm.getContent() == null ? "" : lm.getContent()).build();
        return Msg.builder()
                .role(io.agentscope.core.message.MsgRole.valueOf(role.toUpperCase()))
                .content(tb)
                .build();
    }

    /** 旧版消息结构。 */
    public static class LegacyMessage {
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
