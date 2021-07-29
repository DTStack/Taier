package com.dtstack.batch.dto;

import lombok.Data;

/**
 * 用于查看 圈定的表
 */
@Data
public class BatchWaitMergeTableDTO {

    private Long projectId;

    private Long tenantId;

    private Long fileCount;

    private Long storage;

    private String tableName;
}
