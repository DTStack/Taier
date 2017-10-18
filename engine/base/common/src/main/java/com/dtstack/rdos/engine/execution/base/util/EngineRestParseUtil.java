package com.dtstack.rdos.engine.execution.base.util;

import java.util.Map;

/**
 * 
 * @author sishu.yss
 *
 */
public class EngineRestParseUtil {
	
	public static class SparkRestParseUtil{
		
		public final static String ROOT = "/";
		
		public final static String EXCEPTION_IINFO = "";
		
		public  static  Map<String,Map<String,Object>> getAvailSlots(String message){
			
			return null;
		}
		
		public static String getJobMessage(String message){
			return null;
		}
	}
	
	public static class FlinkRestParseUtil{
		
		public final static String SLOTS_INFO = "/index.html#/taskmanagers";
		
		public final static String EXCEPTION_INFO = "/index.html#/jobs/%s/exceptions";
		
		public  static  Map<String,Map<String,Object>> getAvailSlots(String message){
			
			return null;
		}
		
		public static String getJobMessage(String message){
			return null;
		}
		
	}
}
