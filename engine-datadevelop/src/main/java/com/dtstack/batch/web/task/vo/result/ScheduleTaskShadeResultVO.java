package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class ScheduleTaskShadeResultVO {

    @ApiModelProperty(value = "id")
    private Long id = 0L;

    @ApiModelProperty(value = "项目 ID")
    private Long projectId;

    @ApiModelProperty(value = "taskId")
    private Long taskId;

    @ApiModelProperty(value = "产品类型")
    private Integer appType;

    @ApiModelProperty(value = "任务名称", example = "spark_test")
    private String name;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr，2 sync ，3 python", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "0")
    private Integer engineType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1")
    private Integer computeType;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "租户名称", example = "dtstack")
    private String tenantName;

    @ApiModelProperty(value = "项目别名", example = "dev")
    private String projectAlias;

}
