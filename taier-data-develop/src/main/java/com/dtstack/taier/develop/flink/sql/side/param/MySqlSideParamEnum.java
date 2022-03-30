package com.dtstack.taier.develop.flink.sql.side.param;

import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;

/**
 * @author qianyi
 */
public enum MySqlSideParamEnum implements ISqlParamEnum {

    /**
     * 地址
     */
    url("jdbcUrl", "url", "url"),

    /**
     * 账号
     */
    userName("username", "userName", "username"),

    /**
     * 密码
     */
    password("password", "password", "password"),

    /**
     * 表名
     */
    tableName("table", "tableName", "table-name"),

    /**
     * 缓存类型
     */
    cache("cache", "cache", "lookup.cache-type"),

    /**
     * 缓存时间
     */
    cacheTTLMs("cacheTTLMs", "cacheTTLMs", "lookup.cache.ttl"),

    /**
     * 缓存大小
     */
    cacheSize("cacheSize", "cacheSize", "lookup.cache.max-rows"),

    /**
     * 错误条数
     */
    errorLimit("errorLimit", "errorLimit", "lookup.error-limit"),

    /**
     * 并发数
     */
    parallelism("parallelism", "parallelism", "lookup.parallelism"),

    /**
     * 异步池大小
     */
    asyncPoolSize("asyncPoolSize", "asyncPoolSize", "vertx.worker-pool-size");

    /**
     * 前端页面
     */
    private final String front;
    private final String flink110;
    private final String flink112;

    MySqlSideParamEnum(String front, String flink110, String flink112) {
        this.front = front;
        this.flink110 = flink110;
        this.flink112 = flink112;
    }

    @Override
    public String getFront() {
        return front;
    }

    @Override
    public String getFlink110() {
        return flink110;
    }

    @Override
    public String getFlink112() {
        return flink112;
    }
}
