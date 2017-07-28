package com.dtstack.rdos.engine.execution.base.operator.stream;

import java.util.Map;
import java.util.Properties;
import com.dtstack.rdos.common.util.ClassUtil;
import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import org.apache.commons.lang3.StringUtils;

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
	
	private String sql;

	@Override
	public void createOperator(String sql) throws Exception{
		// TODO Auto-generated method stub
		this.sql = sql;
		Map<String,Object> result = GrokUtil.toMap(pattern, sql);
		this.name = (String)result.get("name");
		setFieldsAndFieldTypes((String)result.get("fields"));
		setTypeAndProperties((String)result.get("properties"));
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
		String[] strs = sql.trim().split("'\\s*,");
		this.properties = new Properties();
        for(int i=0;i<strs.length;i++){
        	String[] ss = strs[i].split("=");
        	String key = ss[0].trim();
        	String value = ss[1].trim().replaceAll("'", "").trim();
        	if("type".equals(key)){
        		this.type = value;
        	}else{
        		this.properties.put(key, value);
        	}
        }
	}
	
	public boolean verific(String sql) throws Exception{
		String uppserSql = StringUtils.upperCase(sql);
		return GrokUtil.isSuccess(pattern, uppserSql);
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
