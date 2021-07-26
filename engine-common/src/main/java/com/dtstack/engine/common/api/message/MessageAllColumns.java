package com.dtstack.engine.common.api.message;

import java.io.Serializable;

/**
 * @Auther: dazhi
 * @Date: 2021/7/26 11:29 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class MessageAllColumns implements Serializable {

    private static final long serialVersionUID = 7344103223135690544L;

    private String tableName;

    private String schemaName;

    private String dbName;

    private String pluginInfo;

    private String engineType;

    public MessageAllColumns(String tableName, String schemaName, String dbName, String pluginInfo,String engineType) {
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.dbName = dbName;
        this.pluginInfo = pluginInfo;
        this.engineType = engineType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }
}
