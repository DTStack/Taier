package com.dtstack.engine.sparkk8s.parser;

import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.exception.RdosDefineException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddJarOperator {
	
	private static Pattern pattern = Pattern.compile("^(?!--)(?i)\\s*add\\s+jar\\s+with\\s+(\\S+)(\\s+AS\\s+(\\S+))?");

	public static JarFileInfo parseSql(String sql) {

		Matcher matcher = pattern.matcher(sql);
		if(!matcher.find()){
			throw new RdosDefineException("not a addJar operator:" + sql);
		}

		JarFileInfo jarFileInfo = new JarFileInfo();
		jarFileInfo.setJarPath(matcher.group(1));

		if(matcher.groupCount() == 3){
			jarFileInfo.setMainClass(matcher.group(3));
		}

		return jarFileInfo;
	}

	public static boolean verific(String sql){
		return pattern.matcher(sql).find();
	}

	/*
	 * handle add jar statements and comment statements on the same line
	 * " --desc \n\n ADD JAR WITH xxxx"
	 */
	public static String handleSql(String sql) {
		String[] sqls = sql.split("\\n");
		for (String s: sqls) {
			if (verific(s)) {
				return s;
			}
		}
		return sql;
	}

}
