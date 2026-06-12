/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import lombok.Generated;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyBatisPlusMetaObjectHandler
implements MetaObjectHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MyBatisPlusMetaObjectHandler.class);

    public void insertFill(MetaObject metaObject) {
        log.debug("Start insert fill...");
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    public void updateFill(MetaObject metaObject) {
        log.debug("Start update fill...");
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }
}

