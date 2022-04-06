package com.dtstack.taier.develop.flink.sql.sink.param;


import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;

/**
 * @author qianyi
 */
public enum ES7SinkParamEnum implements ISqlParamEnum {

    /**
     * 地址
     */
    address("address", "address", "hosts"),

    /**
     * 索引名称
     */
    index("index", "index", "index"),

    /**
     * es7索引映射(动态mapping)，flink_1.12 版本暂不支持
     */
    index_definition("indexDefinition", "index_definition", null),

    /**
     * es id 生成规则
     */
    id("esId", "id", null),

    /**
     * 并发数
     */
    parallelism("parallelism", "parallelism", "sink.parallelism"),

    /**
     * 用户名
     */
    userName("username", "userName", "username"),

    /**
     * 密码
     */
    password("password", "password", "password");

    /**
     * 前端页面
     */
    private final String front;
    private final String flink110;
    private final String flink112;

    ES7SinkParamEnum(String front, String flink110, String flink112) {
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
