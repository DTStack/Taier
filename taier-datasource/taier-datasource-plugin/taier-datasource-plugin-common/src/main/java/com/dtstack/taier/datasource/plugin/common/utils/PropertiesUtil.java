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

package com.dtstack.taier.datasource.plugin.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Properties;

/**
 * properties 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午3:25 2022/1/21
 * company: www.dtstack.com
 */
public class PropertiesUtil {

    public static Properties convertToProp(RdbmsSourceDTO rdbmsSourceDTO) {
        return convertToProp(rdbmsSourceDTO, null, null);
    }

    public static Properties convertToProp(RdbmsSourceDTO rdbmsSourceDTO, Properties properties) {
        return convertToProp(rdbmsSourceDTO, properties, null);
    }

    public static Properties convertToProp(RdbmsSourceDTO rdbmsSourceDTO, Properties properties, String prefix) {
        if (Objects.isNull(properties)) {
            properties = new Properties();
        }

        if (StringUtils.isNotBlank(rdbmsSourceDTO.getUsername())) {
            properties.setProperty("user", rdbmsSourceDTO.getUsername());
        }

        if (StringUtils.isNotBlank(rdbmsSourceDTO.getPassword())) {
            properties.setProperty("password", rdbmsSourceDTO.getPassword());
        }

        if (ReflectUtil.fieldExists(RdbmsSourceDTO.class, "properties") && StringUtils.isNotBlank(rdbmsSourceDTO.getProperties())) {
            JSONObject propertiesJson = JSONUtil.parseJsonObject(rdbmsSourceDTO.getProperties());
            for (String key : propertiesJson.keySet()) {
                String value = propertiesJson.getString(key);
                if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(prefix)) {
                    String newKey = key.startsWith(prefix) ? key : prefix + key;
                    properties.setProperty(newKey, value);
                }
            }
        }

        return properties;
    }

    public static void setIgnoreEmpty(Properties properties, String key, String value) {
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
            properties.setProperty(key, value);
        }
    }
}
