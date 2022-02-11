package com.dtstack.taiga.develop.web.develop.query;

import com.dtstack.taiga.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("生成建表SQL实体信息")
public class BatchDatasourceTableCreateSQLVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "表名", example = "dev", required = true)
    private String tableName;

    @ApiModelProperty(value = "原数据源 ID",  example = "3", required = true)
    private Long originSourceId;

    @ApiModelProperty(value = "目标数据源 ID",  example = "11", required = true)
    private Long targetSourceId;

    @ApiModelProperty(value = "分区",  example = "/dev", required = true)
    private String partition;

    @ApiModelProperty(value = "原数据源 schema 信息", example = "schema")
    private String originSchema;

    @ApiModelProperty(value = "目标数据源 schema 信息", example = "schema")
    private String targetSchema;

}
