package com.dtstack.taier.develop.datasource.convert.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * map 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午8:53 2021/7/5
 * company: www.dtstack.com
 */
public class DtMapUtils {

    /**
     * map -> stringMap, ignore null.
     *
     * @param map 需要转换的 map
     * @return 转换后的 map
     */
    public static Map<String, String> toStringMap(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            return null;
        }
        Map<String, String> afterMap = new HashMap<>();
        map.entrySet().forEach(entry -> {
            if (Objects.nonNull(entry)) {
                afterMap.put(entry.getKey(), entry.getValue().toString());
            }
        });
        return afterMap;
    }

    /**
     * map put 数据，忽略 value 为空的情况
     *
     * @param map   map集合
     * @param key   map-key
     * @param value map-value
     */
    public static void putStringIgnoreBlank(Map<String, Object> map, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
    }

    /**
     * 将Map值转化为String
     *
     * @param map
     * @return
     */
    public static Map<String, String> transferStringValue(Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) {
            return new HashMap<>();
        }

        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> (entry.getValue() == null ? null : entry.getValue().toString())));
    }

    public static String getStrFromJson(JSONObject dataJson, String key) {
        Objects.requireNonNull(dataJson);
        Objects.requireNonNull(key);
        return dataJson.containsKey(key) ? dataJson.getString(key) : "";
    }
}
