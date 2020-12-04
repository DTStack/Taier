package com.dtstack.sql.hive;

import com.dtstack.sql.Column;
import com.dtstack.sql.ParseResult;
import com.dtstack.sql.SqlType;
import com.dtstack.sql.handler.IUglySqlHandler;
import com.dtstack.sql.handler.ImpalaUglySqlHandler;
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
