package com.dtstack.rdos.engine.execution.base.operator.stream;

import java.util.Map;
import java.util.Properties;

import com.dtstack.rdos.common.util.ClassUtil;
import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.exception.SqlVerificationException;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class CreateResultOperator implements Operator{
	
	/**
	 *  CREATE RESULT TABLE student_stream(
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
	private static String pattern="CREATERESULT";
	
	private Properties properties;
	
	private String[] fields;
	
	private Class<?>[] fieldTypes;
	
	private String name;
	
	private String type;
	
	private String sql;

	@Override
	public boolean createOperator(String sql) throws Exception{
		// TODO Auto-generated method stub
		this.sql = sql;
		Map<String,Object> result = GrokUtil.toMap(pattern, sql);
		this.name = (String)result.get("name");
		setFieldsAndFieldTypes((String)result.get("fields"));
		setTypeAndProperties((String)result.get("properties"));
		return true;
	}

	
	private void setFieldsAndFieldTypes(String sql){
		String[] strs = sql.trim().split(",");
		this.fields = new String[strs.length];
		this.fieldTypes = new Class<?>[strs.length];
		for(int i=0;i<strs.length;i++){
			String[] ss = strs[i].trim().split("\\s+");
			this.fields[i] = ss[0].trim();
			this.fieldTypes[i] = ClassUtil.stringConvetClass(ss[1].trim());
		}
	}
	
	private void setTypeAndProperties(String sql){
		String[] strs = sql.trim().split(",");
		this.properties = new Properties();
        for(int i=0;i<strs.length;i++){
        	String[] ss = strs[i].split("=");
        	String key = ss[0].trim();
        	if("type".equals(key)){
        		this.type = ss[1].trim().replaceAll("'", "");
        	}else{
        		this.properties.put(key, ss[1].trim().replaceAll("'", ""));
        	}
        }
	}
	
	@Override
	public void verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		if(GrokUtil.isSuccess(pattern, sql)){
			throw new SqlVerificationException("create result");
		}
	}
	
	public static boolean verific(String sql) throws Exception{
		return GrokUtil.isSuccess(pattern, sql);
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


	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return this.sql.trim();
	}
	
}
