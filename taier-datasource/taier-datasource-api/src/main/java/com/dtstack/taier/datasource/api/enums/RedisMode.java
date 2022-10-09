package com.dtstack.taier.datasource.api.enums;

/**
 * company: www.dtstack.com
 *
 * @author ：nanqi
 * date ：Created in 14:15 2020/2/5
 * description：Redis 模式
 */
public enum RedisMode {
    /**
     * 单点
     */
    Standalone(1),

    /**
     * 哨兵
     */
    Sentinel(2),

    /**
     * 集群
     */
    Cluster(3);

    private int value;

    public int getValue() {
        return value;
    }

    RedisMode(int value) {
        this.value = value;
    }

    public static RedisMode getRedisModel(int mode) {
        for (RedisMode value : values()) {
            if (mode == value.getValue()) {
                return value;
            }
        }
        return null;
    }
}
