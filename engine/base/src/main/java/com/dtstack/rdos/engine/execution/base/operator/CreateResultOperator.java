package com.dtstack.rdos.engine.execution.base.operator;

import java.util.Properties;

public class CreateResultOperator implements Operator{
	
	
	/**
	 * CREATE STREAM TABLE student_stream(
     *  id BIGINT,
     *  name STRING) WITH (
     *  type='datahub',
	 *  endpoint='http://dh-cn-hangzhou.aliyuncs.com',
	 *  accessId='OERGMhXn6H2mBkhk',
	 *  accessKey='qnuSKMKoMcY5Va97GGFtL0nvlAoLZx',
	 *  projectName='dtstack',
	 *  topic='datahub_test'
	 *  );
	 */
	private static String resultPattern="";
	
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

	@Override
	public boolean verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	
}
