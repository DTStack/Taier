package com.dtstack.taier.develop.flink.sql.source.param;


import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;

/**
 * @author qianyi
 */
public enum KafkaSourceParamEnum implements ISqlParamEnum {

    /**
     * 主题
     */
    TOPIC("topic", "topic", "topic"),

    /**
     * 地址
     */
    BOOT_STRAP_SERVERS("bootstrapServers", "bootstrapServers", "properties.bootstrap.servers"),

    /**
     * 读取类型
     */

    SOURCE_DATA_TYPE("sourceDataType", "sourcedatatype", "format"),

    /**
     * 读取类型是csv 时，需要拼接上。
     */
    FIELD_DELIMITER("fieldDelimiter", "fieldDelimiter", "csv.field-delimiter"),

    /**
     * 读取类型是avro 时，需要拼接上。
     */
    SCHEMA_INFO("schemaInfo", "schemaInfo", null),

    /**
     * 消费启始时间
     */
    TIME_STAMP_OFFSET("timestampOffset", "timestampOffset", "scan.startup.timestamp-millis"),

    /**
     * 偏移量
     */
    OFFSET_RESET("offsetReset", "offsetReset", "scan.startup.mode"),

    /**
     * 编码，1.12版本没有
     */
    @Deprecated
    CHARSET_NAME("charsetName", "charsetName", "encoding"),

    /**
     * 时区, 1.12版本没有
     */
    @Deprecated
    TIMEZONE("timezone", "timezone", "timezone"),

    /**
     * topic是不是正则,前端未使用
     */
    @Deprecated
    TOPIC_IS_PATTERN("topicIsPattern", "topicIsPattern", "topic-pattern"),

    /**
     * timeType， proctime or EventTime
     */
    TIME_TYPE("timeType", null, null),

    /**
     * 时间特征 EventTime  timeColumn
     */
    TIME_COLUMN("timeColumn", null, null),

    /**
     * 时间特征 EventTime offset
     */
    OFFSET("offset", null, null),

    /**
     * 时间特征 procTime
     */
    PROC_TIME("procTime", null, null),

    /**
     * 时间特征 EventTime offset单位
     */
    OFFSET_UNIT("offsetUnit", null, null),

    /**
     * 自定义offset 偏移量， flink1.10以前，直接拼在offsetReset里
     */
    OFFSET_VALUE("offsetValue", null, "scan.startup.specific-offsets"),

    /**
     * 并发数
     */
    parallelism("parallelism", "parallelism", "scan.parallelism");

    /**
     * 前端页面
     */
    private final String front;
    private final String flink110;
    private final String flink112;

    KafkaSourceParamEnum(String front, String flink110, String flink112) {
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
