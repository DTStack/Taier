package com.dtstack.sql.rdb;

import com.dtstack.sql.ParseResult;
import com.dtstack.sql.SqlType;
import com.dtstack.sql.handler.IUglySqlHandler;
import com.dtstack.sql.handler.LibraUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class DeleteSqlParserTest  {

    static IUglySqlHandler iUglySqlHandler = new LibraUglySqlHandler();
    static CalciteNodeParser calciteNodeParser = new CalciteNodeParser(iUglySqlHandler);


    @Test
    public void deleteSqlTest() throws Exception {
        String sql = "delete from eee.sss where id in (select id from www)";
        ParseResult parseResult = calciteNodeParser.parseSql(sql, "shixi", new HashMap<>());
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DELETE);
    }

    @Test
    public void selectSqlTest() throws Exception {
        String sql = "select *  from user";
        ParseResult parseResult = calciteNodeParser.parseSql(sql, "shixi", new HashMap<>());
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DELETE);
    }

}
