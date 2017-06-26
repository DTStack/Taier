package com.dtstack.rdos.engine.execution.base.operator.batch;

/**
 * Created by sishu.yss on 2017/4/13.
 */

import java.util.Map;

import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import org.apache.commons.lang3.StringUtils;

public class BatchCreateFunctionOperator implements Operator{

    //CREATE [TEMPORARY] FUNCTION  strip AS 'com.hcr.hadoop.hive.Strip';
    private static String pattern = "BATCHCREATEFUNCTION";

    private String sql ;
    
    private String type;
    
    private String name;
    
    private String className;

    @Override
    public void createOperator(String sql) throws Exception {
        this.sql = sql;
        String uppserSql = StringUtils.upperCase(sql);
		Map<String,Object> result =GrokUtil.toMap(pattern, uppserSql);
		this.type = (String)result.get("type");
        this.name = (String)result.get("name");
        this.className = (String)result.get("className");
    }


    public boolean verific(String sql) throws Exception{
        String uppserSql = StringUtils.upperCase(sql);
        return GrokUtil.isSuccess(pattern, uppserSql);
    }

    public String getSql() {
        return this.sql.trim();
    }


	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}
}
