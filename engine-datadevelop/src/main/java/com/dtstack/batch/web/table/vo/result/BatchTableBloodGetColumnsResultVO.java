package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel("表血缘信息")
public class BatchTableBloodGetColumnsResultVO {

    @ApiModelProperty(value = "数据源 ID", example = "3")
    private Long dataSourceId;

    @ApiModelProperty(value = "所属项目 ID", example = "24")
    private Long belongProjectId;

    @ApiModelProperty(value = "表名", example = "user")
    private String tableName;

    @ApiModelProperty(value = "字段信息")
    private Set<String> columns;

}
