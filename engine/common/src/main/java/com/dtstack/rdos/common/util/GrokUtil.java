package com.dtstack.rdos.common.util;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.google.common.collect.Maps;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class GrokUtil {

	private static Logger logger = LoggerFactory.getLogger(GrokUtil.class);

	private static String patternFile = "pattern";
	
    private static Map<String,Grok> groks = Maps.newConcurrentMap();
        
	static{
		try {
		    Grok grok = new Grok();
			grok.addPatternFromReader(new InputStreamReader(GrokUtil.class.getClassLoader()
					.getResourceAsStream(patternFile)));
			Set<Map.Entry<String, String>> sets = grok.getPatterns().entrySet();
			for(Map.Entry<String, String> entry:sets){
				Grok g = new Grok();
				g.addPatternFromReader(new InputStreamReader(GrokUtil.class.getClassLoader()
					.getResourceAsStream(patternFile)));
				String name = getName(entry.getKey());
				g.compile(name);
				groks.put(name, g);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(ExceptionUtil.getErrorMessage(e));
		}
	}
	
	
	private static String getName(String name){
		return "%{"+name+"}";
	}
	
	public static boolean isSuccess(String name,String text) throws Exception{
		Grok grok = groks.get(getName(name));
		Match match =grok.match(text);
		match.captures();
		return !match.isNull();
	}
	
	public static Map<String,Object> toMap(String name,String text) throws Exception{
		Grok grok = groks.get(getName(name));
		Match match =grok.match(text);
		match.captures();
		return match.toMap();
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
      System.out.println(GrokUtil.isSuccess("ADDJAR","ADD JAR WITH xxxxx;"));
	}

}
