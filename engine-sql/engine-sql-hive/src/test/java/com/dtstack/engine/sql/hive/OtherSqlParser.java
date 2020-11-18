package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 其他sql类型的 测试用例
 */
public class OtherSqlParser {
    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);
    static Map<String, List<Column>> tableColumnMap= new HashMap<>();

    @Test
    public void showTables() throws Exception {
        String sql = "show tables";
        ParseResult parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.SHOW_TABLES);
    }

    @Test
    public void descTables() throws Exception {
        String sql = "desc table1";
        ParseResult parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DESC_TABLE);

        sql = "describe formatted shixi";
        parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DESC_TABLE);
    }

    @Test
    public void  dataBasesOperate() throws Exception {
        String sql = "show dataBases";
        ParseResult parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DATABASE_OPERATE);

        sql = "drop database shixi";
        parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DATABASE_OPERATE);

        sql = "create database shixi";
        parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DATABASE_OPERATE);
    }

    @Test
    public void  specialShowOperate() throws Exception {
        String sql = "show dataBases";
        ParseResult parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DATABASE_OPERATE);

        sql = "drop database shixi";
        parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DATABASE_OPERATE);

        sql = "create database shixi";
        parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.DATABASE_OPERATE);
    }

    @Test
    public void  explainOperate() throws Exception {
        String sql = "explain select count(*) from tb_office_pri_102";
        ParseResult parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.EXPLAIN);

    }

    @Test
    public void  withSql() throws Exception {
        String sql = "explain select count(*) from tb_office_pri_102";
        ParseResult parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.EXPLAIN);

    }

    @Test
    public void  truncateSql() throws Exception {
        String sql = "truncate table office_pub.tb_28321_im1";
        ParseResult parseResult = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.TRUNCATE);
        Assert.assertEquals(parseResult.getMainTable().getName(), "tb_28321_im1");

    }


}

