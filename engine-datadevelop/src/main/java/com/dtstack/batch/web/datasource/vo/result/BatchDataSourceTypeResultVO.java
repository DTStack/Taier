package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据源类型结果信息")
public class BatchDataSourceTypeResultVO {
    @ApiModelProperty(value = "数据源类型", example = "hive")
    private String name;

    @ApiModelProperty(value = "数据源类型", example = "1")
    private Integer value;

    @ApiModelProperty(value = "排序标识", example = "1")
    private Integer order;
}