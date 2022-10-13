package com.dtstack.taier.develop.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author zhiChen
 * @date 2022/3/4 16:06
 */
public class JsonUtils {
    public static Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonUtils() {
    }

    public static <T> T objectToObject(Object params, Class<T> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(params), clazz);
    }

    public static Map<String, Object> objectToMap(Object obj) {
        return JSONObject.parseObject(JSONObject.toJSONString(obj));
    }

    public static Map<String, Object> strToMap(String str) {
        return JSONObject.parseObject(str);
    }

    public static <T> T strToObject(String str, Class<T> clazz) {
        return JSONObject.parseObject(str, clazz);
    }

    public static String objectToStr(Object object) {
        return JSONObject.toJSONString(object);
    }

    public static List<Object> objectToList(String str) throws IOException {
        return (List) JSONObject.parseObject(str, List.class);
    }

    public static String formatJSON(String json) {
        try {
            String formatJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readValue(json, Object.class));
            return formatJson;
        } catch (Exception var3) {
            LOG.warn("JOSN解析失败:{}", var3);
            return json;
        }
    }

    public static String formatJSON(Object json) {
        try {
            String formatJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            return formatJson;
        } catch (Exception var3) {
            throw new RdosDefineException("JOSN解析失败", var3);
        }
    }

    static {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 获取对应可以的值，如果不存在则直接附空字符串
     *
     * @param json
     * @param key
     * @return
     */
    public static String getStringDefaultEmpty(JSONObject json, String key) {
        return json.containsKey(key) ? json.getString(key) : "";
    }
}
