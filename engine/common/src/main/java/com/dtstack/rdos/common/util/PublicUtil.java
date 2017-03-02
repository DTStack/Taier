package com.dtstack.rdos.common.util;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class PublicUtil {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static <T> T mapToObject(Map<String,Object> params,Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		return  objectMapper.readValue(objectMapper.writeValueAsBytes(params),clazz);
	}

}
