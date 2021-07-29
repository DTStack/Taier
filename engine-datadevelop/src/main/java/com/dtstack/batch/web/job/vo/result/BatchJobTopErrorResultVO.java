package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("近30天任务出错排行")
public class BatchJobTopErrorResultVO {

    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "错误数量", example = "2")
    private Integer errorCount = 0;

    @ApiModelProperty(value = "任务名称", example = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "创建人", example = "admin")
    private String createUser;
}
