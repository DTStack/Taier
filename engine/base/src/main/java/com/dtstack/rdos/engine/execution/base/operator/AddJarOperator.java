package com.dtstack.rdos.engine.execution.base.operator;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class AddJarOperator implements Operator{
	
	/**
	 * add jar with xx
	 */
	private static String addJarPattern = "";
	
	private String jarPath;

	
	public String getJarPath() {
		return jarPath;
	}


	@Override
	public boolean createOperator(String sql)throws Exception {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
}
