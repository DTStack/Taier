package com.dtstack.sql.hive;

import com.dtstack.sql.ParseResult;
import com.dtstack.sql.SqlParserImpl;
import com.dtstack.sql.SqlType;
import com.dtstack.sql.Table;
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

    @Test
    public void testTableWithBlank() throws Exception {
        String sql = readStringFromResource("info/hiveBlank.sql");
        ParseResult parseResult = getHiveSqlParser().parseSql(sql, "dev", new HashMap<>());
        Table mainTable = parseResult.getMainTable();
        Assert.assertEquals(mainTable.getName(),"ods_bidb_ctn_info_szyt_dii");
        Assert.assertEquals(SqlType.CREATE,parseResult.getSqlType());
    }
}
