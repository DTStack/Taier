package com.dtstack.rdos.engine.execution.base.operator.batch;

import java.util.Map;
import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;

/**
 * 
 * @author sishu.yss
 *
 */

public class BatchCreateTable implements Operator{
	
	private static String pattern = "BATCHCREATETABLE";
	
	private String sql;
	
	private String name;
	
	private String type;

	@Override
	public void createOperator(String sql) throws Exception {
		// TODO Auto-generated method stub
		this.sql = sql;
		Map<String,Object> result =GrokUtil.toMap(pattern, sql);
        this.name = (String)result.get("name");
        this.type = (String)result.get("type");
	}

	@Override
	public boolean verific(String sql) throws Exception {
		// TODO Auto-generated method stub
        return GrokUtil.isSuccess(pattern, sql);
	}

	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return this.sql.trim();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
}
