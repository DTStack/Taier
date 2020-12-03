package com.dtstack.engine.sql.hive.info;

import com.dtstack.engine.sql.AlterResult;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.TableOperateEnum;
import com.dtstack.engine.sql.hive.HiveSqlBaseTest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

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

    @Test
    public void testAlterTableLocation() throws Exception {
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        String sql = readStringFromResource("info/alterTableSetLocation.sql");
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        AlterResult alterResult = dev.getAlterResult();
        Assert.assertNotNull(alterResult);
        Assert.assertEquals(alterResult.getAlterType(), TableOperateEnum.ALTERTABLE_LOCATION);
    }

    @Test
    public void testHiveProp() throws Exception {
        String sql = "ALTER TABLE asdadasd SET TBLPROPERTIES('comment' = '这是新的。。。。当时的');";
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        ParseResult parseResult = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        SqlType sqlType = parseResult.getSqlType();
        Assert.assertEquals(sqlType, SqlType.ALTER);
        List<Pair<String, String>> tableProperties = parseResult.getAlterResult().getTableProperties();
        Assert.assertTrue(CollectionUtils.isNotEmpty(tableProperties));
    }
}
