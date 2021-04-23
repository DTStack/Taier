package com.dtstack.engine.api.pojo;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2020/11/23 11:15 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ParamTaskAction {

    private ScheduleTaskShade batchTask;

    @ApiModelProperty(notes = "任务id")
    private String jobId;

    @ApiModelProperty(notes = "是否重试: NORMAL(0), RESTARTED(1) 默认 NORMAL")
    private Integer isRestart;

    @ApiModelProperty(notes = "工作流Id")
    private String flowJobId;

    public ScheduleTaskShade getBatchTask() {
        return batchTask;
    }

    public void setBatchTask(ScheduleTaskShade batchTask) {
        this.batchTask = batchTask;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getIsRestart() {
        return isRestart;
    }

    public void setIsRestart(Integer isRestart) {
        this.isRestart = isRestart;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }
}
