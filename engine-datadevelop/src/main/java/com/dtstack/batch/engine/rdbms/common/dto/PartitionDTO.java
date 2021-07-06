package com.dtstack.batch.engine.rdbms.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/7/20.
 */
@Data
@Builder
public class PartitionDTO {

    private String name;

    private Long lastDDLTime;

    private String storeSize;

    private Long partId;

    /**
     * 分区下文件数量
     */
    private Long fileCount;
}
