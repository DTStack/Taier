package com.dtstack.rdos.engine.entrance.sql;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.engine.entrance.service.paramObject.ParamAction;
import com.dtstack.rdos.engine.execution.base.operator.AddJarOperator;
import com.dtstack.rdos.engine.execution.base.operator.CreateFunctionOperator;
import com.dtstack.rdos.engine.execution.base.operator.CreateResultOperator;
import com.dtstack.rdos.engine.execution.base.operator.CreateSourceOperator;
import com.dtstack.rdos.engine.execution.base.operator.ExecutionOperator;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.ParamsOperator;
import com.dtstack.rdos.engine.execution.exception.RdosException;
import com.google.common.collect.Lists;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class SqlParser {
	
	private static Logger logger = LoggerFactory.getLogger(SqlParser.class);
	
	@SuppressWarnings("unchecked")
	private static List<Class<? extends Operator>> operatorClasses = 
			    Lists.newArrayList(AddJarOperator.class,CreateFunctionOperator.class,CreateSourceOperator.class,CreateResultOperator.class,ExecutionOperator.class);

	public static List<Operator> parser(ParamAction paramAction) throws Exception{
		List<Operator> operators = parserSql(paramAction.getSqlText());
		operators.add(parserParams(paramAction.getTaskParams()));
		return operators;
	}
	
	public static List<Operator> parserSql(String sql) throws Exception{
		sql = sql.trim();
		String[] sqls = sql.split(";");
		List<Operator> operators = Lists.newArrayList();
		A:for(String cql:sqls){
			cql = cql.replaceAll("--.*", "").replaceAll("\r\n", "").replaceAll("\n", "").trim();
			boolean result = false;
			for(Class<? extends Operator> operatorClass :operatorClasses){
				 result = result || (boolean) operatorClass.getMethod("verific", String.class).invoke(null, cql);
			    if(result){
			    	Object obj = operatorClass.newInstance();
			    	operatorClass.getMethod("createOperator", String.class).invoke(obj, cql);
			    	operators.add((Operator) obj);
			    	continue A;
			    }
			}
			if(!result){
				throw new RdosException(String.format("%s:parserSql fail",cql));
			}
		}
		return operators;
	}
	
	public static Operator parserParams(String params) throws Exception{
		ParamsOperator paramsOperator = new ParamsOperator();
		paramsOperator.verification(params);
		paramsOperator.createOperator(params);
		return paramsOperator;
	}

	
	public static void main(String[] args){
		String ss = "--Stream SQL\n"+
"--********************************************************************--\n"+
"--Author: wangliang@dtstack.com\n"+
"--CreateTime: 2016-08-09 10:52:42\n"+
"--Comment: 请输入业务注释信息\n"+"--********************************************************************--\n"+
"CREATE STREAM TABLE student_stream(\n"+
"id BIGINT,\n"+
"name STRING\n"+
") WITH (\n"+
    "type='datahub'\n"+
	"endpoint='http://dh-cn-hangzhou.aliyuncs.com',\n"+
	"accessId='OERGMhXn6H2mBkhk',\n"+
	"accessKey='qnuSKMKoMcY5Va97GGFtL0nvlAoLZx',\n"+
	"projectName='dtstack',\n"+
	"topic='datahub_test'\n"+
");";
		System.out.println(ss.replaceAll("--.*", "").replaceAll("\r\n", "").replaceAll("\n", ""));
		
	}
}
