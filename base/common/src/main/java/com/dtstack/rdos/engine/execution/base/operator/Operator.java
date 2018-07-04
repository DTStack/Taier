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
	
	void createOperator(String sql)throws Exception;
	
	boolean verific (String sql) throws Exception;
	
	String getSql();

}
