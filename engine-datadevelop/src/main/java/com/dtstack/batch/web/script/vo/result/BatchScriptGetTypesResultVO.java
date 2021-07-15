package com.dtstack.batch.web.script.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("得到脚本类型")
public class BatchScriptGetTypesResultVO {

    @ApiModelProperty(value = "类型值", example = "2")
    private Integer value;

    @ApiModelProperty(value = "名称", example = "Impala")
    private String name;
}
