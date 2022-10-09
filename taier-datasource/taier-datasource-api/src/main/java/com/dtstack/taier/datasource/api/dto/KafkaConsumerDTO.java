package com.dtstack.taier.datasource.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * kafka 消费者组相关参数
 *
 * @author ：wangchuan
 * date：Created in 下午4:26 2021/4/13
 * company: www.dtstack.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConsumerDTO {

    // 消费者组
    private String groupId;

    // 当前分区
    private Integer partition;

    // 当前消费 offset
    private Long currentOffset;

    // 属于 topic
    private String topic;

    // broker host
    private String brokerHost;

    // 未消费数据
    private Long lag;

    // 当前分区 leader 最后一次提交的offset 也就是当前分区的最大偏移量
    private Long logEndOffset;
}
