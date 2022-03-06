package com.dtstack.taier.develop.service.template.kafka;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.service.template.BaseWriterPlugin;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author sanyue
 * @date 2018/9/13
 */
public abstract class KafkaBaseWriter  extends BaseWriterPlugin {
    private String zookeeperConnect;

    private String bootstrapServers;

    private String encoding;

    private String topic;

    private String brokerList;

    private Map<String, String> producerSettings;
    private List<String> tableFields;
    private Map<String, String> kafkaSettings;
    private List<String> partitionAssignColumns;
    protected boolean dataCompelOrder;

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

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBrokerList() {
        return brokerList;
    }

    public void setBrokerList(String brokerList) {
        this.brokerList = brokerList;
    }

    public Map<String, String> getProducerSettings() {
        return producerSettings;
    }

    public void setProducerSettings(Map<String, String> producerSettings) {
        this.producerSettings = producerSettings;
    }

    public List<String> getTableFields() {
        return tableFields;
    }

    public void setTableFields(List<String> tableFields) {
        this.tableFields = tableFields;
    }

    public Map<String, String> getKafkaSettings() {
        return kafkaSettings;
    }

    public void setKafkaSettings(Map<String, String> kafkaSettings) {
        this.kafkaSettings = kafkaSettings;
    }

    public List<String> getPartitionAssignColumns() {
        return partitionAssignColumns;
    }

    public void setPartitionAssignColumns(List<String> partitionAssignColumns) {
        this.partitionAssignColumns = partitionAssignColumns;
    }

    public boolean isDataCompelOrder() {
        return dataCompelOrder;
    }

    public void setDataCompelOrder(boolean dataCompelOrder) {
        this.dataCompelOrder = dataCompelOrder;
    }

    public void checkFormat(JSONObject data) {
        Boolean isKafka9 = DataSourceType.KAFKA_09.getVal().equals(data.getInteger("type"));
        data = data.getJSONObject("parameter");
        if (StringUtils.isBlank(data.getString("topic"))){
            throw new RdosDefineException("kafka数据源 topic 不能为空");
        }
        JSONObject producerSettings = data.getJSONObject("producerSettings");
        if (isKafka9 && StringUtils.isEmpty(data.getString("brokerList"))) {
            throw new RdosDefineException("kafka数据源 bootstrapServers 不能为空");
        }
        if (!isKafka9 && StringUtils.isEmpty(producerSettings.getString("bootstrap.servers"))) {
            throw new RdosDefineException("kafka数据源 bootstrap.servers 不能为空");
        }
    }

}
