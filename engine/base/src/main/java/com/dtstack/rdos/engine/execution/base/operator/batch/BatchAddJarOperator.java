package com.dtstack.rdos.engine.execution.base.operator.batch;

import com.dtstack.rdos.common.util.ClassUtil;
import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.exception.SqlVerificationException;
import org.apache.parquet.Strings;

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
	 * ADD JAR WITH xx
	 */
	private static String pattern = "ADDBATCHJAR";
	
	private String jarPath;

	private String mainClass;

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
	public boolean createOperator(String sql)throws Exception {
		Map<String,Object> result = GrokUtil.toMap(pattern, sql);
		String fields = (String)result.get("fields");
		String[] strArray = fields.split(",");
		for(String str : strArray){
            String[] ss = str.trim().split("\\s+");
            String key = ss[0].trim();
            if("jarPath".equalsIgnoreCase(key)){
                jarPath = ss[1].trim();
            }else if("mainClass".equalsIgnoreCase(key)){
                mainClass = ss[1].trim();
            }
        }

		if(Strings.isNullOrEmpty(jarPath) || Strings.isNullOrEmpty(mainClass)){
            throw new SqlVerificationException("add batch jar");
        }
		return true;
	}

	@Override
	public void verification(String sql) throws Exception {
		if(GrokUtil.isSuccess(pattern,sql)){
			throw new SqlVerificationException("add batch jar");
		}
	}
	
	public static boolean verific(String sql) throws Exception{
		return GrokUtil.isSuccess(pattern,sql);
	}
}
