package com.dtstack.batch.web.datasource.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("自定义参数")
public class BatchTaskParamVO {

    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "自定义参数类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "自定义参数名称", example = "xxxx", required = true)
    private String paramName;

    @ApiModelProperty(value = "自定义参数替换值", example = "xxxx", required = true)
    private String paramCommand;
}
