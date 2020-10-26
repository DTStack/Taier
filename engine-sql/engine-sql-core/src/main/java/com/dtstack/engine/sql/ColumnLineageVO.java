package com.dtstack.engine.sql;

import java.util.List;

/**
 * @author chener
 * @Classname ColumnLineageVO
 * @Description 字段级血缘解析查询结果
 * @Date 2020/10/15 13:55
 * @Created chener@dtstack.com
 */
public class ColumnLineageVO {
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 内部数据源id
     */
    private Long sourceId;
    /**
     * 是否内部数据源
     */
    private Boolean innerSource;
    /**
     * 外部数据源id
     */
    private Long outerSourceId;

    private ColumnLineageItem columnLineageItem;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Boolean getInnerSource() {
        return innerSource;
    }

    public void setInnerSource(Boolean innerSource) {
        this.innerSource = innerSource;
    }

    public Long getOuterSourceId() {
        return outerSourceId;
    }

    public void setOuterSourceId(Long outerSourceId) {
        this.outerSourceId = outerSourceId;
    }

    public ColumnLineageItem getColumnLineageItem() {
        return columnLineageItem;
    }

    public void setColumnLineageItem(ColumnLineageItem columnLineageItem) {
        this.columnLineageItem = columnLineageItem;
    }

    private static class ColumnLineageItem{
        private String db;
        private String table;
        private String column;

        private List<ColumnLineageItem> upstreamColumnLineageItems;

        public String getDb() {
            return db;
        }

        public void setDb(String db) {
            this.db = db;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public List<ColumnLineageItem> getUpstreamColumnLineageItems() {
            return upstreamColumnLineageItems;
        }

        public void setUpstreamColumnLineageItems(List<ColumnLineageItem> upstreamColumnLineageItems) {
            this.upstreamColumnLineageItems = upstreamColumnLineageItems;
        }
    }
}
