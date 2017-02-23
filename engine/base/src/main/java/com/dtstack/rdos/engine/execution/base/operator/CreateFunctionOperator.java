package com.dtstack.rdos.engine.execution.base.operator;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class CreateFunctionOperator implements Operator{
	
	/**
	 * CREATE (TABBLE|SCALA) FUNCTION xxx WITH com.dtstack.testFunction
	 */
	private static String functionPattern ="";
	
	private String name;

	private String className;
	
	private String type;
	

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

	public String getType() {
		return type;
	}
	

}
