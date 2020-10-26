package com.dtstack.engine.sql;


import java.util.List;

/**
 * sql解析结果类
 *
 * @author jiangbo
 */
public class ParseResult {

    /**
     * 解析结果
     */
    private boolean parseSuccess = true;

    /**
     * 解析错误日志
     */
    private String failedMsg;

    /**
     * 标准 sql
     */
    private String standardSql;

    /**
     * 带有生命周期，类目的sql
     */
    private String originSql;

    /**
     * sql类型
     */
    private SqlType sqlType;

    /**
     * 额外sql类型，目前libra中用于创建临时表类型
     */
    private SqlType extraSqlType;

    /**
     * 当前sql运行时的数据库
     */
    private String currentDb;

    /**
     * DDL语句中的表(DDL语句都是针对一张表的)
     */
    private Table mainTable;

    /**
     * alter语句解析结果
     */
    private AlterResult alterResult;

    /**
     * 当为sql带有查询语句时会解析出一个查询树
     */
    private QueryTableTree root;

    /**
     * 字段血缘，包含数据库，表，字段三部分
     * 需要手动触发从root树中解析
     */
    private List<ColumnLineage> columnLineages;

    /**
     * 表级血缘  包含数据库 表
     */
    private List<TableLineage> tableLineages;

    private List<Table> tables;

    /**
     * 如果是查询语句 limit是否存在
     */
    private List<Long> limit;

    public ParseResult() {
    }

    public SqlType getExtraSqlType() {
        return extraSqlType;
    }

    public void setExtraSqlType(SqlType extraSqlType) {
        this.extraSqlType = extraSqlType;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public List<ColumnLineage> getColumnLineages() {
        return columnLineages;
    }

    public void setColumnLineages(List<ColumnLineage> columnLineages) {
        this.columnLineages = columnLineages;
    }

    public QueryTableTree getRoot() {
        return root;
    }

    public void setRoot(QueryTableTree root) {
        this.root = root;
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

    public Table getMainTable() {
        return mainTable;
    }

    public void setMainTable(Table mainTable) {
        this.mainTable = mainTable;
    }

    public AlterResult getAlterResult() {
        return alterResult;
    }

    public void setAlterResult(AlterResult alterResult) {
        this.alterResult = alterResult;
    }

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

    public List<TableLineage> getTableLineages() {
        return tableLineages;
    }

    public void setTableLineages(List<TableLineage> tableLineages) {
        this.tableLineages = tableLineages;
    }

    public List<Long> getLimit() {
        return limit;
    }

    public void setLimit(List<Long> limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "standardSql='" + standardSql + '\'' +
                ", originSql='" + originSql + '\'' +
                ", sqlType=" + sqlType +
                ", currentDb='" + currentDb + '\'' +
                ", mainTable=" + mainTable +
                ", alterResult=" + alterResult +
                ", root=" + root +
                ", columnBlood=" + columnLineages +
                ", tableBlood=" + tableLineages +
                '}';
    }
}
