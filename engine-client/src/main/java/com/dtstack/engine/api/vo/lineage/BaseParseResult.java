package com.dtstack.engine.api.vo.lineage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname BaseParseResult
 * @Description 解析结果基类
 * @Date 2020/10/15 11:01
 * @Created chener@dtstack.com
 */
@ApiModel
public class BaseParseResult {

    /**
     * 解析结果
     */
    @ApiModelProperty("解析是否成功")
    private boolean parseSuccess = true;

    /**
     * 解析错误日志
     */
    @ApiModelProperty("失败信息")
    private String failedMsg;

    /**
     * 标准 sql(格式化后的sql)
     */
    @ApiModelProperty("格式化后的sql")
    private String standardSql;

    /**
     * 原始sql
     */
    @ApiModelProperty("原始sql")
    private String originSql;

    /**
     * sql类型
     */
    @ApiModelProperty("sql操作类型")
    private SqlType sqlType;

    /**
     * 附加类型，常用语创建临时表
     */
    @ApiModelProperty("sql操作类型")
    private SqlType extraType;

    /**
     * 当前sql运行时的数据库
     */
    @ApiModelProperty("当前sql运行数据库")
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

    public SqlType getExtraType() {
        return extraType;
    }

    public void setExtraType(SqlType extraType) {
        this.extraType = extraType;
    }
}
