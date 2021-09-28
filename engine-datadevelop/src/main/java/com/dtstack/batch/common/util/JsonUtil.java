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

package com.dtstack.batch.common.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:08 2020/2/25
 * @Description：Json补充 工具类
 */
public class JsonUtil {
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
