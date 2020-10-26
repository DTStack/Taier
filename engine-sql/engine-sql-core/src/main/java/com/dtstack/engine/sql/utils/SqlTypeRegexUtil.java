package com.dtstack.engine.sql.utils;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * shixi
 * 如果解析sql失败  就采用正则的办法解析出来大概的sql类型
 */
public class SqlTypeRegexUtil {

    static String createSql = "(?i)(^create\\s+.*)";
    static String insertSql = "(?i)(^insert\\s+.*)";
    static String alterSql = "(?i)(^alter\\s+.*)";
    static String dropSql = "(?i)(^drop\\s+.*)";
    static String selectSql = "(?i)(^select\\s+.*)";
    static String withSql = "(?i)(^with\\s+.*)";
    static String showSql = "(?i)(^show\\s+.*)";

    /**
     * 对语法解析器未成功解析的sql进行简单的解析
     *
     * @param parseResult
     * @return
     */
    public static ParseResult getParseResultByRegexSql(ParseResult parseResult) {
        String sql = parseResult.getStandardSql().trim().toLowerCase().replaceAll("\n"," ").replaceAll("\t",  " ");
        Map<String, String> tableAndDb = new HashMap<>();
        if (sql.matches(createSql)) {
            tableAndDb = SqlRegexUtil.parseDbTableFromCreateSql(parseResult.getOriginSql());
            parseResult.setSqlType(SqlType.CREATE);
        } else if (sql.matches(insertSql)) {
            tableAndDb = SqlRegexUtil.parseDbTableFromInsertSql(parseResult.getOriginSql());
            parseResult.setSqlType(SqlType.INSERT);
        } else if (sql.matches(alterSql)) {
            tableAndDb = SqlRegexUtil.parseDbTableFromAlterSql(parseResult.getOriginSql());
            parseResult.setSqlType(SqlType.ALTER);
        } else if (sql.matches(dropSql)) {
            tableAndDb = SqlRegexUtil.parseDbTableFromDropSql(parseResult.getOriginSql());
            parseResult.setSqlType(SqlType.DROP);
        } else if (sql.matches(selectSql)) {
            parseResult.setSqlType(SqlType.QUERY);
        } else if (sql.matches(showSql)){
            parseResult.setSqlType(SqlType.SHOW);
        } else if (sql.matches(withSql)){
            if (sql.indexOf("create")>-1){
                parseResult.setSqlType(SqlType.CREATE);
            }else if (sql.indexOf("insert")>-1){
                parseResult.setSqlType(SqlType.INSERT);
            }else {
                parseResult.setSqlType(SqlType.SHOW);
            }
        }  else {
            parseResult.setSqlType(SqlType.OTHER);
        }
        if (!tableAndDb.isEmpty()) {
            Table table = new Table();
            table.setName(tableAndDb.get(SqlRegexUtil.KEY_TABLE));
            if (tableAndDb.containsKey(SqlRegexUtil.KEY_DB)) {
                table.setDb(tableAndDb.get(SqlRegexUtil.KEY_DB));
            } else {
                table.setDb(parseResult.getCurrentDb());
            }
            parseResult.setMainTable(table);
        }
        return parseResult;
    }
}
