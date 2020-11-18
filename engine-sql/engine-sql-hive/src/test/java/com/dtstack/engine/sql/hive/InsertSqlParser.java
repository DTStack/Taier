package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertSqlParser {
    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);
    static Map<String, List<Column>> tableColumnMap= new HashMap<>();

    static {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("id",0));
        columns.add(new Column("name",1));
        columns.add(new Column("text",2));
        columns.add(new Column("address",3));

        List<Column> columns1 = new ArrayList<>();
        columns1.add(new Column("id1",0));
        columns1.add(new Column("name1",1));
        columns1.add(new Column("text1",2));
        columns1.add(new Column("address1",3));

        List<Column> columns2 = new ArrayList<>();
        columns2.add(new Column("id2",0));
        columns2.add(new Column("name2",1));
        columns2.add(new Column("text2",2));
        columns2.add(new Column("address2",3));

        List<Column> columns3 = new ArrayList<>();
        columns3.add(new Column("id3",0));
        columns3.add(new Column("name3",1));
        columns3.add(new Column("text3",2));
        columns3.add(new Column("address3",3));
        tableColumnMap.put("shixi.a",columns);
        tableColumnMap.put("shixi.b",columns1);
        tableColumnMap.put("shixi.c",columns2);
        tableColumnMap.put("shixi.d",columns3);
    }

    @Test
    public void simpleSql() throws Exception {
        String sql = "insert into table a   select id1,name1 from b";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(),2);
    }

    @Test
    public void joinSql() throws Exception {
        String sql = "insert into table a   select id1,name1,id2,name2 from b b left join c c on c.id2 = b.id1";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(),4);
    }

    @Test
    public void joinOtherSql() throws Exception {
        String sql = "insert into table a   select id1,name1,id2,name2 from b,c";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(),4);
    }

    @Test
    public void sonSelectSql() throws Exception {
        String sql = "insert into table a   select id2,name2 from (select id2, name2 from c) b";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(),2);
    }

    @Test
    public void joinSonSelectSql() throws Exception {
        String sql = "insert into table a   select id2,name2,id3,name3 from (select id2, name2 from c)b left join d d on b.id = d.id";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(),4);
    }


    @Test
    public void unionSelectSql() throws Exception {
        String sql = "insert into table a   select id1,name1,id2,name2 from (select id2, name2 from c union select id1,name1 from d)b ";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(),4);
    }

    @Test
    public void functionSelectSql() throws Exception {
        String sql = "insert into table a  select nvl(id1),name1 from b ";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(),2);
    }

    @Test
    public void moreFunctionSelectSql() throws Exception {
        String sql = "insert into table a   select nvl(nvl(id1),address1),name1 from b ";
        ParseResult p = astNodeParser.parseSql(sql,"shixi",tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(),3);
    }

    @Test
    public void insertSelectSql() throws Exception {
        String sql = "INSERT   INTO     shier.tb_wf_impala_3 PARTITION (pt=\"20200505\")     SELECT         id,         y,         x0,         x1,         x2,         x3,         x4,         x5,         x6,         x7,         x8,         x9,         x10,         x11,         x12,         x13,         x14            FROM     (         SELECT             *                   FROM             tb_wf_hive_1                       UNION         SELECT             *                   FROM             tb_wf_hive_2                       UNION         SELECT             *                   FROM             tb_wf_hive_3           ) t";
        List<Table> tables = astNodeParser.parseTables("shixi", sql);
        System.out.println(tables.size());
    }

    @Test
    public void insert_oneSql() throws Exception {
        String sql = "INSERT   INTO   a   values (1,\"ssss\",\"ssss\",\"ssss\")";
        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.INSERT);
    }


    @Test
    public void insertPartitionSql() throws Exception {
        String sql = "DESCRIBE formatted tb_regress_impalaSQL_4";
        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        System.out.println(parseResult.toString());
    }


    @Test
    public void withSelectQtSql() throws Exception {
        String sql = "with \n" +
                "    t_with_1 as (select * from a where id>25),\n" +
                "    t_with_2 as (select * from t_with_1 where age>50) \n" +
                "    INSERT INTO a  SELECT * FROM t_with_2";
        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.INSERT);
        Assert.assertEquals(result.getColumnLineages().size(), 4);
    }

    @Test
    public void insertJoinSql() throws Exception {
        String sql ="insert overwrite table shixi.a\n" +
                "partition(dm_etl_date='000000000000000')\n" +
                "select * from (\n" +
                "select\n" +
                "id1 as id,\n" +
                "name1,\n" +
                "text1 " +
                "from shixi.b orders\n" +
                "left semi join (\n" +
                "select product_no\n" +
                "from adm.dm_hongbao_user_info\n" +
                "where dm_etl_date='000000000000001'\n" +
                "and to_date(first_txn_time)<'000000000000002') users\n" +
                "on orders.mobile_nbr = users.product_no\n" +
                "where orders.day_id='000000000000003'\n" +
                "and orders.xiaofei_ind = '1'\n" +
                "\n" +
                "union all\n" +
                "\n" +
                "select\n" +
                "    id1 as id1,\n" +
                "    name1,\n" +
                "    text1\n" +
                "from shixi.b orders\n" +
                "left semi join (\n" +
                "        select product_no\n" +
                "        from adm.dm_hongbao_user_info\n" +
                "        where dm_etl_date='000000000000004'\n" +
                "          and to_date(first_txn_time)='000000000000005') users\n" +
                "    on orders.mobile_nbr = users.product_no\n" +
                "where orders.day_id >= '2018-01-01'\n" +
                "  and orders.day_id <= '000000000000006'\n" +
                "  and orders.xiaofei_ind = '1') tmp\n" +
                "distribute by 1";
        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(parseResult.getSqlType(), SqlType.INSERT);
    }





}
