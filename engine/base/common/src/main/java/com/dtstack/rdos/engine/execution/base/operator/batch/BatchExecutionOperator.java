package com.dtstack.rdos.engine.execution.base.operator.batch;


import org.apache.commons.lang3.StringUtils;
import com.dtstack.rdos.engine.execution.base.operator.Operator;

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
	public void createOperator(String sql) throws Exception{
		// TODO Auto-generated method stub
		this.sql = sql;
	}

	public String getSql() {
		return sql.trim();
	}

	public boolean verific(String sql) throws Exception{
		return StringUtils.isNotBlank(sql)&&(sql.trim().toLowerCase().startsWith("select")||sql.trim().toLowerCase().startsWith("insert"));
	}
}
