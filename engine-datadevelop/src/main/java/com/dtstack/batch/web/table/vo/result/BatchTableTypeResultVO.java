package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("项目支持表类型返回信息")
public class BatchTableTypeResultVO {

    @ApiModelProperty(value = "表值", example = "1")
    private Integer value;

    @ApiModelProperty(value = "表名称", example = "table")
    private String name;
}
