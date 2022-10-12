package com.dtstack.taier.develop.service.template.clickhouse;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.dtstack.taier.develop.service.template.PluginName;

import java.util.List;

/**
 * <a href="https://github.com/DTStack/chunjun/blob/master/chunjun-examples/json/clickhouse/clickhouse.json">...</a>
 *
 * @author leon
 * @date 2022-10-11 22:47
 **/
public class ClickHouseReader extends BaseReaderPlugin {

    private List<ColumnDTO> column;

    private String increColumn;

    private String startLocation;

    private List<ConnectionDTO> connection;

    private String customSql;

    private String username;

    private String password;

    private String splitPk;

    private String where;

    private List<Long> sourceIds;

    @Override
    public String pluginName() {
        return PluginName.Clickhouse_R;
    }

    @Override
    public void checkFormat(JSONObject data) {}

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

    public String getCustomSql() {
        return customSql;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
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
