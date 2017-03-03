package com.dtstack.rdos.engine.execution.base.operator;

import java.util.Map;

import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.exception.SqlVerificationException;

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
	

	@Override
	public boolean createOperator(String sql) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> result = GrokUtil.toMap(pattern, sql);
		this.name = (String)result.get("name");
		this.className = (String)result.get("className");
		this.type = (String)result.get("type");
		return true;
	}

	@Override
	public void verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		if(GrokUtil.isSuccess(pattern, sql)){
			throw new SqlVerificationException("create function");
		}
	}
	
	public static boolean verific(String sql) throws Exception{
		return GrokUtil.isSuccess(pattern, sql);
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
	

}
