package com.dtstack.rdos.engine.execution.base.operator;

import java.util.Map;

import com.dtstack.rdos.common.util.GrokUtil;


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
	private static String pattern = "ADDJAR";
	
	private String jarPath;

	public String getJarPath() {
		return jarPath;
	}


	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}


	@Override
	public boolean createOperator(String sql)throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> result =GrokUtil.toMap(pattern, sql);
		this.jarPath = (String)result.get("path");
		return true;
	}


	@Override
	public boolean verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		return GrokUtil.isSuccess(pattern,sql);
	}
	
}
