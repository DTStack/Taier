package com.dtstack.engine.master.utils;


import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class PublicUtil {
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static <T> T objectToObject(Object params,Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		if(params ==null) {return null;}
		return  objectMapper.readValue(objectMapper.writeValueAsBytes(params),clazz);
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> objectToMap(Object obj) throws IOException{
		if(obj ==null){
			return null;
		}

		return objectMapper.readValue(objectMapper.writeValueAsBytes(obj), Map.class);
	}

	public static Map<String, Object> strToMap(String str) throws  IOException{
		if(str ==null){
			return null;
		}

		return objectMapper.readValue(str, Map.class);
	}

	public static <T> T strToObject(String str,Class<T> classzz) throws  IOException{
		if(str ==null){
			return null;
		}

		return objectMapper.readValue(str,classzz);
	}

	public static String objectToStr(Object object) throws  IOException{
		if(object ==null){
			return null;
		}

		return objectMapper.writeValueAsString(object);
	}

	public static <T> T strToObject(String str, TypeReference valueTypeRef) throws  IOException{
		if(str ==null){
			return null;
		}

		return objectMapper.readValue(str, valueTypeRef);
	}

	public static List<Object> objectToList(String str) throws IOException {
		if(str == null){
		    return null;
        }

		return objectMapper.readValue(str, List.class);
	}

	public static boolean count(int index,int multiples){
		return index%multiples==0;
	}

	public static  String upperFirstLetter(String word){
		return word.substring(0, 1).toUpperCase() + word.substring(1);
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
			obj = Integer.parseInt(obj.toString());
		}else if(clazz.equals(Long.class)|| long.class.equals(clazz)){
			obj = Long.parseLong(obj.toString());
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

	
    public static String convertDelimiter(String in) {
        String line = in;
        line = line.replaceAll("\\\\t", "\t");
        line = line.replaceAll("\\\\n", "\n");
        line = line.replaceAll("\\\\r", "\r");
        line = line.replaceAll("\\\\\\\\", "\\\\");

        String pattern = "\\\\(\\d{3})";
        Pattern r = Pattern.compile(pattern);
        while(true) {
            Matcher m = r.matcher(line);
            if(!m.find()){
				break;
			}
            String num = m.group(1);
            int x = Integer.parseInt(num, 8);
            line = m.replaceFirst(String.valueOf((char)x));
        }
        return line;
    }


	public static List<Integer> getConvertSendType(int type) {
		String hexType = new StringBuffer(Integer.toHexString(type)).reverse().toString();
		char[] buf = hexType.toCharArray();
		List<Integer> types = new ArrayList<>();
		for (int i = 0; i < buf.length; i++) {
			int cint = Character.getNumericValue(buf[i]);
			if (cint != 0) {
				types.add(i + 1);
			}
		}

		return types;
	}

}
