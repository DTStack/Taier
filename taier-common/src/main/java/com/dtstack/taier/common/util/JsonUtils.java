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
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Date: 2020/7/21
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class JsonUtils {
    public static ObjectMapper mapper = new ObjectMapper();

    public static Logger LOG = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T objectToObject(Object params, Class<T> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(params), clazz);
    }

    public static String objectToStr(Object object) {
        return JSONObject.toJSONString(object);
    }


    public static String formatJSON(String json) {
        String formatJson;
        try {
            formatJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readValue(json, Object.class));
        } catch (Exception e) {
            LOG.warn("JOSN解析失败:{}", json, e);
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
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        return formatJson;
    }

    public static String getStrFromJson(JSONObject dataJson, String key) {
        Objects.requireNonNull(dataJson);
        Objects.requireNonNull(key);
        return dataJson.containsKey(key) ? dataJson.getString(key) : "";
    }

    public static Map<String, Object> objectToMap(Object obj) {
        return JSONObject.parseObject(JSONObject.toJSONString(obj));
    }
}
