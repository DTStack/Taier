package com.dtstack.taier.develop.service.template.rdbms;

import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;

public class RdbmsPollReaderParam extends DaPluginParam {

    /**
     * 关系型数据库实时采集类型 1 binlog | 2 间隔轮询
     */
    private Integer rdbmsDaType;

    /**离线同步方式（0：全量同步；1：增量同步）
     * @see SyncModel
     */
    private Integer syncModel;

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
    private String tableName;

    /**
     * 轮询时间间隔
     */
    private Long pollingInterval;

    /**
     * 采集字段
     */
    private List<String> tableFields;

    private  List<Object> sourceList;
    /**
     * splitPk代表的字段进行数据分片
     */
    private String splitPK;
    /**
     * where条件
     */
    private String where;
    /**
     * 列名
     */
    private List column;

    /**
     * 连接信息
     */
    private List<ConnectionDTO> connection;

    private Object table;

    public Integer getRdbmsDaType() {
        return rdbmsDaType;
    }

    public void setRdbmsDaType(Integer rdbmsDaType) {
        this.rdbmsDaType = rdbmsDaType;
    }

    public Integer getSyncModel() {
        return syncModel;
    }

    public void setSyncModel(Integer syncModel) {
        this.syncModel = syncModel;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
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

    public List<Object> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<Object> sourceList) {
        this.sourceList = sourceList;
    }

    public String getSplitPK() {
        return splitPK;
    }

    public void setSplitPK(String splitPK) {
        this.splitPK = splitPK;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

    public List<ConnectionDTO> getConnection() {
        return connection;
    }

    public void setConnection(List<ConnectionDTO> connection) {
        this.connection = connection;
    }

    public Object getTable() {
        return table;
    }

    public void setTable(Object table) {
        this.table = table;
    }
}

