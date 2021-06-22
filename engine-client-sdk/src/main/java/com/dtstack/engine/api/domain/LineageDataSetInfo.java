package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageTableInfo
 * @Description 数据集，通常为表
 * @Date 2020/10/22 20:14
 * @Created chener@dtstack.com
 */
@ApiModel
public class LineageDataSetInfo extends DtUicTenantEntity {

    @ApiModelProperty(notes = "应用类型")
    private Integer appType;

    @ApiModelProperty(notes = "数据源中心id")
    private Long dataInfoId;


    @ApiModelProperty(notes = "数据源名称")
    private String sourceName;

    @ApiModelProperty(notes = "数据源类型")
    private Integer sourceType;


    @ApiModelProperty(notes = "数据集类型 0 表，1 文件")
    private Integer setType;

    @ApiModelProperty(notes = "一般数据集类型为表，该字段为数据库名称;当数据集类型为文件时，该字段可以取文件名，或者其他定义")
    private String dbName;

    @ApiModelProperty(notes = "一般情况下，schema=db，SQLserver不同。SQLserver表结构为db.schema.table")
    private String schemaName;

    @ApiModelProperty(notes = "一般数据集类型为表，该字段为表名称；当数据集类型为文件时，该字段可以由文件描述的数据集模型名定义")
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

    public Long getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Long dataInfoId) {
        this.dataInfoId = dataInfoId;
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


    public Integer getSetType() {
        return setType;
    }

    public void setSetType(Integer setType) {
        this.setType = setType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
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

    @Override
    public String toString() {
        return "LineageDataSetInfo{" +
                "appType=" + appType +
                ", dataInfoId=" + dataInfoId +
                ", sourceName='" + sourceName + '\'' +
                ", sourceType=" + sourceType +
                ", setType=" + setType +
                ", dbName='" + dbName + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableKey='" + tableKey + '\'' +
                ", isManual=" + isManual +
                '}';
    }
}
