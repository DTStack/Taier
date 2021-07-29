package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务名对应的被其他项目依赖的任务")
public class BatchProjectDelCheckParentResultVO {

    @ApiModelProperty(value = "项目名", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "租户名", example = "dtstack")
    private String tenantName;

    @ApiModelProperty(value = "任务名", example = "task")
    private String taskName;

    @ApiModelProperty(value = "产品类型", example = "dev")
    private Integer appType;
}
