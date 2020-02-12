package com.dtstack.engine.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * 
 *
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class PublicUtil {
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
	    //允许出现不识别的字段
	    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
    }
	
	public static <T> T mapToObject(Map<String,Object> params,Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		return  objectMapper.readValue(objectMapper.writeValueAsBytes(params),clazz);
	}

	public static <T> T jsonStrToObject(String jsonStr, Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		return  objectMapper.readValue(jsonStr, clazz);
	}


	@SuppressWarnings("unchecked")
	public static Map<String,Object> ObjectToMap(Object obj) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		
		return objectMapper.readValue(objectMapper.writeValueAsBytes(obj), Map.class);
	}

	public static String objToString(Object obj) throws IOException {
		return objectMapper.writeValueAsString(obj);
	}
	
	
	public static boolean count(int index,int multiples){
		return index%multiples==0;
	}
	
	public static Object ClassConvter(Class<?> clazz,Object obj){
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
	
	public static Properties stringToProperties(String str) throws IOException{
	   Properties properties = new Properties();
	   properties.load(new ByteArrayInputStream(str.getBytes("UTF-8")));
	   return properties;
	}

}
