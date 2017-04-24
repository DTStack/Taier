package com.dtstack.rdos.engine.execution.base.operator;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class ParamsOperator implements Operator{
	
	private String sql;
	
	private Properties properties;

	@Override
	public void createOperator(String sql) throws Exception {
		// TODO Auto-generated method stub
		this.sql =sql;
		properties = new Properties(); 
        InputStream   inputStream   =  new  ByteArrayInputStream(sql.trim().getBytes());
        properties.load(inputStream);
	}

	public Properties getProperties() {
		return properties;
	}


	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return this.sql.trim();
	}

	@Override
	public boolean verific(String sql) {
		// TODO Auto-generated method stub
		return StringUtils.isNotBlank(sql);
	}
}
