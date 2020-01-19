package com.dtstack.engine.base.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String,Object> ObjectToMap(Object obj) throws Exception{
        return objectMapper.readValue(objectMapper.writeValueAsBytes(obj), Map.class);
    }

    public static <T> T jsonStrToObject(String jsonStr, Class<T> clazz) throws Exception{
        return  objectMapper.readValue(jsonStr, clazz);
    }
}
