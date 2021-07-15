package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据同步-表存储信息")
public class BatchDataSourceIncreColumnVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "表名称", required = true)
    private Object tableName;

    @ApiModelProperty(value = "查询的schema", example = "test")
    private String schema;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;
}
