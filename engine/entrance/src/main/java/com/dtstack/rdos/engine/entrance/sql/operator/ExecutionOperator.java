package com.dtstack.rdos.engine.entrance.sql.operator;

public class ExecutionOperator implements Operator{
	
	private String sql;

	@Override
	public boolean createOperator(String sql) throws Exception{
		// TODO Auto-generated method stub
		this.sql = sql.trim();
		return true;
	}

}
