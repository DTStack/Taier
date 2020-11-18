package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.SqlType;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author chener
 * @Classname HiveExplainTest
 * @Description TODO
 * @Date 2020/11/12 20:27
 * @Created chener@dtstack.com
 */
public class HiveExplainTest extends HiveSqlBaseTest {
    @Test
    public void testExplain() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        String sql = readStringFromResource("hiveExp.sql");
        ParseResult parseResult = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        SqlType sqlType = parseResult.getSqlType();
        Assert.assertEquals(sqlType,SqlType.ALTER);
    }
}
