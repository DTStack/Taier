package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("运行同步任务返回信息")
public class BatchStartSyncResultVO {

    @ApiModelProperty(value = "信息", example = "1")
    private String msg;

    @ApiModelProperty(value = "任务实例ID", example = "1")
    private String jobId;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

}
