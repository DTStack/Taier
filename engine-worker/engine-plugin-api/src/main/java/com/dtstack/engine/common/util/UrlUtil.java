package com.dtstack.engine.common.util;

import com.dtstack.engine.common.exception.RdosDefineException;

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
	
	public static String getHttpUrl(String node,String path){
		return String.format("http://%s/%s", node,path);
	}

	/**
	 * 返回URL请求的 ${域名:port}部分
	 * @return
	 */
	public static String getHttpRootUrl(String url){
		Matcher matcher = URLPattern.matcher(url);
		if(!matcher.find()){
			throw new RdosDefineException(String.format("url:%s is not regular HTTP_URL", url));
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
