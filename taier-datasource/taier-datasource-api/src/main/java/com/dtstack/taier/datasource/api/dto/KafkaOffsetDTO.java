package com.dtstack.taier.datasource.api.dto;

import lombok.Data;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:22 2020/2/26
 * @Description：Kafka 偏移量
 */
@Data
public class KafkaOffsetDTO {
    /**
     * 分区编号
     */
    private Integer partition;

    /**
     * 最新的偏移量
     */
    private Long lastOffset;

    /**
     * 最早的偏移量
     */
    private Long firstOffset;
}
