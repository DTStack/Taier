/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reason: 类转化工具
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class PublicUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> objectToMap(Object obj) throws IOException {
        if (obj == null) {
            return null;
        }

        return objectMapper.readValue(objectMapper.writeValueAsBytes(obj), Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> strToMap(String str) throws IOException {
        if (str == null) {
            return null;
        }

        return objectMapper.readValue(str, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T strToObject(String str, Class<T> classzz) throws IOException {
        if (str == null) {
            return null;
        }

        return objectMapper.readValue(str, classzz);
    }

    @SuppressWarnings("unchecked")
    public static String objectToStr(Object object) throws IOException {
        if (object == null) {
            return null;
        }

        return objectMapper.writeValueAsString(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T strToObject(String str, TypeReference valueTypeRef) throws IOException {
        if (str == null) {
            return null;
        }

        return objectMapper.readValue(str, valueTypeRef);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> objectToList(String str) throws IOException {
        if (str == null) {
            return null;
        }

        return objectMapper.readValue(str, List.class);
    }

    public static <T> T mapToObject(Map<String, Object> params, Class<T> clazz) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsBytes(params), clazz);
        } catch (IOException e) {
            throw new TaierDefineException(String.format("对象转换异常:%s", e.getMessage()), e);
        }
    }

    public static <T> T objectToObject(Object params, Class<T> clazz) {
        try {
            return params == null ? null : objectMapper.readValue(objectMapper.writeValueAsBytes(params), clazz);
        } catch (IOException e) {
            throw new TaierDefineException(String.format("对象转换异常:%s", e.getMessage()), e);
        }
    }

    public static Properties stringToProperties(String str) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(str.getBytes("UTF-8")));
        return properties;
    }

    public static String propertiesRemoveEmpty(String propertiesStr) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(propertiesStr.getBytes("UTF-8")));
        StringBuffer stringBuffer = new StringBuffer();
        for (Object key : properties.keySet()) {
            stringBuffer.append(key).append("=").append(properties.get(key)).append("\r\n");
        }
        return stringBuffer.toString();
    }


    public static boolean count(int index, int multiples) {
        return index % multiples == 0;
    }


    public static boolean matcher(String source, String pattern) {
        return Pattern.compile(pattern).matcher(source).matches();
    }

    public static boolean isJavaBaseType(Class<?> clazz) {
        if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            return true;
        }
        if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            return true;
        }
        if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            return true;
        }
        if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            return true;
        }
        if (Byte.class.equals(clazz) || byte.class.equals(clazz)) {
            return true;
        }
        if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            return true;
        }
        if (clazz.equals(Boolean.class) || boolean.class.equals(clazz)) {
            return true;
        }
        if (String.class.equals(clazz)) {
            return true;
        }
        return false;
    }


    public static Object ClassConvter(Class<?> clazz, Object obj) {
        if (obj == null) {
            return null;
        }
        if (clazz.equals(Integer.class) || int.class.equals(clazz)) {
            if (StringUtils.isNotEmpty(obj.toString())) {
                obj = Integer.parseInt(obj.toString());
            } else {
                obj = null;
            }
        } else if (clazz.equals(Long.class) || long.class.equals(clazz)) {
            if (StringUtils.isNotEmpty(obj.toString())) {
                obj = Long.parseLong(obj.toString());
            } else {
                obj = null;
            }
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
                jsonObject.put(split1[0], split1[1]);
                jsonObject.put(lineToHump(split1[0]), split1[1]);
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

    static {
        //允许出现不识别的字段
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
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


}
