package com.dtstack.taier.develop.service.template.rdbms;

import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;

/**
 * @Author: gengxin
 * @Date: 2021/10/30 8:01 下午
 * 和前端对接的字段
 */
public class RDBReaderParam extends DaPluginParam {
    /**
     * 关系型数据库实时采集类型 1 binlog | 2 间隔轮询
     */
    private Integer rdbmsDaType;

    /**
     * 增量标识字段
     */
    private String increColumn;

    /**
     * 采集起点
     */
    private String startLocation;

    /**
     * schema
     */
    private String schema;

    /**
     * 表名
     */
    private List<String> tableName;

    /**
     * 轮询时间间隔
     */
    private Long pollingInterval;

    /**
     * 采集字段
     */
    private List<String> tableFields;
   // private List column;

    private String session;
    private String preSql;
    private String postSql;
    private String writeMode;
    protected List<Long> sourceIds;

    public Integer getRdbmsDaType() {
        return rdbmsDaType;
    }

    public void setRdbmsDaType(Integer rdbmsDaType) {
        this.rdbmsDaType = rdbmsDaType;
    }

    public String getIncreColumn() {
        return increColumn;
    }

    public void setIncreColumn(String increColumn) {
        this.increColumn = increColumn;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<String> getTableName() {
        return tableName;
    }

    public void setTableName(List<String> tableName) {
        this.tableName = tableName;
    }

    public Long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(Long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public List<String> getTableFields() {
        return tableFields;
    }

    public void setTableFields(List<String> tableFields) {
        this.tableFields = tableFields;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getPreSql() {
        return preSql;
    }

    public void setPreSql(String preSql) {
        this.preSql = preSql;
    }

    public String getPostSql() {
        return postSql;
    }

    public void setPostSql(String postSql) {
        this.postSql = postSql;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    @Override
    public List<Long> getSourceIds() {
        return sourceIds;
    }

    @Override
    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }
}
