package com.dtstack.engine.api.domain;


import com.dtstack.engine.api.annotation.Unique;
import io.swagger.annotations.ApiModel;

/**
 * @author sishu.yss
 */
@ApiModel
public class ScheduleTaskTaskShade extends AppTenantEntity {

    @Unique
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
