package com.dtstack.rdos.engine.entrance.sql.operator;

import java.util.Properties;

public class CreateSourceOperator implements Operator{
	
	private Properties properties;
	
	private String[] fields;
	
	private Class<?>[] fieldTypes;

	@Override
	public boolean createOperator(String sql) throws Exception{
		// TODO Auto-generated method stub
		return false;
	}

	public Properties getProperties() {
		return properties;
	}

	public String[] getFields() {
		return fields;
	}

	public Class<?>[] getFieldTypes() {
		return fieldTypes;
	}
}
