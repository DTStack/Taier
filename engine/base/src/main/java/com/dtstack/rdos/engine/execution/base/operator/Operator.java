package com.dtstack.rdos.engine.execution.base.operator;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface Operator {
	
	public boolean createOperator(String sql)throws Exception;
	
	public void verification(String sql)throws Exception;

}
