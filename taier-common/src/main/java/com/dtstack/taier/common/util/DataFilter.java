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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class DataFilter {

    public static List<String> PASSWORD_KEYS = Arrays.asList("password", "pass", "secretkey", "hadoop.security.group.mapping.ldap.bind.password");

    /**
     * WebSocket 特殊加密规则
     */
    private static final String WEB_SOCKET_PARAMS = "webSocketParams";

    /**
     * json字符串中的密码脱敏
     */
    public static String passwordFilter(String dataStr){
        if (StringUtils.isEmpty(dataStr)){
            return dataStr;
        }

        try{
            JSONObject jsonData = JSONObject.parseObject(dataStr);
            passwordFilter(jsonData);
            return jsonData.toJSONString();
        } catch (Exception e){
            return dataStr;
        }
    }

    public static void passwordFilter(Object data){
        if (data == null){
            return;
        }

        if (data instanceof JSONObject){
            for (String key : ((JSONObject)data).keySet()) {
                Object item = ((JSONObject)data).get(key);
                // WebSocket 参数特殊处理
                if (WEB_SOCKET_PARAMS.equalsIgnoreCase(key)) {
                    ((JSONObject)data).getJSONObject(key).entrySet().forEach(entry -> entry.setValue("******"));
                    continue;
                }

                if (item instanceof JSONObject || item instanceof JSONArray){
                    passwordFilter(item);
                } else if(PASSWORD_KEYS.contains(key.toLowerCase())){
                    ((JSONObject)data).put(key, "******");
                }
            }
        } else if(data instanceof JSONArray){
            for (Object datum : ((JSONArray) data)) {
                passwordFilter(datum);
            }
        }
    }
}
