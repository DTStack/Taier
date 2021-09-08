package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;

/**
 * @author: toutian
 */
@ApiModel
public class BatchDataSourceTaskRef extends TenantProjectEntity {

    private Long dataSourceId;

    private Long taskId;


    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
