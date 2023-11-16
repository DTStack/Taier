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

package com.dtstack.taier.scheduler.datasource.convert.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
}
