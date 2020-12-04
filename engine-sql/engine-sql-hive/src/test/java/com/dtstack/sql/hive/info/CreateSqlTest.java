package com.dtstack.sql.hive.info;

import com.dtstack.sql.ParseResult;
import com.dtstack.sql.SqlParserImpl;
import com.dtstack.sql.SqlType;
import com.dtstack.sql.hive.HiveSqlBaseTest;
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

    /**
     * hive中database和schema是同一个概念
     */
    private static final String create_database = "create database if not exists test_db comment 'comment' location 'hdfs_path' MANAGEDLOCATION 'mlocation' WITH DBPROPERTIES (comment = '')";

    private static final String create_schema = "create schema if not exists test_db";

    private static final String drop_database = "drop database if exists test_db CASCADE";

    private static final String alter_db1 = "alter database test_db set dbproperties(comment='asd',location='sd')";

    private static final String alter_db2 = "alter database test_db set owner hive";

    private static final String alter_db3 = "alter database set location 'sd'";

    private static final String alter_db4 = "alter schema set managelocation 'asd'";

    private static final String use_db = "use test";

}
