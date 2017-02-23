package com.dtstack.rdos.engine.execution.base.operator;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ExecutionOperator implements Operator{
	
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
	public boolean verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		return StringUtils.isNotBlank(sql);
	}
}
