package com.dtstack.rdos.engine.execution.flink140.parser;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AddJarInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class AddJarOperator{
	
	private static Pattern pattern = Pattern.compile("(?i)\\s*add\\s+jar\\s+with\\s+(\\S+)(\\s+AS\\s+(\\S+))?");

	public static AddJarInfo parseSql(String sql) {

		Matcher matcher = pattern.matcher(sql);
		if(!matcher.find()){
			throw new RdosException("not a addJar operator:" + sql);
		}

		AddJarInfo addJarInfo = new AddJarInfo();
		addJarInfo.setJarPath(matcher.group(1));

		if(matcher.groupCount() == 3){
			addJarInfo.setMainClass(matcher.group(3));
		}

		return addJarInfo;
	}

	public static boolean verific(String sql){
		return pattern.matcher(sql).find();
	}

}
