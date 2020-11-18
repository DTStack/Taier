package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlParser {

    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);
    static Map<String, List<Column>> tableColumnMap = new HashMap<>();

    @Test
    public void test() throws Exception {
        AstNodeParser astNodeParser = new AstNodeParser();
        String sql = "create table if not EXISTS tb_regress_hiveSQL_1(id int,name string) PARTITIONED BY (pt STRING) STORED AS TEXTFILE ";

        List<Table> result = astNodeParser.parseTables("hgx", sql);

        System.out.println(result.toString());
    }

    @Test
    public void withSql() throws Exception {
        String sql = "WITH t1 AS (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM carinfo\n" +
                "\t), \n" +
                "\tt2 AS (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM car_blacklist\n" +
                "\t)\n" +
                "SELECT *\n" +
                "FROM t1, t2 where id >0  limit 100,200";
        ParseResult result = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        Assert.assertEquals(result.getSqlType(), SqlType.WITH_QUERY);

    }

    @Test
    public void withUnionSql() throws Exception {
        String sql = "WITH t1 AS (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM carinfo\n" +
                "\t), \n" +
                "\tt2 AS (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM car_blacklist\n" +
                "\t)\n" +
                "SELECT *\n" +
                "FROM t1 union select * from t2";
        ParseResult result = astNodeParser.parseSql(sql,"hgx",new HashMap<>());
        Assert.assertEquals(result.getSqlType(), SqlType.WITH_QUERY);
    }

    @Test
    public void withInsertSql() throws Exception {
        String sql = "with \n" +
                "    t_with_1 as (select * from customers where age>25),\n" +
                "    t_with_2 as (select * from t_with_1 where age>50) \n" +
                "    INSERT INTO TABLE_WITH_NEW  SELECT * FROM t_with_2";
        ParseResult result = astNodeParser.parseSql(sql, "hgx", new HashMap<>());
        Assert.assertEquals(result.getSqlType(), SqlType.INSERT);
    }


    @Test
    public void testSql() throws Exception {
        String sql = "CREATE TABLE TABLE_WITH AS\n" +
                "with t_with_1 as (select\n" +
                "    * \n" +
                "from\n" +
                "    customers \n" +
                "where\n" +
                "    age>25), t_with_2 as (select\n" +
                "    * \n" +
                "from\n" +
                "    employee \n" +
                "where\n" +
                "    age>25) (\n" +
                "    select\n" +
                "        * \n" +
                "    from\n" +
                "        t_with_1 \n" +
                "    union\n" +
                "    select\n" +
                "        * \n" +
                "    from\n" +
                "        t_with_2\n" +
                ");";
        ParseResult result = astNodeParser.parseSql(sql,"hgx",new HashMap<>());
        System.out.println(result.getSqlType());
    }

}
