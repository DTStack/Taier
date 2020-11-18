package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.handler.HiveUglySqlHandler;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;

/**
 * @author chener
 * @Classname LateralTest
 * @Description TODO
 * @Date 2020/9/16 14:40
 * @Created chener@dtstack.com
 */
public class LateralTestSqlTest extends BaseSqlTest {
    @Test
    public void testLateral() throws Exception {
        String sql = readStringFromResource("lateral.sql");
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        ParseResult parseResult = sqlParser.parseSql(sql, "dev", new HashedMap());
        System.out.println(parseResult.getSqlType());
    }

    @Test
    public void testLateralSimple() throws Exception {
        String sql = readStringFromResource("lateral2.sql");
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        ParseResult parseResult = sqlParser.parseSql(sql, "dev", new HashedMap());
    }
}
