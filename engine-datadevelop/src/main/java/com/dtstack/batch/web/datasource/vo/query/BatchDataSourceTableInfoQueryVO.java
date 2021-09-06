package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据同步-数据源 表信息查询")
public class BatchDataSourceTableInfoQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "数据源ID", example = "1", required = true)
    private Long dataSourceId;

    @ApiModelProperty(value = "schema", example = "dev")
    private String schema;

    @ApiModelProperty(value = "表名", example = "abc", required = true)
    private String tableName;

}
