package com.dtstack.taier.develop.vo.develop.query;


import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


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


    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Object getTableName() {
        return tableName;
    }

    public void setTableName(Object tableName) {
        this.tableName = tableName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
