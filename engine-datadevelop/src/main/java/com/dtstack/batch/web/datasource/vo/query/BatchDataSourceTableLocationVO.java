package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据源表存储信息")
public class BatchDataSourceTableLocationVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "表名称", example = "table_name", required = true)
    private String tableName;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;
}
