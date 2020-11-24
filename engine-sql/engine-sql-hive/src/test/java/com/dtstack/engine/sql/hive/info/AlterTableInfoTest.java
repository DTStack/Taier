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
 * @Classname AlterTableColumn
 * @Description 修改表测试
 * @Date 2020/11/24 16:58
 * @Created chener@dtstack.com
 */
public class AlterTableInfoTest extends HiveSqlBaseTest {

    @Test
    public void testAlterTableName() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        String sql = readStringFromResource("info/alterTableName.sql");
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        Assert.assertEquals(dev.getSqlType(), SqlType.ALTER);
    }

    @Test
    public void testAlterTableComment() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        String sql = readStringFromResource("info/alterTableComment.sql");
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        Assert.assertEquals(dev.getSqlType(), SqlType.ALTER);
    }

    @Test
    public void testAlterTableDilm() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        String sql = readStringFromResource("info/alterTableDilm.sql");
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        Assert.assertEquals(dev.getSqlType(), SqlType.ALTER);
    }

    @Test
    public void testAlterTable() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        String sql = readStringFromResource("info/alterTable.sql");
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        Assert.assertEquals(dev.getSqlType(), SqlType.ALTER);
    }

    @Test
    public void testAlterTablePartition() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        String sql = readStringFromResource("info/alterTablePartition.sql");
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        Assert.assertEquals(dev.getSqlType(), SqlType.ALTER);
    }

    @Test
    public void testAlterTableRenamePartition() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        String sql = readStringFromResource("info/alterTableRenamePartition.sql");
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        Assert.assertEquals(dev.getSqlType(), SqlType.ALTER);
    }
}
