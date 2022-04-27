package com.dtstack.taier.develop.vo.develop.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;


public class JobHistoryVO {

    @ApiModelProperty(value = "yarn 上任务对应的 applicationID", example = "application_000000000000_0001")
    private String applicationId;

    @ApiModelProperty(value = "任务开始时间", example = "2021-09-23T02:35:24.000+0000")
    private Timestamp execStartTime;

    @ApiModelProperty(value = "任务结束时间", example = "2021-09-23T02:35:24.000+0000")
    private Timestamp execEndTime;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Timestamp getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Timestamp execEndTime) {
        this.execEndTime = execEndTime;
    }
}
