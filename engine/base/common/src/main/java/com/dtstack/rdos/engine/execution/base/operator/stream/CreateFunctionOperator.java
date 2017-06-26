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
public class CreateFunctionOperator implements Operator{
	
	/**
	 * CREATE (TABLE|SCALA) FUNCTION xxx WITH com.dtstack.testFunction
	 */
	private static String pattern = "CREATEFUNCTION";
	
	private String name;

	private String className;
	
	private String type;
	
	private String sql;
	

	@Override
	public void createOperator(String sql) throws Exception {
		// TODO Auto-generated method stub
		this.sql = sql;
		String upperSql = StringUtils.upperCase(sql);
		Map<String,Object> result = GrokUtil.toMap(pattern, upperSql);
		this.name = (String)result.get("name");
		this.className = (String)result.get("className");
		this.type = (String)result.get("type");
	}

	
	public  boolean verific(String sql) throws Exception{
		String uppserSql = StringUtils.upperCase(sql);
		return GrokUtil.isSuccess(pattern, uppserSql);
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
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
