package com.dtstack.rdos.engine.execution.base.operator.batch;

import com.dtstack.rdos.common.util.GrokUtil;

import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.exception.SqlVerificationException;

import java.util.Map;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年04月05日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class BatchExecutionOperator implements Operator{

    private static String pattern = "BATCHSQL";

    private String sql;

	@Override
	public boolean createOperator(String sql) throws Exception{
        Map<String,Object> result = GrokUtil.toMap(pattern, sql);
        String targetSql = (String)result.get("sql");
		this.sql = targetSql.trim();
		return true;
	}

	public String getSql() {
		return sql;
	}


    @Override
    public void verification(String sql) throws Exception {
        if(GrokUtil.isSuccess(pattern, sql)){
            throw new SqlVerificationException("add batch sql");
        }
    }

    public static boolean verific(String sql) throws Exception{
        return GrokUtil.isSuccess(pattern, sql);
    }
}
