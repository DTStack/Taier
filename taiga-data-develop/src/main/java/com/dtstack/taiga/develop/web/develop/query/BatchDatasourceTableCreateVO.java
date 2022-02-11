package com.dtstack.taiga.develop.web.develop.query;

import com.dtstack.taiga.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("执行建表SQL实体信息")
public class BatchDatasourceTableCreateVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "建表sql", example = "create table", required = true)
    private String sql;

    @ApiModelProperty(value = "数据源 ID",  example = "11", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "目标数据源 schema 信息", example = "schema")
    private String targetSchema;

}
