package com.agentverse.common.utils;

import cn.hutool.core.util.IdUtil;

/**
 * ID生成工具类
 */
public class IdUtils {

    private IdUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 生成UUID（不带横线）
     *
     * @return UUID字符串
     */
    public static String uuid() {
        return IdUtil.simpleUUID();
    }

    /**
     * 生成带横线的UUID
     *
     * @return UUID字符串
     */
    public static String uuidWithDash() {
        return IdUtil.randomUUID();
    }

    /**
     * 生成雪花算法ID
     *
     * @return 雪花ID字符串
     */
    public static String snowflakeId() {
        return IdUtil.getSnowflakeNextIdStr();
    }
}
