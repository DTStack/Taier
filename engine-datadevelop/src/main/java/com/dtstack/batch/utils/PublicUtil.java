package com.dtstack.batch.utils;

import com.dtstack.batch.common.exception.PubSvcDefineException;
import com.google.common.collect.Sets;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
 */
public class PublicUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(PublicUtil.class);

    static {
        //允许出现不识别的字段
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
    }

    public static <T> T mapToObject(Map<String, Object> params, Class<T> clazz) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsBytes(params), clazz);
        } catch (IOException e) {
            throw new PubSvcDefineException(String.format("对象转换异常:%s", e.getMessage()), e);
        }
    }

    public static Map<String, Object> objectToMap(Object obj) {
        try {
            return obj == null ? new HashMap<>() : (Map) objectMapper.readValue(objectMapper.writeValueAsBytes(obj), Map.class);
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


    public static <T> T jsonStrToObject(String jsonStr, Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException {
        return objectMapper.readValue(jsonStr, clazz);
    }

    public static String objToString(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }


    public static boolean count(int index, int multiples) {
        return index % multiples == 0;
    }

    public static Object classConvert(Class<?> clazz, Object obj) {
        if (clazz.equals(Integer.class) || int.class.equals(clazz)) {
            obj = Integer.parseInt(obj.toString());
        } else if (clazz.equals(Long.class) || long.class.equals(clazz)) {
            obj = Long.parseLong(obj.toString());
        } else if (clazz.equals(Double.class) || double.class.equals(clazz)) {
            obj = Double.parseDouble(obj.toString());
        } else if (clazz.equals(Float.class) || float.class.equals(clazz)) {
            obj = Float.parseFloat(obj.toString());
        } else if (clazz.equals(Byte.class) || byte.class.equals(clazz)) {
            obj = Byte.parseByte(obj.toString());
        } else if (clazz.equals(Short.class) || short.class.equals(clazz)) {
            obj = Short.parseShort(obj.toString());
        } else if (clazz.equals(Boolean.class) || boolean.class.equals(clazz)) {
            obj = Boolean.parseBoolean(obj.toString());
        } else if (clazz.equals(String.class)) {
            obj = obj.toString();
        }
        return obj;
    }

    public static Properties stringToProperties(String str) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(str.getBytes("UTF-8")));
        return properties;
    }

    /**
     * 格式化double
     *
     * @param value
     * @return
     */
    public static Double formatDouble(Double value) {

        if (value == null) {
            return 0.0;
        }

        BigDecimal bg = new BigDecimal(value);
        /**
         * 参数：
         newScale - 要返回的 BigDecimal 值的标度。
         roundingMode - 要应用的舍入模式。
         返回：
         一个 BigDecimal，其标度为指定值，其非标度值可以通过此 BigDecimal 的非标度值乘以或除以十的适当次幂来确定。
         */
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public static Long getLongVal(Long value) {
        if (value == null) {
            return 0L;
        }
        return value;
    }

    public static boolean checkIntersection(Set<String> firstSet, Set<String> secondSet) {
        Set<Object> totalSet = Sets.newHashSet();
        totalSet.addAll(firstSet);
        totalSet.addAll(secondSet);
        if (totalSet.size() == firstSet.size() + secondSet.size()) {
            return false;
        }
        return true;
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
