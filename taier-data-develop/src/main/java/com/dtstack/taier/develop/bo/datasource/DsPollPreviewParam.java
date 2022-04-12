package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("数据预览参数")
public class DsPollPreviewParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "数据源Id")
    private Long sourceId;

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "schema")
    private String schema;

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }


}