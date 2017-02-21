package com.dtstack.rdos.engine.entrance.sql.operator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ParamsOperator implements Operator{
	
	private Properties properties;

	@Override
	public boolean createOperator(String sql) throws Exception {
		// TODO Auto-generated method stub
		properties = new Properties(); 
        InputStream   inputStream   =  new  ByteArrayInputStream(sql.trim().getBytes());
        properties.load(inputStream);
		return true;
	}

	public Properties getProperties() {
		return properties;
	}
}
