package com.dtstack.rdos.engine.entrance.sql.operator;

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
