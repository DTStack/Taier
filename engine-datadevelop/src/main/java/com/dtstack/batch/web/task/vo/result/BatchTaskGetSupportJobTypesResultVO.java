package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("支持的引擎类型")
public class BatchTaskGetSupportJobTypesResultVO {

    @ApiModelProperty(value = "任务类型", example = "0")
    private Integer key;

    @ApiModelProperty(value = "任务描述", example = "SparkSQL")
    private String value;
}
