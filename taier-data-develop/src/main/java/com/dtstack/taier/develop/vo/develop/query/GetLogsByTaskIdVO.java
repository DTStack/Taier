package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

public class GetLogsByTaskIdVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务ID", example = "111", required = true)
    private Long taskId;

    @ApiModelProperty(value = "任务管理ID", example = "111")
    private String taskManagerId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }


    public String getTaskManagerId() {
        return taskManagerId;
    }

    public void setTaskManagerId(String taskManagerId) {
        this.taskManagerId = taskManagerId;
    }
}
