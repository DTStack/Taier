package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class DeleteSqlParser {
    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);

    @Test
    public void deleteTest() throws Exception {
        String sql = "delete from eee.sss where id in (select id from www)";
        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", new HashMap<>());
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DELETE);
    }
}
