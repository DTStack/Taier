package com.dtstack.taier.develop.service.template.doris;

import com.dtstack.taier.develop.service.template.BaseWriterPlugin;
import com.dtstack.taier.develop.service.template.PluginName;

import java.util.List;

/**
 * <a href="https://github.com/DTStack/chunjun/blob/master/chunjun-examples/json/doris/mysql_doris.json">...</a>
 * @author leon
 * @date 2022-10-12 21:06
 **/
public class DorisWriter extends BaseWriterPlugin {

    private List<String> feNodes;

    private String password;

    private String username;

    private String database;

    private String table;

    private String fieldDelimiter;

    protected List column;


    @Override
    public String pluginName() {
        return PluginName.DORIS_RESTFUL_W;
    }

    public List<String> getFeNodes() {
        return feNodes;
    }

    public void setFeNodes(List<String> feNodes) {
        this.feNodes = feNodes;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }
}
