package com.dtstack.sql.hive;

import com.dtstack.sql.ParseResult;
import com.dtstack.sql.SqlType;
import com.dtstack.sql.handler.IUglySqlHandler;
import com.dtstack.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class DropSqlParser {

    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);

    @Test
    public void dropSql() throws Exception {
        String sql = "drop table if  exists a ";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",new HashMap<>());
        Assert.assertEquals(p.getSqlType(), SqlType.DROP);
    }
}
