package com.dtstack.engine.api.vo.lineage.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname QueryTableLineageParam
 * @Description 查询表血缘关系参数
 * @Date 2020/11/3 10:41
 * @Created chener@dtstack.com
 */
@ApiModel("查询表血缘关系参数")
public class QueryTableLineageParam {
    @ApiModelProperty("应用类型")
    private Integer appType;

    @ApiModelProperty("数据源类型")
    private Integer sourceType;

    @ApiModelProperty(value = "engine数据源id",notes = "数据资产必须传，离线不传")
    private Long engineSourceId;

    @ApiModelProperty("uic租户id")
    private Long dtUicTenantId;

    @ApiModelProperty("schema名称")
    private String schemaName;

    @ApiModelProperty("db名称")
    private String dbName;

    @ApiModelProperty("表名称")
    private String tableName;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Long getEngineSourceId() {
        return engineSourceId;
    }

    public void setEngineSourceId(Long engineSourceId) {
        this.engineSourceId = engineSourceId;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
