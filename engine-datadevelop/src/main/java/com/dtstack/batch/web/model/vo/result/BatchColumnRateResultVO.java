package com.dtstack.batch.web.model.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("字段不规范原因分布返回信息")
public class BatchColumnRateResultVO {

    @ApiModelProperty(value = "字段名不规范数", example = "1")
    private Integer name;

    @ApiModelProperty(value = "字段数据类型不规范数", example = "1")
    private Integer dataType;

    @ApiModelProperty(value = "字段描述不规范数", example = "1")
    private Integer desc;
}
