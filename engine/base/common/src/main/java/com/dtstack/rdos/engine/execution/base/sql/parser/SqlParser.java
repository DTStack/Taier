package com.dtstack.rdos.engine.execution.base.sql.parser;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.stream.*;
import com.google.common.collect.Lists;
import com.dtstack.rdos.engine.execution.base.operator.batch.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class SqlParser {
	
//	private static Logger logger = LoggerFactory.getLogger(SqlParser.class);
	
	@SuppressWarnings("unchecked")
	private static List<Class<? extends Operator>> flinkOperatorClasses =
			    Lists.newArrayList(AddJarOperator.class, CreateFunctionOperator.class,
                        CreateSourceOperator.class, CreateResultOperator.class, ExecutionOperator.class);

	@SuppressWarnings("unchecked")
	private static List<Class<? extends Operator>> sparkOperatorClasses =
			Lists.newArrayList(BatchAddJarOperator.class, BatchCanExecuteOperator.class);

	public static List<Operator> parser(int engineType, int computeType,String sql) throws Exception{
		List<Operator> operators = null;
        if(engineType == EngineType.Spark.getVal()&&computeType ==ComputeType.BATCH.getComputeType()){
			operators = parserSql(sql,sparkOperatorClasses);
		}else if(engineType == EngineType.Flink120.getVal()||engineType == EngineType.Flink130.getVal()){
			operators = parserSql(sql,flinkOperatorClasses);
		}
		return operators;
	}
	
	private static List<Operator> parserSql(String sql,List<Class<? extends Operator>> operatorClasses) throws Exception{
		sql = sql.trim();
		String[] sqls = sql.split(";");
		List<Operator> operators = Lists.newArrayList();
		A:for(String cql:sqls){
			cql = cql.replaceAll("--.*", "").replaceAll("\r\n", "").replaceAll("\n", "").trim();
			boolean result = false;
			for(Class<? extends Operator> operatorClass :operatorClasses){
		    	Object obj = operatorClass.newInstance();
		    	String upperCQL = StringUtils.upperCase(cql);
				result = result || (boolean) operatorClass.getMethod("verific", String.class).invoke(obj, upperCQL);
			    if(result){
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
