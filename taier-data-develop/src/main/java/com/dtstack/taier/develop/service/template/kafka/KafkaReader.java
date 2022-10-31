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

package com.dtstack.taier.develop.service.template.kafka;

import com.dtstack.taier.develop.service.template.PluginName;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 * for kafka 1.0 and latter
 * @author xiaochen
 */
public class KafkaReader extends KafkaBaseReader {

    /**
     * 可选值：
     * group-offsets：      从ZK / Kafka brokers中指定的消费组已经提交的offset开始消费
     * earliest-offset：    从最早的偏移量开始(如果可能)
     * latest-offset：      从最新的偏移量开始(如果可能)
     * timestamp：          从每个分区的指定的时间戳开始
     * specific-offsets：   从每个分区的指定的特定偏移量开始
     * 必选：否
     * 字段类型：String
     * 默认值：group-offsets
     */
    private String mode;

    /**
     * 必选：当mode为timestamp时必选
     * 字段类型：Long
     * 默认值：无
     */
    private Long timestamp;

    /**
     * 必选：当mode为specific-offsets时必选
     * 字段类型：String
     * 格式：partition:0,offset:42;partition:1,offset:300;partition:2,offset:300
     * 默认值：无
     */
    private String offset;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    @Override
    public String pluginName() {
        return PluginName.KAFKA_R;
    }
}
