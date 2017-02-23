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
	 * add jar with xx
	 */
	private String jarPath;

	public String getJarPath() {
		return jarPath;
	}


	@Override
	public boolean createOperator(String sql)throws Exception {
		// TODO Auto-generated method stub
		Map<String,Object> result =GrokUtil.toMap( sql);
		jarPath = (String)result.get("path");
		return true;
	}


	@Override
	public boolean verification(String sql) throws Exception {
		// TODO Auto-generated method stub
		return GrokUtil.isSuccess(sql);
	}
	
}
