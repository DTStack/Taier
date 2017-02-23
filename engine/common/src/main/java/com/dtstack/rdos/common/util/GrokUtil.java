package com.dtstack.rdos.common.util;

import java.io.InputStreamReader;
import java.util.Map;
import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;

/**
 * 
 * @author sishu.yss
 *
 */
public class GrokUtil {

	private static Logger logger = LoggerFactory.getLogger(GrokUtil.class);

	private static String patternFile = "pattern";
	
	private static Grok grok = new Grok();
	
	
	static{
		try {
			grok.addPatternFromReader(new InputStreamReader(GrokUtil.class.getClassLoader()
					.getResourceAsStream(patternFile)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(ExceptionUtil.getErrorMessage(e));
		}
	}
	
	public static boolean isSuccess(String text) throws Exception{
		Match match =grok.match(text);
		match.captures();
		return match.isNull();
	}
	
	public static Map<String,Object> toMap(String text) throws Exception{
		Match match =grok.match(text);
		match.captures();
		return match.toMap();
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
      System.out.println(GrokUtil.toMap("add jar with xxxxx"));
	}

}
