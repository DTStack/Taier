package com.dtstack.schedule.common.jdbc;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * <p>
 *     构建基本query对象，其余熟悉用时自行添加
 * </p>
 */
public class JdbcQuery {

    private Connection connection;

    private String database;

    private Long tenantId;

    private String sql;

    private int maxRows;

    private int queryTimeout;

    private boolean multiplex;

    public JdbcQuery() {
    }


    public JdbcQuery(Connection connection, String database, Long tenantId, String sql, boolean multiplex) {
        this.connection = connection;
        this.database = database;
        this.tenantId = tenantId;
        this.sql = sql;
        this.multiplex = multiplex;
    }

    public JdbcQuery(Connection connection, String database, Long tenantId, String sql) {
        this.connection = connection;
        this.database = database;
        this.tenantId = tenantId;
        this.sql = sql;
    }

    public JdbcQuery done(){
        // 以下四个参数为必填项,在构建最终hq对象时最好调用该方法
        assert connection != null;
        assert StringUtils.isNotEmpty(database);
        assert tenantId > 0;
        assert StringUtils.isNotEmpty(sql);
        return this;
    }

    public boolean getMultiplex() {
        return multiplex;
    }

    public void setMultiplex(boolean multiplex) {
        this.multiplex = multiplex;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public JdbcQuery multiplex(boolean multiplex){
        this.maxRows = maxRows;
        return this;
    }

    public JdbcQuery maxRows(int maxRows){
        this.maxRows = maxRows;
        return this;
    }

    public JdbcQuery connection(Connection connection){
        this.connection = connection;
        return this;
    }

    public JdbcQuery database(String database){
        this.database = database;
        return this;
    }

    public JdbcQuery sql(String sql){
        this.sql = sql;
        return this;
    }

    public JdbcQuery tenant(Long tenantId){
        this.tenantId = tenantId;
        return this;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getDatabase() {
        return database;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getSql() {
        return sql;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
}
