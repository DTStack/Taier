package com.dtstack.taier.develop.vo.develop.query;


import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author qianyi
 * @version 1.0
 * @date 2021/1/3 6:47 下午
 */
public class TaskIdQueryVO extends DtInsightAuthParam {


    @ApiModelProperty(value = "任务ID", example = "1", required = true)
    private Long taskId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

}
