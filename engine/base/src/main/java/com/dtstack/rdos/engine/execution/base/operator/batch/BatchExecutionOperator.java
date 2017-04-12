package com.dtstack.rdos.engine.execution.base.operator.batch;

import org.apache.commons.lang.StringUtils;

import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.exception.SqlVerificationException;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年04月05日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class BatchExecutionOperator implements Operator{
	
	private String sql;

	@Override
	public boolean createOperator(String sql) throws Exception{
		// TODO Auto-generated method stub
		this.sql = sql.trim();
		return true;
	}

	public String getSql() {
		return sql;
	}


	@Override
	public void verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		if(!StringUtils.isNotBlank(sql)||(!sql.trim().toLowerCase().startsWith("select")&&!sql.trim().toLowerCase().startsWith("insert")&&!sql.trim().toLowerCase().startsWith("create"))){
			throw new SqlVerificationException("batch execution");
		}
	}

	public static boolean verific(String sql) throws Exception{
		return StringUtils.isNotBlank(sql)&&(sql.trim().toLowerCase().startsWith("select")||sql.trim().toLowerCase().startsWith("insert")&&!sql.trim().toLowerCase().startsWith("create"));
	}
}
