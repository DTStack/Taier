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

package com.dtstack.taier.datasource.api.dto.tsdb;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Data
public class QueryResult {
    private String metric;
    private Map<String, String> tags;
    private List<String> aggregateTags;
    private LinkedHashMap<Long, Object> dps = new LinkedHashMap<>();
    private Class<?> type;

    private static final Comparator<KeyValue> ORDER_CMP = (keyValue, t1) -> {
        long diff = keyValue.getTimestamp() - t1.getTimestamp();
        return diff == 0 ? 0 : (diff > 0 ? 1 : -1);
    };

    public static final Comparator<KeyValue> REVERSE_ORDER_CMP = (keyValue, t1) -> {
        long diff = keyValue.getTimestamp() - t1.getTimestamp();
        return diff == 0 ? 0 : (diff > 0 ? -1 : 1);
    };

    public List<KeyValue> getOrderDps() {
        return getOrderDps(false);
    }

    /**
     * 获取排序后的数据
     *
     * @param reverse 是否反转排序
     * @return 查询结果
     */
    public List<KeyValue> getOrderDps(boolean reverse) {
        if (dps == null || dps.isEmpty()) {
            return Collections.emptyList();
        }
        List<KeyValue> keyValues = new ArrayList<>(dps.size());
        for (Map.Entry<Long, Object> entry : dps.entrySet()) {
            keyValues.add(new KeyValue(entry.getKey(), entry.getValue()));
        }
        if (reverse) {
            keyValues.sort(REVERSE_ORDER_CMP);
        } else {
            keyValues.sort(ORDER_CMP);
        }
        return keyValues;
    }

    /**
     * tags 转 string
     *
     * @return string tags
     */
    public String tagsToString() {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        Set<String> tagKs = new TreeSet<>(tags.keySet());
        StringBuilder tagsString = new StringBuilder();
        boolean firstTag = true;
        for (String tagK : tagKs) {
            if (firstTag) {
                tagsString.append(tagK).append("$").append(tags.get(tagK));
                firstTag = false;
            } else {
                tagsString.append("$").append(tagK).append("$").append(tags.get(tagK));
            }
        }
        return tagsString.toString();
    }
}
