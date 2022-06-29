package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：zhaiyue
 * @date ：2022/06/29 23:15
 * @description：
 */
@Data
public class DevelopTenantComponentResultVO {

    @ApiModelProperty(value = "任务类型", example = "0")
    private Integer taskType;

    @ApiModelProperty(value = "任务类型名称", example = "SparkSQl")
    private String taskTypeName;

    @ApiModelProperty(value = "任务使用的schema", example = "dev")
    private String schema;

}
