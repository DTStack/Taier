package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ColumnLineage;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname HiveCreateColumnLineageTest
 * @Description 血缘解析测试
 * @Date 2020/11/18 14:09
 * @Created chener@dtstack.com
 */
public class HiveColumnLineageTest extends HiveSqlBaseTest{

    @Test
    public void testCreateAs() throws Exception {
        String sql = "create table chener as select * from chener1";
        Map<String, List<Column>> stringListMap = readColumnMapFromResource("hiveColumnMap.json");
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", stringListMap);
        List<ColumnLineage> columnLineages = dev.getColumnLineages();
        Assert.assertEquals(columnLineages.size(),3);
    }
}
