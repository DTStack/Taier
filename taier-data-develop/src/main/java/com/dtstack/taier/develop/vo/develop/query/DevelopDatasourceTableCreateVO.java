package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("执行建表SQL实体信息")
public class DevelopDatasourceTableCreateVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "建表sql", example = "create table", required = true)
    private String sql;

    @ApiModelProperty(value = "数据源 ID", example = "11", required = true)
    private Long sourceId;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

}
