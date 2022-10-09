package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * kafka分区信息传输类
 *
 * @author ：wangchuan
 * date：Created in 3:36 下午 2021/1/10
 * company: www.dtstack.com
 */
@Data
@Builder
public class KafkaPartitionDTO {

    // topic名称
    private final String topic;

    // 分区编号
    private final Integer partition;

    // 副本中的leader
    private final Node leader;

    // 所有的副本
    private final Node[] replicas;

    // isr队列中的副本
    private final Node[] inSyncReplicas;

    @Data
    @Builder
    public static class Node {

        // 副本编号
        private final int id;

        // 副本 string 类型编号
        private final String idString;

        // host
        private final String host;

        // 端口号
        private final int port;

        // 机架名称
        private final String rack;
    }

}