package com.dtstack.rdos.engine.execution.base.operator.batch;

import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.exception.SqlVerificationException;

import java.util.Map;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月22日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class BatchAddJarOperator implements Operator{
	
	/**
	 * ADD JAR WITH hdfs://xx.ff AS zbc.df.como.KKK
	 */
	private static String pattern = "BATCHADDJAR";

	
	private String jarPath;

	private String mainClass;
	
	private String sql;

	public String getJarPath() {
		return jarPath;
	}

	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	@Override
	public boolean createOperator(String sql)throws Exception {
		// TODO Auto-generated method stub
		this.sql = sql;
		Map<String,Object> result =GrokUtil.toMap(pattern, sql);
		this.jarPath = (String)result.get("path");
		this.mainClass = (String)result.get("mainClass");
		return true;
	}

	@Override
	public void verification(String sql) throws Exception {
		if(GrokUtil.isSuccess(pattern,sql)){
			throw new SqlVerificationException("add batch jar");
		}
	}
	
	public static boolean verific(String sql) throws Exception{
		return GrokUtil.isSuccess(pattern,sql);
	}

	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return this.sql.trim();
	}
}
