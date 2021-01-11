package com.dtstack.engine.common.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.Map;

/**
 * Date: 2020/7/21
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class JsonUtils {
    public static ObjectMapper mapper = new ObjectMapper();


    public static Map<String, Object> parseMap(String json) {
        try {
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
