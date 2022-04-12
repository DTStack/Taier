package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("数据源列表查询参数")
public class DsTableListBySchemaParam extends PubSvcBaseParam {
    @ApiModelProperty(value = "数据源Id")
    private Long sourceId;

    @ApiModelProperty(value = "schema")
    private String schema;

    @ApiModelProperty(value = "模糊查询tableName")
    private String searchKey;

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }


}