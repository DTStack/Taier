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

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author qianyi
 * date 2021/3/11 10:21
 */
public class MapUtil {

    /**
     * 根据key 以及切割键 获取真正的key，将key 和value放入data中
     *
     * @param key            key
     * @param fieldDelimiter 切割键
     * @param value          值
     * @param data           载体data
     */
    public static void buildMap(String key, String fieldDelimiter, Object value, Map<String, Object> data) {
        String[] split = new String[1];
        if (org.apache.commons.lang3.StringUtils.isBlank(fieldDelimiter)) {
            split[0] = key;
        } else {
            split = key.split(StringUtils.escapeExprSpecialWord(fieldDelimiter));
        }

        if (split.length == 1) {
            data.put(split[0], value);
        } else {
            Map<String, Object> temp = data;
            for (int i = 0; i < split.length - 1; i++) {
                if (temp.containsKey(split[i])) {
                    if (temp.get(split[i]) instanceof HashMap) {
                        temp = (HashMap) temp.get(split[i]);
                    } else {
                        throw new RuntimeException("build map failed ,data is " + JSON.toJSONString(data) + " key is " + key);
                    }
                } else {
                    Map hashMap = new HashMap(2);
                    temp.put(split[i], hashMap);
                    temp = hashMap;
                }
                if (i == split.length - 2) {
                    temp.put(split[split.length - 1], value);
                }
            }
        }
    }

    /**
     * 根据指定的key从map里获取对应的值
     * 如果key不存在 报错
     *
     * @param map            需要解析的map
     * @param key            指定的key  key可以是嵌套的
     * @param fieldDelimiter 嵌套key的分隔符
     */
    public static Object getValueByKey(Map<String, Object> map, String key, String fieldDelimiter) {
        if (MapUtils.isEmpty(map)) {
            throw new RuntimeException(key + " not exist  because map is empty");
        }
        Object o = null;
        String[] split = new String[1];
        if (org.apache.commons.lang3.StringUtils.isBlank(fieldDelimiter)) {
            split[0] = key;
        } else {
            split = key.split(StringUtils.escapeExprSpecialWord(fieldDelimiter));
        }

        Map<String, Object> tempMap = map;
        for (int i = 0; i < split.length; i++) {
            o = getValue(tempMap, split[i]);
            //仅仅代表这个key对应的值是null但是key还是存在的
            if (o == null && i != split.length - 1) {
                throw new RuntimeException(key + " on  [" + JSON.toJSONString(map) + "]  is null");
            }

            if (i != split.length - 1) {
                if (!(o instanceof Map)) {
                    throw new RuntimeException("key " + key + " on " + map + " is not a json");
                }
                tempMap = (Map<String, Object>) o;
            }
        }
        return o;
    }

    private static Object getValue(Map<String, Object> map, String key) {
        if (!map.containsKey(key)) {
            throw new RuntimeException(key + " not exist on  " + JSON.toJSONString(map));
        }
        return map.get(key);
    }

    /**
     * 为map集合添加 key、value - 当 value 不为 null 时添加
     *
     * @param params 参数列表
     * @param key    key
     * @param value  value
     */
    public static void putIfValueNotNull(Map<String, Object> params, String key, Object value) {
        if (Objects.nonNull(params) && Objects.nonNull(value)) {
            params.put(key, value);
        }
    }

    /**
     * 为map集合添加 key、value
     *
     * @param params 参数列表
     * @param key    key
     * @param value  value
     */
    public static void putIfValueNotEmpty(Map<String, Object> params, String key, String value) {
        if (Objects.nonNull(params) && org.apache.commons.lang3.StringUtils.isNotEmpty(value)) {
            params.put(key, value);
        }
    }

    /**
     * 为map集合添加 key、value - 当 value 不为 null 时添加
     *
     * @param params 参数列表
     * @param key    key
     * @param value  value
     */
    public static void putIfValueNotBlank(Map<String, String> params, String key, String value) {
        if (Objects.nonNull(params) && org.apache.commons.lang3.StringUtils.isNotBlank(value)) {
            params.put(key, value);
        }
    }
}
