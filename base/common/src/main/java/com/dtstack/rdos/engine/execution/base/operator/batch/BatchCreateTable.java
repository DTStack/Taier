package com.dtstack.rdos.engine.execution.base.operator.batch;

import java.util.Map;
import java.util.regex.Pattern;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author sishu.yss
 *
 */

public class BatchCreateTable implements Operator{
	
	protected String pattern = "BATCHCREATETABLE";

	private static Pattern tableNamePattern = Pattern.compile("^[0-9a-zA-Z-_]+$");

	private String sql;
	
	private String name;
	
	private String type;

	@Override
	public void createOperator(String sql) throws Exception {
		// TODO Auto-generated method stub
		this.sql = sql;
		Map<String,Object> result =GrokUtil.toMap(pattern, sql);
        this.name = (String)result.get("name");
		if(!tableNamePattern.matcher(this.name).find()){
            throw  new RdosException("table name format error");
		}
        this.type = (String)result.get("type");
	}

	@Override
	public boolean verific(String sql) throws Exception {
		String uppserSql = StringUtils.upperCase(sql);
        return GrokUtil.isSuccess(pattern, uppserSql);
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
