package com.dtstack.taier.develop.flink.sql.sink.param;

import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;

public enum HbaseSinkParamEnum implements ISqlParamEnum {

    /**
     * hbase 使用 zk 集群地址
     */
    hbase_quorum("hbase_quorum", "zookeeperQuorum", "zookeeper.quorum"),

    /**
     * hbase 使用 zk 的根结点
     */
    hbase_parent("hbase_parent", "zookeeperParent", "zookeeper.znode.parent"),

    /**
     * 更新模式
     */
    updateMode("updateMode", "updateMode", "writeMode"),

    /**
     * 缓存类型
     */
    batchSize("batchSize", "batchSize", "sink.buffer-flush.max-rows"),

    /**
     * 缓存时间
     */
    batchWaitInterval("batchWaitInterval", "batchWaitInterval", "sink.buffer-flush.interval"),

    /**
     * rowKey 名称
     */
    rowKey("rowKey", null, null),

    /**
     * rowKey 类型
     */
    rowKeyType("rowKeyType", null, null),

    /**
     * 并发数
     */
    parallelism("parallelism", "parallelism", "sink.parallelism"),

    /**
     * 表名
     */
    table("table", "table", "table-name");

    /**
     * 前端页面
     */
    private final String front;
    private final String flink110;
    private final String flink112;

    HbaseSinkParamEnum(String front, String flink110, String flink112) {
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
