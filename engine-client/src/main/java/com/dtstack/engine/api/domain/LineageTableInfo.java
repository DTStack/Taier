package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageTableInfo
 * @Description TODO
 * @Date 2020/10/22 20:14
 * @Created chener@dtstack.com
 */
@ApiModel
public class LineageTableInfo extends TenantEntity {

    @ApiModelProperty(notes = "应用类型")
    private Integer appType;

    @ApiModelProperty(notes = "数据源id")
    private Integer sourceId;

    @ApiModelProperty(notes = "真实数据源id")
    private Integer realSourceId;

    @ApiModelProperty(notes = "数据源名称")
    private String sourceName;

    @ApiModelProperty(notes = "数据源类型")
    private Integer sourceType;

    @ApiModelProperty(notes = "数据源定位码")
    private String sourceKey;

    @ApiModelProperty(notes = "数据库名称")
    private String dbName;

    @ApiModelProperty(notes = "表名称")
    private String tableName;

    @ApiModelProperty(notes = "表定位码")
    private String tableKey;

    @ApiModelProperty(notes = "是否手动维护")
    private Integer isManual;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getRealSourceId() {
        return realSourceId;
    }

    public void setRealSourceId(Integer realSourceId) {
        this.realSourceId = realSourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
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

    public String getTableKey() {
        return tableKey;
    }

    public void setTableKey(String tableKey) {
        this.tableKey = tableKey;
    }

    public Integer getIsManual() {
        return isManual;
    }

    public void setIsManual(Integer isManual) {
        this.isManual = isManual;
    }
}
