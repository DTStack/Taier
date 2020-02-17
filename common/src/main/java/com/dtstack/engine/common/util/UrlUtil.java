package com.dtstack.engine.common.util;

import com.dtstack.engine.common.exception.RdosException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Reason: TODO ADD REASON(可选)
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
			throw new RdosException(String.format("url:%s is not regular HTTP_URL", url));
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

}
