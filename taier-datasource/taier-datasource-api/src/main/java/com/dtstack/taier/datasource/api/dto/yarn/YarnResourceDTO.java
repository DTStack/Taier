package com.dtstack.taier.datasource.api.dto.yarn;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * yarn 资源 DTO
 *
 * @author ：wangchuan
 * date：Created in 上午9:37 2022/3/17
 * company: www.dtstack.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YarnResourceDTO {

    private YarnResourceDTO.ResourceMetrics resourceMetrics;
    private List<YarnResourceDTO.NodeDescription> nodes = new ArrayList<>();
    private List<JSONObject> queues;
    private JSONObject scheduleInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NodeDescription {

        private String nodeName;
        private int memory;
        private int virtualCores;
        private int usedMemory;
        private int usedVirtualCores;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResourceMetrics {

        private Double totalMem;
        private Integer totalCores;
        private Double usedMem;
        private Integer usedCores;
        private Double memRate;
        private Double coresRate;
    }
}
