package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("删除任务前置判断")
public class BatchGetChildTasksResultVO {

    @ApiModelProperty(value = "产品类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "任务名称", example = "task")
    private String taskName;

    @ApiModelProperty(value = "租户名称", example = "dtstack租户")
    private String tenantName;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "项目别名", example = "dev")
    private String projectAlias;
}
