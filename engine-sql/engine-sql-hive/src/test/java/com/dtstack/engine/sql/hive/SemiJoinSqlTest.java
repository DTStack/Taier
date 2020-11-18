package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.Table;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author chener
 * @Classname OrangeSqlTest
 * @Description 甜橙金融血缘解析
 * @Date 2020/9/19 11:02
 * @Created chener@dtstack.com
 */
public class SemiJoinSqlTest extends HiveSqlBaseTest{
    @Test
    public void testSemiJoinSubQuerySql1() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        List<Table> tables = hiveSqlParser.parseTables("dev", readStringFromResource("semiJoin.sql"));
        printTables(tables);
        Assert.assertEquals(tables.size(),3);
    }

    @Test
    public void testSimpleJoinSql() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        List<Table> tables = hiveSqlParser.parseTables("dev", readStringFromResource("simpleJoin.sql"));
        printTables(tables);
    }

    @Test
    public void testOrange2() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        List<Table> tables = hiveSqlParser.parseTables("dev", readStringFromResource("orange2.sql"));
        printTables(tables);
    }

    @Test
    public void testOrange3() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        List<Table> tables = hiveSqlParser.parseTables("dev", readStringFromResource("orange3.sql"));
        printTables(tables);
    }


    @Test
    public void testOrange6() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        List<Table> tables = hiveSqlParser.parseTables("dev", readStringFromResource("orange6.sql"));
        printTables(tables);
    }

    @Test
    public void testOrange4() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        List<Table> tables = hiveSqlParser.parseTables("dev", readStringFromResource("orange4.sql"));
        printTables(tables);
    }
}
