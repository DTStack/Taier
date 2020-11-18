package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;

import java.util.List;

/**
 * @author chener
 * @Classname ImpalaSqlTest
 * @Description TODO
 * @Date 2020/8/31 15:43
 * @Created chener@dtstack.com
 */
public class ImpalaSqlTest {

    private static final String impala1 = "INSERT INTO TABLE DWD_TXN_BCS_FIN_JNL (\n" +
            "CUSM_NO    -- 客户号或账号\n" +
            ",TRAN_DATE    -- 交易日期\n" +
            ",BRCH    -- 分行号\n" +
            ",TELLER    -- 柜员号\n" +
            ",POST_DATE    -- 发送日期\n" +
            ",HOME_BRNCH    -- 发起行号\n" +
            ",FROM_CURR_RATE    -- 转出币种\n" +
            ",TO_CURR_RATE    -- 转入币种汇率\n" +
            ",BUS_INC_CODE    -- 结售汇项目代码\n" +
            ",CURR_CHANGE    -- 兑换货币金额\n" +
            ",FROM_CURR_CODE    -- 转出币种\n" +
            ",TO_CURRENCY_CODE    -- 截止货币代码\n" +
            ",JNRST_AMOUNT    -- 交易金额\n" +
            ",JRNL_NO    -- 流水号\n" +
            ",JNRST_TRANSFER_ACCT    -- 转账账号\n" +
            ",TRAN_CODE    -- 交易码\n" +
            ",GL_CLASS_CODE    -- 机构产品大类\n" +
            ",JNRST_DATA_2    -- 资料2\n" +
            ",JNRST_TRANSFER_ACCT1    -- 转账账号\n" +
            ",RATE_TYPE    -- 汇率类型1\n" +
            ",JR01_DATE    -- 流水日期\n" +
            ",TO_CURR_AMOUNT    -- 转入币种金额\n" +
            ",PROMO_NO    -- 提示码\n" +
            ",SYST    -- 系统\n" +
            ",BHID\n" +
            ",DW_ETL_DATE)PARTITION(ETL_MONTH)\n" +
            "\n" +
            "SELECT\n" +
            "T1.CUSM_NO     -- 客户号或账号\n" +
            ",T1.TRAN_DATE     -- 交易日期\n" +
            ",T1.BRCH     -- 分行号\n" +
            ",T1.TELLER     -- 柜员号\n" +
            ",T1.POST_DATE     -- 发送日期\n" +
            ",T1.HOME_BRNCH     -- 发起行号\n" +
            ",T1.FROM_CURR_RATE     -- 转出币种\n" +
            ",T1.TO_CURR_RATE     -- 转入币种汇率\n" +
            ",T1.BUS_INC_CODE     -- 结售汇项目代码\n" +
            ",T1.CURR_CHANGE     -- 兑换货币金额\n" +
            ",T1.FROM_CURR_CODE     -- 转出币种\n" +
            ",T1.TO_CURRENCY_CODE     -- 截止货币代码\n" +
            ",T1.JNRST_AMOUNT     -- 交易金额\n" +
            ",T1.JRNL_NO     -- 流水号\n" +
            ",T1.JNRST_TRANSFER_ACCT     -- 转账账号\n" +
            ",T1.TRAN_CODE     -- 交易码\n" +
            ",T1.GL_CLASS_CODE     -- 机构产品大类\n" +
            ",T1.JNRST_DATA_2     -- 资料2\n" +
            ",T1.JNRST_TRANSFER_ACCT1     -- 转账账号\n" +
            ",T1.RATE_TYPE     -- 汇率类型1\n" +
            ",T1.JR01_DATE     -- 流水日期\n" +
            ",T1.TO_CURR_AMOUNT     -- 转入币种金额\n" +
            ",T1.PROMO_NO     -- 提示码\n" +
            ",T1.SYST     -- 系统\n" +
            ",T1.bhid\n" +
            ",T1.DW_ETL_DATE\n" +
            ",CONCAT(SUBSTR('20200820',1,4),'-',SUBSTR('20202020',5,2))\n" +
            "FROM ODS_BCS_JR01 T1 WHERE T1.DW_ETL_DATE = CONCAT(SUBSTR('20200820',1,4),'-',SUBSTR('20200820',5,2),'-',SUBSTR('20200820',7,2)) \n" +
            "--AND T1.bhid = '0800'\n" +
            "UNION ALL\n" +
            "SELECT * FROM DWD_TXN_BCS_FIN_JNL_BAK20200820";

    @Test
    public void testImpala1() throws Exception {
        SqlParserImpl sqlParser = new AstNodeParser(new ImpalaUglySqlHandler());
        List<Table> tables = sqlParser.parseTables("default", impala1);
        ParseResult aDefault = sqlParser.parseSql(impala1, "default", new HashedMap());
        System.out.println(aDefault);
    }
}
