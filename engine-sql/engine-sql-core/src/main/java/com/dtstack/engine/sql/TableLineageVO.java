package com.dtstack.engine.sql;

import java.util.List;

/**
 * @author chener
 * @Classname TableLineageVO
 * @Description 表血缘关系对象
 * @Date 2020/10/15 11:47
 * @Created chener@dtstack.com
 */
public class TableLineageVO {
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

    /**
     * 从最下游开始的血缘信息
     */
    private TableLineageItem tableLineageItem;

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

    public TableLineageItem getTableLineageItem() {
        return tableLineageItem;
    }

    public void setTableLineageItem(TableLineageItem tableLineageItem) {
        this.tableLineageItem = tableLineageItem;
    }

    public static class TableLineageItem{
        /**
         * 数据库名
         */
        private String db;
        /**
         * 表名
         */
        private String table;

        /**
         * 上游表血缘
         */
        private List<TableLineageItem> upstreamTableLineageItems;

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

        public List<TableLineageItem> getUpstreamTableLineageItems() {
            return upstreamTableLineageItems;
        }

        public void setUpstreamTableLineageItems(List<TableLineageItem> upstreamTableLineageItems) {
            this.upstreamTableLineageItems = upstreamTableLineageItems;
        }
    }
}
