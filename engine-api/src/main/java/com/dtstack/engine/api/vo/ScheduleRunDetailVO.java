package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/7/5
 */
@ApiModel
public class ScheduleRunDetailVO implements Serializable {

    private String taskName;

    private String startTime;

    private String endTime;

    private Long execTime;

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Long getExecTime() {
        return execTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
