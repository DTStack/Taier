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

package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.ErrorCode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
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

    public static Logger LOG = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 类转换
     *
     * @param params
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T objectToObject(Object params, Class<T> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(params), clazz);
    }

    /**
     * 类转化为 Map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
        return JSONObject.parseObject(JSONObject.toJSONString(obj));
    }

    /**
     * string 转化为 Map
     *
     * @param str
     * @return
     */
    public static Map<String, Object> strToMap(String str) {
        return JSONObject.parseObject(str);
    }

    /**
     * String 转化为对象
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T strToObject(String str, Class<T> clazz) {
        return JSONObject.parseObject(str, clazz);
    }

    /**
     * 对象转化为 String
     *
     * @param object
     * @return
     */
    public static String objectToStr(Object object) {
        return JSONObject.toJSONString(object);
    }

    /**
     * 字符串转化为数组
     *
     * @param str
     * @return
     * @throws IOException
     */
    public static List<Object> objectToList(String str) throws IOException {
        return JSONObject.parseObject(str, List.class);
    }

    public static String formatJSON(String json) {
        String formatJson;
        try {
            formatJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readValue(json, Object.class));
        } catch (Exception e) {
            LOG.warn("JOSN解析失败:{}", e);
            return json;
        }
        return formatJson;
    }

    public static String formatJSON(Object json) {
        String formatJson;
        try {
            formatJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DtCenterDefException(ErrorCode.JSON_PARSING_FAILED);
        }
        return formatJson;
    }
}
