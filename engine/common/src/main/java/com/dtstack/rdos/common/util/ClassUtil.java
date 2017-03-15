package com.dtstack.rdos.common.util;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ClassUtil {
	
	public static Class<?> stringConvetClass(String str){
		switch(str.toLowerCase()){
		  case "int":return Integer.class;

		  case "bigint":return Long.class;

		  case "byte":return Byte.class;

		  case "short":return Short.class;

		  case "string":return String.class;

		  case "float":return Float.class;

		  case "double":return Double.class;
		}
		return null;
	}
}
