package com.dtstack.sql.rdb;

import com.dtstack.sql.ParseResult;
import com.dtstack.sql.handler.LibraUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author chener
 * @Classname CalciteNodeParserTest
 * @Description TODO
 * @Date 2020/8/24 22:56
 * @Created chener@dtstack.com
 */
public class CalciteNodeParserTest {
    @Test
    public void test() throws Exception {
        CalciteNodeParser parser = new CalciteNodeParser(new LibraUglySqlHandler());
        ParseResult result = parser.parseSql("select id,name from test", "db", null);
        Assert.assertNotNull(result.getMainTable());
    }
}
