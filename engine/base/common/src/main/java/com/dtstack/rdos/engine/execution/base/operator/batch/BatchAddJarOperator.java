package com.dtstack.rdos.engine.execution.base.operator.batch;

import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import org.apache.commons.lang3.StringUtils;

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
	public void createOperator(String sql)throws Exception {
		// TODO Auto-generated method stub
		this.sql = sql;
		String uppserSql = StringUtils.upperCase(sql);
		Map<String,Object> result =GrokUtil.toMap(pattern, uppserSql);
		this.jarPath = (String)result.get("path");
		this.mainClass = (String)result.get("mainClass");
	}
	
	public boolean verific(String sql) throws Exception{
		String uppserSql = StringUtils.upperCase(sql);
		return GrokUtil.isSuccess(pattern, uppserSql);
	}

	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return this.sql.trim();
	}
}
