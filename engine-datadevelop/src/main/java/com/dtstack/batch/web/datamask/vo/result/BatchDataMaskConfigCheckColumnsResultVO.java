package com.dtstack.batch.web.datamask.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏检查上游表字段结果信息")
public class BatchDataMaskConfigCheckColumnsResultVO {
    @ApiModelProperty(value = "表名称", example = "test")
    private String tableName;

    @ApiModelProperty(value = "表字段名称", example = "col_name")
    private String columnName;
}
