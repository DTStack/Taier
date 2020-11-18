package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.handler.HiveUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author chener
 * @Classname HIveSqlParserTest
 * @Description TODO
 * @Date 2020/8/10 16:18
 * @Created chener@dtstack.com
 */
public class HIveSqlParserTest {
    private static final String create_sql = "create table test(id int,name varchar(10))";

    @Test
    public void testCreate() throws Exception {
        AstNodeParser parser = new AstNodeParser(new HiveUglySqlHandler());
        List<Table> tables = parser.parseTables("db", create_sql);
        Assert.assertTrue(tables.size() == 1);
        Assert.assertTrue(tables.get(0).getName().equals("test"));
    }


    String impala_sql1 = "create table IF NOT EXISTS temp_ods_lsd_cardii_backup_cy\n" +
            "AS\n" +
            "select * \n" +
            "from ods_lsd_cardii \n" +
            "where dw_eti_month = substr(from_timestamp(to_timestamp(cast('20200820' AS STRING ), 'yyyymdd'), 'yyyy-MM-dd'),1,7)\n" +
            "and dw_eti_date <> from_timestamp(to_timestamp(cast('20200820' AS STRING ), 'yyyyMMdd'), 'yyyy-MM-dd')\n" +
            ";";
    @Test
    public void testImpla() throws Exception {
        SqlParserImpl sqlParser = new AstNodeParser(new ImpalaUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("db", impala_sql1);
        ParseResult aDefault = sqlParser.parseSql(impala_sql1, "default", new HashedMap());
        System.out.println(aDefault);
    }

    private static final String impala_sql2 = "create table IF NOT EXISTS temp_ods_lsd_cardii_cur_cy AS select a.cardii_id , a.cardii_idnum ,sa.cardii account , a.cardii_account_name , a.cardii_core_account_no , a.cardii_bind_card , a.cardii_mobile , a.cardii_node_no , a.cardii_frozen_amount , a.cardii_version , a.cardii_create_time , a.cardii_update_time , a.cardii_is_new_core_account_no , a.cardii_status , from_timestamp(to_timestamp(cast('20200820' AS STRING),'yyyyMMdd'),'yyyy-MM-dd') AS dw_eti_date, substr(from_timestamp(to_timestamp (cast ('20200820' AS STRING ), 'yyyyMMad'), 'yyyy-MM-dd'),1,7) AS du_eti_month from ( SELECT * FROM ods_lsd_cardii WHERE dw_eti_date = from_timestamp(adddate(to_timestamp (cast('20200820' AS STRING ), 'yyyyMMdd'), -1), 'yyyy-MM-dd' ) )a left join src_1sd_cardii b on a.cardii_id = b.cardii_id and b.pt = cast('20200820' as string) where b.cardii_id is NULL UNION ALL select cardii_id cardii_idnum , cardii_account , cardii_account_name , cardii_core_account_no cardii_bind_card , cardii_mobile ,cardii_node_no ,frozen_amount ,cardii_version ,cardii_create_time , cardii_update_time , cardii_is_new_core_account_no , cardii_status , from_timestamp(to_timestamp (cast('20200820' AS STRING ), 'yyyymdd' ), 'yyyy-MM-dd') AS dw_eti_date , substr(from_timestamp(to_timestamp(cast('20200820' AS STRING ), 'yyyyMMdd'), 'yyyy-MM-dd'),1,7) AS dw_eti_month from src_lsd_cardii where pt = cast('20200820' AS STRING )";
    @Test
    public void testImpala2() throws Exception {
        SqlParserImpl sqlParser = new AstNodeParser(new ImpalaUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", impala_sql2);
        ParseResult aDefault = sqlParser.parseSql(impala_sql2, "default", new HashedMap());
        System.out.println(aDefault);
    }

    private static final String impala_sql3 = "create table IF NOT EXISTS temp_ods_lsd_cardii_cur_cy\n" +
            " AS\n" +
            " select -- asdfa\n" +
            "a.cardii id\n" +
            ", a.cardii_idnum ,\n" +
            "a.cardii account ,\n" +
            " a.cardii_account_name ,\n" +
            "a.cardii_core_account_no ,\n" +
            " a.cardii_bind_card ,\n" +
            "a.cardii_mobile ,\n" +
            "a.cardii_node_no ,\n" +
            "a.cardii_frozen_amount ,\n" +
            "a.cardii_version ,\n" +
            " a.cardii_create_time ,\n" +
            "a.cardii_update_time ,\n" +
            " a.cardii_is_new_core_account_no ,\n" +
            " a.cardii_status,\n" +
            "from_timestamp(to_timestamp (cast('20200810' AS STRING ), 'yyyyMMdd'), 'yyyy-MM-dd') AS dw_eti_date\n" +
            ", substr(from_timestamp(to_timestamp (cast ('20200810' AS STRING ), 'yyyyMMad'), 'yyyy-MM-dd'),1,7) AS du_eti_month\n" +
            "from (\n" +
            "SELECT * FROM ods_lsd_cardii\n" +
            "    WHERE dw_eti_date = from_timestamp(adddate(to_timestamp (cast('20200810' AS STRING ), 'yyyyMMdd'), -1), 'yyyy-MM-dd')\n" +
            "     ) a\n" +
            "left join src_1sd_cardii b\n" +
            "    on a.cardii_id = b.cardii_id\n" +
            "    and b.pt = cast('20200820' as string)\n" +
            "     where b.cardii_id is NULL\n" +
            "    UNION ALL\n" +
            "    select\n" +
            "    cardii_id\n" +
            "cardii_idnum\n" +
            ", cardii_account\n" +
            ", cardii_account_name\n" +
            ", cardii_core_account_no\n" +
            "cardii_bind_card\n" +
            ", cardii_mobile\n" +
            ",cardii_node_no\n" +
            ",frozen_amount\n" +
            ",cardii_version\n" +
            ",cardii_create_time\n" +
            ", cardii_update_time\n" +
            ", cardii_is_new_core_account_no\n" +
            " , cardii_status\n" +
            " , from_timestamp(to_timestamp (cast('20200820' AS STRING ), 'yyyymmdd' ), 'yyyy-MM-dd') AS dw_eti_date\n" +
            ", substr(from_timestamp(to_timestamp(cast('20200820' AS STRING ), 'yyyyMMdd'), 'yyyy-MM-dd'),1,7) AS dw_eti_month\n" +
            " from src_lsd_cardii\n" +
            " where pt = cast('20200820' AS STRING )\n";
    @Test
    public void testImpala3() throws Exception {
        SqlParserImpl sqlParser = new AstNodeParser(new ImpalaUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", impala_sql3);
        ParseResult aDefault = sqlParser.parseSql(impala_sql3, "default", new HashedMap());
        System.out.println(aDefault);
    }

    private static final String impala4 = "INSERT INTO ods_lsd_cardii \n" +
            "( \n" +
            "\n" +
            "    cardii_id \n" +
            "    , cardii_idnum \n" +
            "    , cardii_account \n" +
            "    , cardii_account_name \n" +
            "    , cardii_core_account_no \n" +
            "    , cardii_bind_card\n" +
            "    ,cardii_mobile\n" +
            "     , cardii_node_no \n" +
            "    , cardii_frozen_amount \n" +
            "    , cardii_version \n" +
            "    , cardii_create_time\n" +
            "     , cardii_update_time\n" +
            "      , cardii_is_new_core_account_no \n" +
            "    , cardii_status \n" +
            "    , dw_eti_date \n" +
            ") "+
            "partition (dw_etl_month) "+
            "select\n" +
            "cardii_id \n" +
            ", cardii_idnum \n" +
            ", cardii_account \n" +
            ", cardii_account_name  \n" +
            ", cardii_core_account_no  \n" +
            ", cardii_bind_card  \n" +
            ", cardii_mobile  \n" +
            ", cardii_node_no  \n" +
            ", cardii_frozen_amount \n" +
            " , cardii_version \n" +
            "  , cardii_create_time\n" +
            "   , cardii_update_time \n" +
            "    , cardii_is_new_core_account_no \n" +
            "    ,cardii_status \n" +
            "     , dw_eti_date\n" +
            "      , dw_eti_month\n" +
            "FROM temp_ods_lsd_cardii_cur_cy \n" ;
    @Test
    public void testImpala4() throws Exception {
        SqlParserImpl sqlParser = new AstNodeParser(new ImpalaUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", impala4);
        ParseResult aDefault = sqlParser.parseSql(impala4, "default", new HashedMap());
        System.out.println(aDefault);
    }
    private static final String STORE_TYPE_SQL = "create table chener_0828_1(\n" +
            "    id int,\n" +
            "    name varchar(10)\n" +
            ") STORED as TEXTFILE;";
    @Test
    public void testStoreType() throws Exception {
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        ParseResult aDefault = sqlParser.parseSql(STORE_TYPE_SQL, "default", new HashedMap());
        String storeType = aDefault.getMainTable().getStoreType();
    }

    @Test
    public void testHiveInsertPartition() throws Exception {
        String sql = "insert into test(id,name) partition(pt) select * from source";
        SqlParserImpl sqlParser = new AstNodeParser(new HiveUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", sql);
        ParseResult aDefault = sqlParser.parseSql(sql, "default", new HashedMap());
        System.out.println(aDefault);
    }
}
