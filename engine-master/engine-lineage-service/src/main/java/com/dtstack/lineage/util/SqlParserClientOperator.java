package com.dtstack.lineage.util;/**
 * @author chenfeixiang6@163.com
 * @date 2021/4/17
 */

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.sqlparser.common.client.ISqlParserClient;
import com.dtstack.sqlparser.common.client.SqlParserClientCache;
import com.dtstack.sqlparser.common.client.domain.Column;
import com.dtstack.sqlparser.common.client.domain.ParseResult;
import com.dtstack.sqlparser.common.client.domain.Table;
import com.dtstack.sqlparser.common.client.enums.ETableType;
import com.dtstack.sqlparser.common.client.exception.ClientAccessException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *类名称:SqlParserClientOperator
 *类描述:TODO
 *创建人:newman
 *创建时间:2021/4/17 11:54 上午
 *Version 1.0
 */

public class SqlParserClientOperator {

    private SqlParserClientCache clientCache = SqlParserClientCache.getInstance();

    private static class SingletonHolder {
        private static SqlParserClientOperator singleton = new SqlParserClientOperator();
    }

    public static SqlParserClientOperator getInstance() {
        return SqlParserClientOperator.SingletonHolder.singleton;
    }

    public ISqlParserClient getClient(String name){
        ISqlParserClient sqlParserClient = null;
        try {
            sqlParserClient = SqlParserClientCache.getInstance().getClient("sqlparser");
        } catch (ClientAccessException e) {
            throw new RdosDefineException("get sqlParserClient error");
        }
        if(null == sqlParserClient){
            throw new RdosDefineException("get sqlParserClient error");
        }
        return sqlParserClient;
    }



    public Set<String> parseFunction(String name,String sql) throws Exception {
        ISqlParserClient client = getClient(name);
        return client.parseFunction(sql);
    }

    public List<Table> parseTables(String name,String defaultDb, String sql, ETableType eTableType) throws Exception {
        ISqlParserClient client = getClient(name);
        return client.parseTables(defaultDb,sql,eTableType);
    }

    public ParseResult parseTableLineage(String name,String defaultDb, String sql, ETableType eTableType) throws Exception {
        ISqlParserClient client = getClient(name);
        return client.parseTableLineage(defaultDb,sql,eTableType);
    }

    public ParseResult parseSql(String name, String sql, String defaultDb, Map<String, List<Column>> map, ETableType eTableType) throws Exception {
        ISqlParserClient client = getClient(name);
        return client.parseSql(sql, defaultDb, map, eTableType);

    }


    private SqlParserClientOperator() {
    }
}


