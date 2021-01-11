package com.dtstack.engine.lineage.asserts;

/**
 * @author chener
 * @Classname AssetLineageColumnDTO
 * @Description TODO
 * @Date 2020/12/1 15:10
 * @Created chener@dtstack.com
 */
public class AssetLineageColumnDTO {

    private Long id;

    private Integer isManual;

    private Long lineageTableId;

    private String columnName;

    private String tableName;

    private String dbName;

    private String dataSourceName;

    private Long tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsManual() {
        return isManual;
    }

    public void setIsManual(Integer isManual) {
        this.isManual = isManual;
    }

    public Long getLineageTableId() {
        return lineageTableId;
    }

    public void setLineageTableId(Long lineageTableId) {
        this.lineageTableId = lineageTableId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
