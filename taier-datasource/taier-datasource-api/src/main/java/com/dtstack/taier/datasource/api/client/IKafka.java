package com.dtstack.taier.datasource.api.client;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.dto.KafkaConsumerDTO;
import com.dtstack.taier.datasource.api.dto.KafkaOffsetDTO;
import com.dtstack.taier.datasource.api.dto.KafkaPartitionDTO;
import com.dtstack.taier.datasource.api.dto.KafkaTopicDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

import java.util.List;

/**
 * kafka
 *
 * @author ：wangchuan
 * date：Created in 下午3:38 2021/12/17
 * company: www.dtstack.com
 */
public interface IKafka extends Client {

    /**
     * 校验 连接
     *
     * @param source 数据源信息
     * @return 是否连接成功
     */
    Boolean testCon(ISourceDTO source);

    /**
     * 获取所有 Brokers 的地址
     *
     * @param source 数据源信息
     * @return Brokers地址
     */
    String getAllBrokersAddress(ISourceDTO source);

    /**
     * 获取 所有 Topic 信息
     *
     * @param source 数据源信息
     * @return Topic
     */
    List<String> getTopicList(ISourceDTO source);

    /**
     * 创建 Topic
     *
     * @param source     数据源信息
     * @param kafkaTopic kafka topic
     * @return 是否创建topic成功
     */
    Boolean createTopic(ISourceDTO source, KafkaTopicDTO kafkaTopic);

    /**
     * 获取特定 Topic 所有分区的偏移量
     *
     * @param source 数据源信息
     * @param topic  topic
     * @return 分区的偏移量
     */
    List<KafkaOffsetDTO> getOffset(ISourceDTO source, String topic);

    /**
     * 获取预览数据
     *
     * @param source   数据源信息
     * @param queryDTO 查询条件
     * @deprecated since 1.4.0 in favor of
     */
    @Deprecated
    List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取预览数据
     *
     * @param source   数据源信息
     * @param queryDTO 查询条件
     * @param prevMode 预览模式
     * @return 预览数据
     */
    List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO, String prevMode);

    /**
     * 获取kafka指定topic下的分区信息
     *
     * @param source 数据源信息
     * @param topic  topic名称
     * @return 分区数量
     */
    List<KafkaPartitionDTO> getTopicPartitions(ISourceDTO source, String topic);

    /**
     * 从kafka 中消费数据
     *
     * @param source          数据源信息
     * @param topic           topic
     * @param collectNum      最大条数
     * @param offsetReset     从哪里开始消费
     * @param timestampOffset 消费启始位置
     * @param maxTimeWait     最大等待时间，单位秒
     * @return kafka数据
     */
    List<String> consumeData(ISourceDTO source, String topic, Integer collectNum, String offsetReset, Long timestampOffset, Integer maxTimeWait);

    /**
     * 获取所有的消费者组
     *
     * @param source 数据源信息
     * @return 消费者组列表
     */
    List<String> listConsumerGroup(ISourceDTO source);

    /**
     * 获取指定topic下的所有的消费者组
     *
     * @param source 数据源信息
     * @return 消费者组列表
     */
    List<String> listConsumerGroupByTopic(ISourceDTO source, String topic);

    /**
     * 获取 kafka 消费者组详细信息
     *
     * @param source  数据源信息
     * @param groupId 消费者组
     * @return 消费者组详细信息
     */
    List<KafkaConsumerDTO> getGroupInfoByGroupId(ISourceDTO source, String groupId);

    /**
     * 获取 kafka 指定topic 下消费者组详细信息
     *
     * @param source 数据源信息
     * @param topic  kafka主题
     * @return 消费者组详细信息
     */
    List<KafkaConsumerDTO> getGroupInfoByTopic(ISourceDTO source, String topic);

    /**
     * 获取 kafka 指定topic下指定消费者组详细信息
     *
     * @param source  数据源信息
     * @param groupId 消费者组
     * @param topic   kafka主题
     * @return 消费者组详细信息
     */
    List<KafkaConsumerDTO> getGroupInfoByGroupIdAndTopic(ISourceDTO source, String groupId, String topic);
}
