package com.dtstack.engine.api.vo.lineage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageTableInfoVO
 * @Description 表信息
 * @Date 2020/10/30 10:25
 * @Created chener@dtstack.com
 */
@ApiModel("表信息")
public class LineageTableVO {

    @ApiModelProperty("表id")
    private Long tableId;

    @ApiModelProperty("表名")
    private String tableName;

    @ApiModelProperty("schema名称")
    private String schemaName;

    @ApiModelProperty("数据库名")
    private String dbName;

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

    /**
     * 数据源信息
     */
    @ApiModelProperty("数据源信息")
    private LineageDataSourceVO dataSourceVO;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LineageDataSourceVO getDataSourceVO() {
        return dataSourceVO;
    }

    public void setDataSourceVO(LineageDataSourceVO dataSourceVO) {
        this.dataSourceVO = dataSourceVO;
    }
}
