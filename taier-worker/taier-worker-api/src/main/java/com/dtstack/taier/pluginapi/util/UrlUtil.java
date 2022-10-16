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

package com.dtstack.taier.pluginapi.util;

import com.dtstack.taier.pluginapi.exception.PluginDefineException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 *
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class UrlUtil {

	private static Pattern URLPattern = Pattern.compile("^(?:(http[s]?)://)?([^:/\\?]+)(?::(\\d+))?([^\\?]*)\\??(.*)");

	/**
	 * 返回URL请求的 ${域名:port}部分
	 * @return
	 */
	public static String getHttpRootUrl(String url){
		Matcher matcher = URLPattern.matcher(url);
		if(!matcher.find()){
			throw new PluginDefineException(String.format("url:%s is not regular HTTP_URL", url));
		}

		String protocol = matcher.group(1) == null ? "http" : matcher.group(1);
		String hostName = matcher.group(2);
		String port = matcher.group(3);

		if(port == null){
			return protocol + "://" + hostName;
		}else{
			return protocol + "://" + hostName + ":" + port;
		}
	}


	/**
	 *  将url host使用占位符替换
	 * @param url
	 * @return
	 */
	public static String formatUrlHost(String url) {
		Matcher matcher = URLPattern.matcher(url);
		if (!matcher.find()) {
			throw new RuntimeException(String.format("url:%s is not regular HTTP_URL", url));
		}

		String protocol = matcher.group(1) == null ? "http" : matcher.group(1);
		String hostNamePlaceholder = "%s";
		String port = matcher.group(3);

		if (port == null) {
			return protocol + "://" + hostNamePlaceholder;
		} else {
			return protocol + "://" + hostNamePlaceholder + ":" + port;
		}
	}

}
