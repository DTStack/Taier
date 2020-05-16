package com.dtstack.engine.common.akka.message;

import java.io.Serializable;

/**
 * @author yuebai
 * @date 2020-05-15
 */
public class MessageExecuteQuery implements Serializable {
    private String engineType;
    private String pluginInfo;
    private String sql;
    private String database;

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public MessageExecuteQuery(String engineType, String pluginInfo, String sql, String database) {
        this.engineType = engineType;
        this.pluginInfo = pluginInfo;
        this.sql = sql;
        this.database = database;
    }
}
