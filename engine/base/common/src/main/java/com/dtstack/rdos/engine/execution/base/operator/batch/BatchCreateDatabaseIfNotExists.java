package com.dtstack.rdos.engine.execution.base.operator.batch;

import java.util.Map;
import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author sishu.yss
 *
 */
public class BatchCreateDatabaseIfNotExists implements Operator{
	
	//CREATE DATABASE IF NOT EXISTS userdb
	private static String pattern = "BATCHCREATEDATABASEIFNOTEXISTS";
	
	private String sql;
	
	private String name;

	@Override
	public void createOperator(String sql) throws Exception {
		// TODO Auto-generated method stub
		this.sql = sql;
		Map<String,Object> result =GrokUtil.toMap(pattern, sql);
        this.name = (String)result.get("name");
	}

	@Override
    public boolean verific(String sql) throws Exception{
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
}
