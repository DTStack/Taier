package com.dtstack.engine.api.domain;


import com.dtstack.engine.api.annotation.Unique;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sishu.yss
 */
@ApiModel
public class ScheduleTaskTaskShade extends AppTenantEntity {

    @Unique
    private Long taskId;

    private Long parentTaskId;

    private Integer parentAppType;

    private String taskKey;

    private String parentTaskKey;

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

    public Integer getParentAppType() {
        return parentAppType;
    }

    public void setParentAppType(Integer parentAppType) {
        this.parentAppType = parentAppType;
    }

    public String getTaskKey() {
        if (StringUtils.isBlank(taskKey)) {
            taskKey = taskId + "-" + getAppType();
        }
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getParentTaskKey() {
        if (StringUtils.isBlank(parentTaskKey) && parentTaskId != null) {
            parentTaskKey = parentTaskId+ "-" + getParentAppType();
        }
        return parentTaskKey;
    }

    public void setParentTaskKey(String parentTaskKey) {
        this.parentTaskKey = parentTaskKey;
    }
}
