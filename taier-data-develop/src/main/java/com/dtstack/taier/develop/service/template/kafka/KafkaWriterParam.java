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

import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;
import java.util.Map;

/**
 * @author sanyue
 * @date 2018/9/13
 */
public class KafkaWriterParam extends DaPluginParam {
    // 采集字段列表
    private List<String> tableFields;
    // 是否保证强制有序
    protected boolean dataSequence;
    private String topic;
    protected List<String> partitionKey;
    private Map<String, String> kafkaSettings;
    private String zookeeperConnect;
    private String bootstrapServers;
    /**
     * json、text
     */
    private String codec;

    public List<String> getTableFields() {
        return tableFields;
    }

    public void setTableFields(List<String> tableFields) {
        this.tableFields = tableFields;
    }

    public boolean isDataSequence() {
        return dataSequence;
    }

    public void setDataSequence(boolean dataSequence) {
        this.dataSequence = dataSequence;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(List<String> partitionKey) {
        this.partitionKey = partitionKey;
    }

    public Map<String, String> getKafkaSettings() {
        return kafkaSettings;
    }

    public void setKafkaSettings(Map<String, String> kafkaSettings) {
        this.kafkaSettings = kafkaSettings;
    }

    public String getZookeeperConnect() {
        return zookeeperConnect;
    }

    public void setZookeeperConnect(String zookeeperConnect) {
        this.zookeeperConnect = zookeeperConnect;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }
}
