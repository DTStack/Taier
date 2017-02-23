package com.dtstack.rdos.engine.execution.base.operator;

import java.util.Map;
import java.util.Properties;
import com.dtstack.rdos.common.util.ClassUtil;
import com.dtstack.rdos.common.util.GrokUtil;

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
	 *  CREATE SOURCE TABLE student_stream(
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
	private static String pattern ="CREATESOURCE";
	
	private Properties properties;
	
	private String[] fields;
	
	private Class<?>[] fieldTypes;
	
	private String name;
	
	private String type;

	@Override
	public boolean createOperator(String sql) throws Exception{
		// TODO Auto-generated method stub
		Map<String,Object> result = GrokUtil.toMap(pattern, sql);
		this.name = (String)result.get("name");
		setFieldsAndFieldTypes((String)result.get("fields"));
		setTypeAndProperties((String)result.get("properties"));
		return true;
	}

	
	private void setFieldsAndFieldTypes(String sql){
		String[] strs = sql.trim().split(",");
		fields = new String[strs.length];
		fieldTypes = new Class<?>[strs.length];
		for(int i=0;i<strs.length;i++){
			String[] ss = strs[i].split("\\s+");
			fields[i] = ss[0].trim();
			fieldTypes[i] = ClassUtil.stringConvetClass(ss[0].trim());
		}
	}
	
	private void setTypeAndProperties(String sql){
		String[] strs = sql.trim().split(",");
		properties = new Properties();
        for(int i=0;i<strs.length;i++){
        	String[] ss = strs[i].split("=");
        	String key = ss[0].trim();
        	if("type".equals(key)){
        		this.type = ss[1].trim().replaceAll("'", "");
        	}else{
        		properties.put(key, ss[1].trim().replaceAll("'", ""));
        	}
        }
	}
	
	@Override
	public boolean verification(String sql) throws Exception {
		// TODO Auto-generated method stub
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

}
