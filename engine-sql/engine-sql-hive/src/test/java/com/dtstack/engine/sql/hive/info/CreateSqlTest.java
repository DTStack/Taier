package com.dtstack.engine.sql.hive.info;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.hive.HiveSqlBaseTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author chener
 * @Classname CreateSqlTest
 * @Description TODO
 * @Date 2020/12/3 17:49
 * @Created chener@dtstack.com
 */
public class CreateSqlTest extends HiveSqlBaseTest {
    @Test
    public void testHiveCreateView() throws Exception {
        String sql = "CREATE VIEW V_REGION_SALES\n" +
                "AS SELECT *\n" +
                "FROM tablename";
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        ParseResult parseResult = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        SqlType sqlType = parseResult.getSqlType();
        Assert.assertEquals(sqlType, SqlType.CREATE_AS);
        Assert.assertEquals(parseResult.getExtraSqlType(), SqlType.CREATE_VIEW);
    }
}
