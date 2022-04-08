package com.dtstack.taier.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.PubSvcDefineException;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Reason: 类转化工具
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 */
public class PublicUtil {
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> objectToMap(Object obj) throws IOException{
		if(obj ==null){
			return null;
		}

		return objectMapper.readValue(objectMapper.writeValueAsBytes(obj), Map.class);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> strToMap(String str) throws  IOException{
		if(str ==null){
			return null;
		}

		return objectMapper.readValue(str, Map.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T strToObject(String str,Class<T> classzz) throws  IOException{
		if(str ==null){
			return null;
		}

		return objectMapper.readValue(str,classzz);
	}

	@SuppressWarnings("unchecked")
	public static String objectToStr(Object object) throws  IOException{
		if(object ==null){
			return null;
		}

		return objectMapper.writeValueAsString(object);
	}

	@SuppressWarnings("unchecked")
	public static <T> T strToObject(String str, TypeReference valueTypeRef) throws  IOException{
		if(str ==null){
			return null;
		}

		return objectMapper.readValue(str, valueTypeRef);
	}

	@SuppressWarnings("unchecked")
	public static List<Object> objectToList(String str) throws IOException {
		if(str == null){
		    return null;
        }

		return objectMapper.readValue(str, List.class);
	}

	public static <T> T mapToObject(Map<String, Object> params, Class<T> clazz) {
		try {
			return objectMapper.readValue(objectMapper.writeValueAsBytes(params), clazz);
		} catch (IOException e) {
			throw new PubSvcDefineException(String.format("对象转换异常:%s", e.getMessage()), e);
		}
	}

	public static <T> T objectToObject(Object params, Class<T> clazz) {
		try {
			return params == null ? null : objectMapper.readValue(objectMapper.writeValueAsBytes(params), clazz);
		} catch (IOException e) {
			throw new PubSvcDefineException(String.format("对象转换异常:%s", e.getMessage()), e);
		}
	}

	public static Properties stringToProperties(String str) throws IOException{
		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(str.getBytes("UTF-8")));
		return properties;
	}


	public static boolean count(int index,int multiples){
		return index%multiples==0;
	}


	public static boolean matcher(String source,String pattern){
		return Pattern.compile(pattern).matcher(source).matches();
	}
	
	public static boolean isJavaBaseType(Class<?> clazz){
		if(Integer.class.equals(clazz) || int.class.equals(clazz)){
			return true;
		}
		if(Long.class.equals(clazz) || long.class.equals(clazz)){
			return true;
		}
		if(Double.class.equals(clazz) || double.class.equals(clazz)){
			return true;
		}		
		if(Float.class.equals(clazz) || float.class.equals(clazz)){
			return true;
		}		
		if(Byte.class.equals(clazz) || byte.class.equals(clazz)){
			return true;
		}		
		if(Short.class.equals(clazz) || short.class.equals(clazz)){
			return true;
		}	
	    if(clazz.equals(Boolean.class)||boolean.class.equals(clazz)){
	    	return true;
	    }
		if(String.class.equals(clazz)){
			return true;
		}		
        return false;		
	}
	
	
	public static Object ClassConvter(Class<?> clazz,Object obj){
		if(obj ==null) {return null;}
		if(clazz.equals(Integer.class)||int.class.equals(clazz)){
			if (StringUtils.isNotEmpty(obj.toString())){
				obj = Integer.parseInt(obj.toString());
			}else {
				obj = null;
			}
		}else if(clazz.equals(Long.class)|| long.class.equals(clazz)){
			if (StringUtils.isNotEmpty(obj.toString())){
				obj = Long.parseLong(obj.toString());
			}else {
				obj = null;
			}
		}else if(clazz.equals(Double.class)|| double.class.equals(clazz)){
			obj = Double.parseDouble(obj.toString());
		}else if(clazz.equals(Float.class)|| float.class.equals(clazz)){
			obj = Float.parseFloat(obj.toString());
		}else if(clazz.equals(Byte.class)|| byte.class.equals(clazz)){
			obj = Byte.parseByte(obj.toString());
		}else if(clazz.equals(Short.class)|| short.class.equals(clazz)){
			obj = Short.parseShort(obj.toString());
		}else if(clazz.equals(Boolean.class)||boolean.class.equals(clazz)){
			obj = Boolean.parseBoolean(obj.toString());
		}else if(clazz.equals(String.class)){
			obj = obj.toString();
		}
		return obj;
	}

	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) {
				emptyNames.add(pd.getName());
			}
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	public static void copyPropertiesIgnoreNull(Object source, Object target) {
		BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
	}

	public static JSONObject paramToMap(String header) {
		JSONObject jsonObject = new JSONObject();

		List<String> strings = Splitter.on(";").trimResults().splitToList(header);

		for (String param : strings) {
			String[] split1 = param.split("=");
			if (ArrayUtils.isNotEmpty(split1) && split1.length == 2) {
				jsonObject.put(split1[0],split1[1]);
				jsonObject.put(lineToHump(split1[0]),split1[1]);
			}
		}

		return jsonObject;
	}

	public static String lineToHump(String str) {
		Pattern linePattern = Pattern.compile("_(\\w)");
		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static void removeEmptyValue(Map<String, Object> paramMap) {
		Set<String> set = paramMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String str = it.next();
			if (paramMap.get(str) == null) {
				paramMap.remove(str);
				set = paramMap.keySet();
				it = set.iterator();
			}
		}
	}

}
