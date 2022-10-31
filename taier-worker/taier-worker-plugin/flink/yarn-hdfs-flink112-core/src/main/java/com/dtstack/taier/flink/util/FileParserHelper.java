/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.flink.util;

import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author xiuzhu
 */
public class FileParserHelper {

	/**
	 * todo: add java doc
	 */
	private static final Pattern jarFilePattern = Pattern.compile("^(?!--)(?i)\\s*add\\s+jar\\s+with\\s+(\\S+)(\\s+AS\\s+(\\S+))?");

	/**
	 * todo: add java doc
	 */
	private static final Pattern resourceFilePattern = Pattern.compile("^(?!--)(?i)\\s*add\\s+file\\s+with\\s+(\\S+)?(\\s+rename\\s+(\\S+))?");

	public static JarFileInfo parseJarFile(String sql) {

		Matcher matcher = jarFilePattern.matcher(sql);
		if(!matcher.find()){
			throw new PluginDefineException("not a addJar operator:" + sql);
		}

		JarFileInfo jarFileInfo = new JarFileInfo();
		jarFileInfo.setJarPath(matcher.group(1));

		if(matcher.groupCount() == 3){
			jarFileInfo.setMainClass(matcher.group(3));
		}

		return jarFileInfo;
	}

	public static boolean verifyJar(String sql){
		return jarFilePattern.matcher(sql).find();
	}

	public static File getResourceFile(String sql) {
		Matcher matcher = resourceFilePattern.matcher(sql);
		if(!matcher.find()){
			throw new PluginDefineException("Get Resource File Error: " + sql);
		}
		String filePath = matcher.group(1);
		return new File(filePath);
	}

	public static String getResourceFileName(String sql) {
		Matcher matcher = resourceFilePattern.matcher(sql);
		if(!matcher.find()){
			throw new PluginDefineException("Get Resource File Name Error: " + sql);
		}

		String fileName = matcher.group(3);
		if (StringUtils.isBlank(fileName)) {
			fileName = getResourceFile(sql).getName();
		}
		return fileName;
	}

	public static boolean verifyResource(String sql){
		return resourceFilePattern.matcher(sql).find();
	}

	/**
	 * handle add jar statements and comment statements on the same line
	 * " --desc \n\n ADD JAR WITH xxxx"
	 */
	public static String handleSql(String sql) {
		String[] sqls = sql.split("\\n");
		for (String s: sqls) {
			if (verifyJar(s)) {
				return s;
			}

			if (verifyResource(s)) {
				return s;
			}
		}
		return sql;
	}


}
