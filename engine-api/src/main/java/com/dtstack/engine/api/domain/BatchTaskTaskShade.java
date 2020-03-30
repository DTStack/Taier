package com.dtstack.engine.api.domain;


/**
 * @author sishu.yss
 */
public class BatchTaskTaskShade extends AppTenantEntity {

    private Long taskId;

    private Long parentTaskId;


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
}
