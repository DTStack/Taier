package com.dtstack.engine.lineage.util;/**
 * @author chenfeixiang6@163.com
 * @date 2021/4/17
 */

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.sqlparser.common.client.ISqlParserClient;
import com.dtstack.sqlparser.common.client.SqlParserClientCache;
import com.dtstack.sqlparser.common.client.exception.ClientAccessException;

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
            sqlParserClient = SqlParserClientCache.getInstance().getClient(name);
        } catch (ClientAccessException e) {
            throw new RdosDefineException("get sqlParserClient error");
        }
        if(null == sqlParserClient){
            throw new RdosDefineException("get sqlParserClient error");
        }
        return sqlParserClient;
    }





    private SqlParserClientOperator() {
    }
}


