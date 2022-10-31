/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
