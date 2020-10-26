package com.dtstack.engine.sql;

import java.util.List;
import java.util.Map;

/**
 * sql解析器接口
 *
 * @author jiangbo
 * @date 2019/5/18
 */
public interface SqlParserImpl {

    /**
     * 解析给定的sql
     *
     * @param originSql 输入的sql
     * @param currentDb 当前的数据库
     * @param tableColumnsMap sql中表的字段信息
     * @return 解析的结果封装到 ParseResult 类里
     * @throws Exception 解析过程中会抛出异常，需要使用自己处理
     */
    ParseResult parseSql(String originSql, String currentDb, Map<String, List<Column>> tableColumnsMap) throws Exception;

    /**
     * 解析出sql语句中包含的表，一般用于带有查询语句的sql
     * @param currentDb
     * @param sql
     * @return
     * @throws Exception
     */
    List<Table> parseTables(String currentDb, String sql) throws Exception;


    /**
     * 解析 给定sql中的表级血缘
     * @param originSql 输入的sql
     * @param currentDb 默认db
     * @return
     * @throws Exception
     */
    ParseResult parseTableLineage(String originSql, String currentDb)throws Exception;

    /**
     * 解析生命周期和类目，填充到结果类中
     *
     * @param parseResult
     */
    void parseLifecycleAndCatalogue(ParseResult parseResult);
}
