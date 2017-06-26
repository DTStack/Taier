package com.dtstack.rdos.engine.execution.base.operator.stream;

import java.util.Map;
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
public class AddJarOperator implements Operator{
	
	/**
	 * ADD JAR WITH xx
	 */
	private String sql;
	
	private static String pattern = "ADDJAR";
	
	private String jarPath;

	public String getJarPath() {
		return jarPath;
	}


	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}


	@Override
	public void createOperator(String sql)throws Exception {
		// TODO Auto-generated method stub
		this.sql = sql;
		String uppserSql = StringUtils.upperCase(sql);
		Map<String,Object> result =GrokUtil.toMap(pattern, uppserSql);
		this.jarPath = (String)result.get("path");
	}
	
	public  boolean verific(String sql) throws Exception{
		String uppserSql = StringUtils.upperCase(sql);
		return GrokUtil.isSuccess(pattern, uppserSql);
	}


	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return this.sql.trim();
	}
}
