package com.dtstack.rdos.engine.execution.base.operator;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class CreateScalaFunctionOperator implements Operator{

	/**
	 * create scala function xxx with com.dtstack.testFunction
	 */
	private static String scalaPattern="";
	
	private String name;

	private String className;
	
	
	@Override
	public boolean createOperator(String sql) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

}
