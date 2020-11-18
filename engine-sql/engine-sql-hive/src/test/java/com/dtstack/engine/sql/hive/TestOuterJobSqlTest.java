package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.SqlType;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author chener
 * @Classname TestOuterJob
 * @Description 测试outer join
 * @Date 2020/9/18 20:10
 * @Created chener@dtstack.com
 */
public class TestOuterJobSqlTest extends HiveSqlBaseTest {

    @Test
    public void testOuterJoin() throws Exception {
        String sql = readStringFromResource("outerJoin.sql");
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        ParseResult parseResult = hiveSqlParser.parseSql(sql, "dev", new HashedMap());
        Assert.assertEquals(parseResult.getSqlType(), SqlType.INSERT);
    }
}
