package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class SelectSqlParser {

    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);

    @Test
    public void lateralSql() throws Exception {
        String sql = "select \n" +
                "  name, \n" +
                "  course, \n" +
                "  t_hobby.hobby \n" +
                "from lateral_test \n" +
                "lateral view explode(split(hobby, ',')) t_hobby as hobby,hobby1";
        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", new HashMap<>());
        Assert.assertEquals(parseResult.getSqlType(), SqlType.QUERY);
        System.out.println(parseResult.toString());

    }
    @Test
    public void yhSql() throws Exception {
        String sql = "SELECT\n" +
                "    c1 AS 'Employee ID',\n" +
                "    c2 AS 'Date of hire'  \n" +
                "FROM\n" +
                "    t1  where id = 1" ;

        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", new HashMap<>());
        Assert.assertEquals(parseResult.getSqlType(), SqlType.QUERY);
    }

    @Test
    public void unionSql() throws Exception {

        String sql = "select\n" +
                "concat(nvl(cast(contract_no as string), ''), '^') as loan_no,\n" +
                "concat(nvl(cast(from_unixtime(unix_timestamp(settle_date, 'yyyyMMdd'), 'yyyy-MM-dd') as string), ''), '^') as settle_date,\n" +
                "concat(nvl(cast(term_no as string), ''), '^') as term_no,\n" +
                "concat(nvl(cast(from_unixtime(unix_timestamp(start_date, 'yyyyMMdd'), 'yyyy-MM-dd') as string), ''), '^') as start_date,\n" +
                "concat(nvl(cast(from_unixtime(unix_timestamp(end_date, 'yyyyMMdd'), 'yyyy-MM-dd') as string), ''), '^') as end_date,\n" +
                "concat(nvl(cast(status as string), ''), '^') as status,\n" +
                "concat(nvl(cast(from_unixtime(unix_timestamp(clear_date, 'yyyyMMdd'), 'yyyy-MM-dd') as string), ''), '^') as clear_date,\n" +
                "concat(nvl(cast(from_unixtime(unix_timestamp(prin_ovd_date, 'yyyyMMdd'), 'yyyy-MM-dd') as string), ''), '^') as prin_ovd_date,\n" +
                "concat(nvl(cast(from_unixtime(unix_timestamp(int_ovd_date, 'yyyyMMdd'), 'yyyy-MM-dd') as string), ''), '^') as int_ovd_date,\n" +
                "concat(nvl(cast(prin_ovd_days as string), ''), '^') as prin_ovd_days,\n" +
                "concat(nvl(cast(int_ovd_days as string), ''), '^') as int_ovd_days,\n" +
                "'0^' as paid_normal_prin_amt,\n" +
                "'0^' as paid_normal_int_amt,\n" +
                "'0^' as paid_ovd_prin_amt,\n" +
                "'0^' as paid_ovd_int_amt,\n" +
                "'0^' as paid_ovd_prin_pnlt_amt,\n" +
                "'0^' as paid_ovd_int_pnlt_amt,\n" +
                "concat(nvl(cast(prin_bal/100 as string), ''), '^') as prin_bal,\n" +
                "concat(nvl(cast(int_bal/100 as string), ''), '^') as int_bal,\n" +
                "concat(nvl(cast(ovd_prin_pnlt_bal/100 as string), ''), '^') as ovd_prin_pnlt_bal,\n" +
                "concat(nvl(cast(ovd_int_pnlt_bal/100 as string), ''), '^') as ovd_int_pnlt_bal,\n" +
                "'0^' as paid_free,\n" +
                "concat(nvl(cast(left(cast(now() as string), 10) as string), ''), '^') as upd_date,\n" +
                "'MYBANK' as proj_no\n" +
                "from net_ods_mybank_instmnt_init_di where cdate = '2020-06-01' limit 10000;" ;

        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", new HashMap<>());
        Assert.assertEquals(parseResult.getSqlType(), SqlType.QUERY);
    }


}
