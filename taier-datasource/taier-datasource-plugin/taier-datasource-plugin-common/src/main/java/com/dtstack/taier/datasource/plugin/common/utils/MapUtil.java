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

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Map 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午9:37 2021/11/9
 * company: www.dtstack.com
 */
public class MapUtil {

    /**
     * 为map集合添加 key、value - 当 value 不为 null 时添加
     *
     * @param params 参数列表
     * @param key    key
     * @param value  value
     */
    public static void putIfValueNotBlank(Map<String, String> params, String key, String value) {
        if (Objects.nonNull(params) && StringUtils.isNotBlank(value)) {
            params.put(key, value);
        }
    }
}
