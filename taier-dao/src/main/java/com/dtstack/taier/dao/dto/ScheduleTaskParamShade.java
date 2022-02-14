package com.dtstack.taier.dao.dto;

import com.dtstack.taier.dao.domain.BaseEntity;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class ScheduleTaskParamShade extends BaseEntity {

    private Long taskId;

    private Integer type;

    private String paramName;

    private String paramCommand;


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamCommand() {
        return paramCommand;
    }

    public void setParamCommand(String paramCommand) {
        this.paramCommand = paramCommand;
    }
}
