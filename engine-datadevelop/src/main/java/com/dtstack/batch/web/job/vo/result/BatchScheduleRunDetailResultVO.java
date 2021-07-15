package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务详情返回信息")
public class BatchScheduleRunDetailResultVO {

    @ApiModelProperty(value = "任务名称", example = "task_name")
    private String taskName;

    @ApiModelProperty(value = "允许通知的开始时间", example = "5:00")
    private String startTime;

    @ApiModelProperty(value = "允许通知的结束时间", example = "22:00")
    private String endTime;

    @ApiModelProperty(value = "exec时间", example = "1525942614000")
    private Long execTime;
}
