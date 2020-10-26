package com.dtstack.engine.sql;

/**
 * @author chener
 * @Classname BaseParseResult
 * @Description 解析结果基类
 * @Date 2020/10/15 11:01
 * @Created chener@dtstack.com
 */
public class BaseParseResult {

    /**
     * 解析结果
     */
    private boolean parseSuccess = true;

    /**
     * 解析错误日志
     */
    private String failedMsg;

    /**
     * 标准 sql(格式化后的sql)
     */
    private String standardSql;

    /**
     * 原始sql
     */
    private String originSql;

    /**
     * sql类型
     */
    private SqlType sqlType;

    /**
     * 当前sql运行时的数据库
     */
    private String currentDb;

    public boolean isParseSuccess() {
        return parseSuccess;
    }

    public void setParseSuccess(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }

    public String getFailedMsg() {
        return failedMsg;
    }

    public void setFailedMsg(String failedMsg) {
        this.failedMsg = failedMsg;
    }

    public String getStandardSql() {
        return standardSql;
    }

    public void setStandardSql(String standardSql) {
        this.standardSql = standardSql;
    }

    public String getOriginSql() {
        return originSql;
    }

    public void setOriginSql(String originSql) {
        this.originSql = originSql;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }

    public String getCurrentDb() {
        return currentDb;
    }

    public void setCurrentDb(String currentDb) {
        this.currentDb = currentDb;
    }
}
