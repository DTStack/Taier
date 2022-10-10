package com.dtstack.taier.datasource.api.dto.yarn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * yarn 资源
 *
 * @author ：wangchuan
 * date：Created in 下午5:11 2022/3/17
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YarnResourceDescriptionDTO {

    private int totalNode;
    private int totalMemory;
    private int totalCores;
    private List<QueueDescription> queueDescriptions;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueDescription {
        private String queueName;
        private String queuePath;
        private String capacity;
        private String maximumCapacity;
        private String queueState;
        private List<QueueDescription> childQueues;
    }
}
