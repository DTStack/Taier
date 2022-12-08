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

package com.dtstack.taier.develop.flink.sql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;

public class GuideTableParamsUtil {
    public static List<JSONObject> deal(List<JSONObject> source, String versionValue) {
        for (JSONObject obj : source) {
            //只有mysql oracle类型采用键值模式填写
            Integer type = obj.getInteger("type");
            if (DataSourceType.MySQL.getVal().equals(type)) {
                convertField(obj);
            } else {
                String columnsText = obj.getString("columnsText");
                obj.put("columns", parseColumnsFromText(columnsText, type, versionValue));
            }
        }
        return source;
    }

    private static void convertField(JSONObject sourceMeta) {
        if (!sourceMeta.containsKey("columns")) {
            return;
        }
        JSONArray columns = sourceMeta.getJSONArray("columns");
        if (Objects.isNull(columns) || columns.size() <= 0) {
            return;
        }
        JSONArray formatColumns = new JSONArray();
        for (int i = 0; i < columns.size(); i++) {
            JSONObject columnJson = columns.getJSONObject(i);
            String type = columnJson.getString("type");
            columnJson.put("type", StringUtils.replace(type, " ", ""));
            formatColumns.add(columnJson);
        }
        sourceMeta.put("columns", formatColumns);
    }

    private static List<JSONObject> parseColumnsFromText(String columnsText, Integer dataSourceType, String versionValue) {
        List<JSONObject> list = Lists.newArrayList();
        List<String> check = Lists.newArrayList();
        if (StringUtils.isBlank(columnsText)) {
            return list;
        }
        String[] columns = columnsText.split("\n");
        for (String column : columns) {
            if (StringUtils.isNotBlank(column)) {
                if (FlinkVersion.FLINK_112.getVersion().equals(versionValue) && DataSourceType.HBASE.getVal().equals(dataSourceType)) {
                    // 1. flink 1.12 2. 数据源为 hbase
                    JSONObject obj = new JSONObject();
                    obj.put("column", column);
                    list.add(obj);
                } else {
                    //根据空格分隔字段名和类型
                    String[] s = column.trim().split("\\s+", 2);
                    if (s.length == 2) {
                        if (check.contains(s[0].trim())) {
                            throw new DtCenterDefException(String.format("field name:[%s] fill in the repeat", column));
                        } else {
                            check.add(s[0].trim());
                        }
                        JSONObject obj = new JSONObject();
                        obj.put("column", s[0].trim());
                        obj.put("type", s[1].trim());
                        list.add(obj);
                    } else {
                        throw new DtCenterDefException(String.format("field information:[%s] fill in the wrong", column));
                    }
                }
            }
        }
        return list;
    }
}
