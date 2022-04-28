package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhiChen
 * @date 2021/1/11 20:47
 */
public class GetCheckpointListVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true, value = "项目ID", example = "111", required = true)
    private Long projectId;

    @NotNull(message = "taskId not null")
    @ApiModelProperty(value = "任务ID", example = "111", required = true)
    private String jobId;

    @NotNull(message = "startTime not null")
    @ApiModelProperty(value = "开始时间", example = "111", required = true)
    private Long startTime;

    @NotNull(message = "endTime not null")
    @ApiModelProperty(value = "结束时间", example = "111", required = true)
    private Long endTime;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
