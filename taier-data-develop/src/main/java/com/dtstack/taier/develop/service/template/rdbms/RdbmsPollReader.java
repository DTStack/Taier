package com.dtstack.taier.develop.service.template.rdbms;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;

import java.util.List;

public abstract class RdbmsPollReader extends BaseReaderPlugin {

    /**
     * 字段dtoList
     */
    private List<ColumnDTO> column;

    /**
     * 增量标识字段
     */
    private String increColumn;

    /**
     * 采集起点
     */
    private String startLocation;

    /**
     * 连接信息
     */
    private List<ConnectionDTO> connection;

    /**
     * 是否是间隔轮询 离线任务只有增量同步场景下是true
     */
    private Boolean polling = true;

    /**
     * 轮询间隔时间
     */
    private Long pollingInterval;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * splitPk代表的字段进行数据分片
     */
    private String splitPk;
    /**
     * where条件
     */
    private String where;
    private List<Long> sourceIds;

    @Override
    public abstract String pluginName();

    @Override
    public void checkFormat(JSONObject jsonObject) {

    }

    public List<ColumnDTO> getColumn() {
        return column;
    }

    public void setColumn(List<ColumnDTO> column) {
        this.column = column;
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

    public List<ConnectionDTO> getConnection() {
        return connection;
    }

    public void setConnection(List<ConnectionDTO> connection) {
        this.connection = connection;
    }

    public Boolean getPolling() {
        return polling;
    }

    public void setPolling(Boolean polling) {
        this.polling = polling;
    }

    public Long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(Long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSplitPk() {
        return splitPk;
    }

    public void setSplitPk(String splitPk) {
        this.splitPk = splitPk;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public List<Long> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }
}
