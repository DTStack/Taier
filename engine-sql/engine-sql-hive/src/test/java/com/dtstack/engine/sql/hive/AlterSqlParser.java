package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.TableOperateEnum;
import com.dtstack.engine.sql.handler.HiveUglySqlHandler;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class AlterSqlParser {

    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);

    @Test
    public void alterTableNameSql() throws Exception {
        String sql = "alter table shier.regress_mobile rename to office_pri.regress_mobile";
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());

        parseResult.getAlterResult();
    }


    @Test
    public void alterColumnNameParse() throws Exception {
        String sql = "ALTER TABLE employee CHANGE name ename String";
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        System.out.println(parseResult.toString());
    }

    @Test
    public void alterColumnAfterNameParse() throws Exception {
        String sql = "ALTER TABLE employee CHANGE name ename String AFTER b";
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        System.out.println(parseResult.toString());
    }

    @Test
    public void addColumnParse() throws Exception {
        String sql = "ALTER TABLE employee ADD COLUMNS (dept STRING COMMENT 'Department name')";
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        System.out.println(parseResult.toString());
    }

    @Test
    public void dropPartitionParse() throws Exception {
        String sql = "alter table dwd_trd_r_base1 drop if exists partition(report_date='2018-03-01')";
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        System.out.println(parseResult.toString());
    }

    @Test
    public void addPartitionParse() throws Exception {
        String sql = "alter table office_pri.dirty_regress_sync_sftp2impala add partition(task_name='regress_sync_sftp2impala',time='1588752784138')";
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        Assert.assertEquals(parseResult.getAlterResult().getAlterType(), TableOperateEnum.ALTERTABLE_ADDPARTS);
        System.out.println(parseResult.toString());
    }

    @Test
    public void alterCommentParse() throws Exception {
        String sql = "ALTER TABLE table_name SET TBLPROPERTIES('comment' = \"new_comment\")";
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        Assert.assertEquals(parseResult.getAlterResult().getAlterType(), TableOperateEnum.ALTERTABLE_PROPERTIES);
        System.out.println(parseResult.toString());
    }

    @Test
    public void alterAddPartitionParse() throws Exception {
        String sql = "ALTER table tb_regress_hiveSQL_4 add PARTITION (pt = '2020041601')";
        astNodeParser = new AstNodeParser(new HiveUglySqlHandler());
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        Assert.assertEquals(parseResult.getAlterResult().getAlterType(), TableOperateEnum.ALTERTABLE_ADDPARTS);
        System.out.println(parseResult.toString());
    }

    @Test
    public void alterAddPartitionAndLocationParse() throws Exception {
        String sql = "ALTER TABLE page_view ADD PARTITION (dt='2008-08-08', country='us')\n" +
                "location '/path/to/us/part080808' PARTITION (dt='2008-08-09',country='us') location '/path/to/us/part080809';" ;
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        Assert.assertEquals(parseResult.getAlterResult().getAlterType(), TableOperateEnum.ALTERTABLE_ADDPARTS);
        System.out.println(parseResult.toString());
    }

    @Test
    public void alterPartitionParse() throws Exception {
        String sql = "alter table people partition(department='1',sex='0',howold=23) rename to partition(department='2',sex='1',howold=24)" ;
        ParseResult parseResult = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        Assert.assertEquals(parseResult.getAlterResult().getAlterType(), TableOperateEnum.ALTERTABLE_RENAMEPART);
        System.out.println(parseResult.toString());
    }

}
