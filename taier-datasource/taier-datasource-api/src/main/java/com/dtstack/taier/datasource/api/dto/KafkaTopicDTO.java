package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * kafka topic
 *
 * @author ：nanqi
 * date：Created in 下午5:05 2022/9/23
 * company: www.dtstack.com
 */
@Data
@Builder
public class KafkaTopicDTO {
    /**
     * 名称
     */
    private String topicName;

    /**
     * 分区数
     */
    @Builder.Default
    private Integer partitions = 1;

    /**
     * 复制因子
     */
    @Builder.Default
    private Short replicationFactor = 1;
}
