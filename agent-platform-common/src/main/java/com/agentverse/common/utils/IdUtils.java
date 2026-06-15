/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.utils;

import cn.hutool.core.util.IdUtil;

public class IdUtils {
    private IdUtils() {
    }

    public static String uuid() {
        return IdUtil.simpleUUID();
    }

    public static String uuidWithDash() {
        return IdUtil.randomUUID();
    }

    public static String snowflakeId() {
        return IdUtil.getSnowflakeNextIdStr();
    }
}

