package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateSqlParser {

    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);
    static Map<String, List<Column>> tableColumnMap = new HashMap<>();

    @Test
    public void updateSimpleSql() throws Exception {
        String sql = "update quene_kudu1 set name ='qt1_new' where id = 1";
        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.UPDATE);
    }
}
