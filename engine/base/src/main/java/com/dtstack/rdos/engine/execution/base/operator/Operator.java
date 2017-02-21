package com.dtstack.rdos.engine.execution.base.operator;

public interface Operator {
	
	public boolean createOperator(String sql)throws Exception;
	
	public boolean verification(String sql)throws Exception;
}
