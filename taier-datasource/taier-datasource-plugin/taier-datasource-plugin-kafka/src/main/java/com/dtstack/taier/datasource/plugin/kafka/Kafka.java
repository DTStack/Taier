package com.dtstack.taier.datasource.plugin.kafka;

import com.dtstack.taier.datasource.plugin.kafka.util.KafkaUtil;
import com.dtstack.taier.datasource.api.client.IKafka;
import com.dtstack.taier.datasource.api.dto.KafkaConsumerDTO;
import com.dtstack.taier.datasource.api.dto.KafkaPartitionDTO;
import com.dtstack.taier.datasource.api.dto.KafkaTopicDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KafkaSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * kafka 客户端，支持 kafka 0.10、10.11、1.x、2.x 版本
 * 支持 kafka kerberos认证(SASL/GSSAPI)、用户名密码认证(SASL/PLAIN)
 *
 * @author ：wangchuan
 * date：Created in 下午4:39 2021/7/9
 * company: www.dtstack.com
 */
public class Kafka implements IKafka {
    @Override
    public Boolean testCon(ISourceDTO sourceDTO) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        return KafkaUtil.checkConnection(kafkaSourceDTO);
    }

    @Override
    public String getAllBrokersAddress(ISourceDTO sourceDTO) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        if (StringUtils.isNotBlank(kafkaSourceDTO.getBrokerUrls())) {
            return kafkaSourceDTO.getBrokerUrls();
        }
        return KafkaUtil.getAllBrokersAddressFromZk(kafkaSourceDTO.getUrl());
    }

    @Override
    public List<String> getTopicList(ISourceDTO sourceDTO) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        List<String> topics = KafkaUtil.getTopicList(kafkaSourceDTO);
        // 过滤掉存储消费者组 offset 的 topic
        return topics.stream().filter(s -> !"__consumer_offsets".equals(s)).collect(Collectors.toList());
    }

    @Override
    public Boolean createTopic(ISourceDTO sourceDTO, KafkaTopicDTO kafkaTopic) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        KafkaUtil.createTopicFromBroker(kafkaSourceDTO, kafkaTopic.getTopicName(), kafkaTopic.getPartitions(), kafkaTopic.getReplicationFactor());
        return true;
    }

    @Override
    public List getOffset(ISourceDTO sourceDTO, String topic) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        return KafkaUtil.getPartitionOffset(kafkaSourceDTO, topic);
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        return getPreview(sourceDTO, queryDTO, KafkaUtil.EARLIEST);
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO sourceDTO, SqlQueryDTO queryDTO, String prevMode) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        List<String> recordsFromKafka = KafkaUtil.getRecordsFromKafka(kafkaSourceDTO, queryDTO.getTableName(), prevMode);
        List<Object> records = new ArrayList<>(recordsFromKafka);
        List<List<Object>> result = new ArrayList<>();
        result.add(records);
        return result;
    }

    @Override
    public List<KafkaPartitionDTO> getTopicPartitions(ISourceDTO source, String topic) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) source;
        return KafkaUtil.getPartitions(kafkaSourceDTO, topic);
    }

    @Override
    public List<String> consumeData(ISourceDTO source, String topic, Integer collectNum, String offsetReset, Long timestampOffset, Integer maxTimeWait) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) source;
        return KafkaUtil.consumeData(kafkaSourceDTO, topic, collectNum, offsetReset, timestampOffset, maxTimeWait);
    }

    @Override
    public List<String> listConsumerGroup(ISourceDTO source) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) source;
        return KafkaUtil.listConsumerGroup(kafkaSourceDTO, null);
    }

    @Override
    public List<String> listConsumerGroupByTopic(ISourceDTO source, String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new SourceException("topic cannot be empty...");
        }
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) source;
        return KafkaUtil.listConsumerGroup(kafkaSourceDTO, topic);
    }

    @Override
    public List<KafkaConsumerDTO> getGroupInfoByGroupId(ISourceDTO source, String groupId) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) source;
        return KafkaUtil.getGroupInfoByGroupId(kafkaSourceDTO, groupId, null);
    }

    @Override
    public List<KafkaConsumerDTO> getGroupInfoByTopic(ISourceDTO source, String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new SourceException("topic cannot be empty...");
        }
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) source;
        return KafkaUtil.getGroupInfoByGroupId(kafkaSourceDTO, null, topic);
    }

    @Override
    public List<KafkaConsumerDTO> getGroupInfoByGroupIdAndTopic(ISourceDTO source, String groupId, String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new SourceException("topic cannot be empty...");
        }
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) source;
        return KafkaUtil.getGroupInfoByGroupId(kafkaSourceDTO, groupId, topic);
    }
}
