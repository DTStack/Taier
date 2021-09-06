package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据源表字段信息")
public class BatchDataSourceTableColumnVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "表名称", example = "table_name", required = true)
    private String tableName;

    @ApiModelProperty(value = "是否包含分区", example = "false", required = true)
    private Boolean isIncludePart;

    @ApiModelProperty(value = "查询的schema", example = "test", required = true)
    private String schema;
}
