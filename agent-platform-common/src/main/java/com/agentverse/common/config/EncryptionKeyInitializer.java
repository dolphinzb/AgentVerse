/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.config;

import com.agentverse.common.util.AesEncryptUtil;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.UUID;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionKeyInitializer {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(EncryptionKeyInitializer.class);
    @Value(value="${AGENTVERSE_ENCRYPTION_KEY:${agent.encryption-key:}}")
    private String encryptionKey;

    @PostConstruct
    public void init() {
        if (this.encryptionKey == null || this.encryptionKey.isEmpty()) {
            String generatedKey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
            log.warn("========================================");
            log.warn("\u672a\u914d\u7f6e\u52a0\u5bc6\u5bc6\u94a5\uff0c\u5df2\u81ea\u52a8\u751f\u6210\u4e34\u65f6\u5bc6\u94a5\u3002");
            log.warn("\u8bf7\u5728\u73af\u5883\u53d8\u91cf AGENTVERSE_ENCRYPTION_KEY \u6216 application.yml \u7684 agent.encryption-key \u4e2d\u914d\u7f6e\u56fa\u5b9a\u5bc6\u94a5\u3002");
            log.warn("\u4e34\u65f6\u5bc6\u94a5: {}", (Object)generatedKey);
            log.warn("========================================");
            this.encryptionKey = generatedKey;
        }
        AesEncryptUtil.setEncryptionKey(this.encryptionKey);
        log.info("\u52a0\u5bc6\u5bc6\u94a5\u521d\u59cb\u5316\u5b8c\u6210");
    }
}

