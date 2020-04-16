package com.dtstack.engine.api.domain.po;


/**
 * @author jiangbo
 * @time 2018/1/8
 */
public class TableCataloguePO {

    private Long id;

    private Long catalogueId;

    private String path;

    private Long tableId;

    private Long tenantId;

    private String tableName;

    private Integer level;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(Long catalogueId) {
        this.catalogueId = catalogueId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
