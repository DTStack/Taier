package com.dtstack.rdos.engine.execution.base.operator.batch;

/**
 * Created by sishu.yss on 2017/4/13.
 */

import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.exception.SqlVerificationException;

public class BatchCreateFunctionOperator implements Operator{

    //CREATE TEMPORARY FUNCTION  strip AS 'com.hcr.hadoop.hive.Strip';
    private static String pattern = "BATCHCREATEFUNCTION";

    private String sql ;

    @Override
    public boolean createOperator(String sql) throws Exception {
        this.sql = sql.trim();
        return true;
    }

    @Override
    public void verification(String sql) throws Exception {
        if(GrokUtil.isSuccess(pattern, sql)){
            throw new SqlVerificationException("create batch function");
        }
    }

    public static boolean verific(String sql) throws Exception{
        return GrokUtil.isSuccess(pattern, sql);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
