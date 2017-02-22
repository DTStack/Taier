package com.dtstack.rdos.engine.execution.base.operator;

import java.util.Properties;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class CreateSourceOperator implements Operator{
	
	/**
	 *  CREATE source TABLE student_stream(
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
	private static String sourcePattern ="";
	
	private Properties properties;
	
	private String[] fields;
	
	private Class<?>[] fieldTypes;
	
	private String name;
	
	private String type;

	@Override
	public boolean createOperator(String sql) throws Exception{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean verification(String sql) throws Exception {
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

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}
