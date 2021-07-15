package com.dtstack.batch.web.model.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("模型不规范原因分布返回结果信息")
public class BatchTableRateResultVO {

    @ApiModelProperty(value = "层级不规范数", example = "1")
    private Integer grade;

    @ApiModelProperty(value = "主题域不规范数", example = "1")
    private Integer subject;

    @ApiModelProperty(value = "刷新不规范数", example = "1")
    private Integer refreshRate;

    @ApiModelProperty(value = "增量方式不规范数", example = "1")
    private Integer increType;
}
