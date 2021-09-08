package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

@Data
public class BatchTableSyncLog extends TenantProjectEntity {

    /**
     * 数据源ID
     */
    private Long sourceId;

    /**
     * 原始表名
     */
    private String originalTableName;

    /**
     * 修改后表名
     */
    private String modifyTableName;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 任务失败描述
     */
    private String taskReport;

    /**
     * 创建人
     */
    private Long createUserId;

}
