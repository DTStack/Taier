package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据源任务结果信息")
public class BatchDataSourceTaskRefResultVO {
    @ApiModelProperty(value = "任务id", example = "1")
    private Long id;

    @ApiModelProperty(value = "任务名称", example = "task_name")
    private String name;
}