package com.dtstack.taier.datasource.plugin.kafka;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 22:56 2020/2/26
 * @Description：TODO
 */
public class KafkaConsistent {
    /**
     * Kafka 会话超时时间
     */
    public static final int SESSION_TIME_OUT = 30000;

    /**
     * Kafka 连接超时时间
     */
    public static final int CONNECTION_TIME_OUT = 5000;

    /**
     * Kafka 默认创建的 TOPIC 名称
     */
    public static final String KAFKA_DEFAULT_CREATE_TOPIC = "__consumer_offsets";

    /**
     * Kafka 默认组名称
     */
    public static final String KAFKA_GROUP = "STREAM_APP_KAFKA";

    /**
     * kafka SASL/PLAIN 认证
     */
    public static final String KAFKA_SASL_PLAIN_CONTENT = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";

    public static final String KAFKA_SASL_SCRAM_CONTENT = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
}
