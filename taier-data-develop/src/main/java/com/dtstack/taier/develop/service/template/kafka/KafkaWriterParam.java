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
