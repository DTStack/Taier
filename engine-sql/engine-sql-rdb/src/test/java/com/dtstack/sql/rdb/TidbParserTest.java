package com.dtstack.sql.rdb;

import com.dtstack.sql.ParseResult;
import com.dtstack.sql.SqlParserImpl;
import com.dtstack.sql.SqlType;
import com.dtstack.sql.Table;
import com.dtstack.sql.handler.LibraUglySqlHandler;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

/**
 * @author chener
 * @Classname TidbParserTest
 * @Description TODO
 * @Date 2020/9/2 18:35
 * @Created chener@dtstack.com
 */
public class TidbParserTest extends TidbSqlBaseTest{

    @Test
    public void testCreate() throws Exception {
        String sql = "CREATE TABLE `async_result14`(\n" +
                "    `result_id` INT COMMENT'',\n" +
                "    `result_name` VARCHAR(20) COMMENT'',\n" +
                "    `result_phone` VARCHAR(20) COMMENT''\n" +
                " )comment''";

//        String sql2 = "CREATE TABLE async_result1(   result_id INT  ,     result_name VARCHAR(20)  ,     result_phone VARCHAR(20)    )";

        CalciteNodeParser sqlParser = new CalciteNodeParser(new LibraUglySqlHandler());
        ParseResult aDefault = sqlParser.parseSql(sql, "default", new HashedMap());
        Assert.assertEquals(aDefault.getSqlType(), SqlType.CREATE);
        Assert.assertNotNull(aDefault.getMainTable());
        Assert.assertEquals(aDefault.getMainTable().getName(),"async_result14");
    }

    @Test
    public void testSelect() throws Exception {
        String sql = "select * from tidb_test1.test_beihai_tidb_1127_1";
        SqlParserImpl tidbParser = getTidbParser();
        ParseResult parseResult = tidbParser.parseSql(sql, "dev", new HashMap<>());
        List<Table> tables = parseResult.getTables();
        Assert.assertEquals(tables.size(),1);
    }
}
