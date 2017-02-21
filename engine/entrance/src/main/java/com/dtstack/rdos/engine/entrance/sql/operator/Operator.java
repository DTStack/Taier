package com.dtstack.rdos.engine.entrance.sql.operator;

public interface Operator {
	
	public boolean createOperator(String sql)throws Exception;
	
	public boolean verification(String sql)throws Exception;
}
