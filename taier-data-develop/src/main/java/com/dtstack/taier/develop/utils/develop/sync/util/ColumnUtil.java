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

package com.dtstack.taier.develop.utils.develop.sync.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 * @explanation
 * @date 2018/11/27
 */
public class ColumnUtil {

    public static List<JSONObject> getColumns(List<Object> columns, String pluginName) {
        List<JSONObject> cols = new ArrayList<>();

        if (CollectionUtils.isEmpty(columns)) {
            return cols;
        }

        if (columns.get(0) instanceof String) {
            for (Object column : columns) {
                JSONObject col = new JSONObject();
                col.put("name", String.valueOf(column));

                cols.add(col);
            }

            return cols;
        }

        for (Object colObj : columns) {
            JSONObject col = new JSONObject();
            Map<String, Object> column = (Map<String, Object>) colObj;
            if (column.containsKey("key")) {
                col.put("key", column.get("key"));
                col.put("name", column.get("key"));
            }

            if (column.containsKey("type")) {
                col.put("type", column.get("type"));
            }

            if (column.containsKey("index")) {
                col.put("index", column.get("index"));
            }

            if (column.containsKey("isPart")) {
                col.put("isPart", column.get("isPart"));
            }

            if (PluginName.FTP_R.equals(pluginName) || PluginName.HDFS_R.equals(pluginName) || PluginName.AWS_S3_R.equals(pluginName)) {
                Object keyObj = column.get("key");
                if (keyObj != null) {
                    if (keyObj instanceof Integer) {
                        col.put("index", keyObj);
                        col.remove("name");
                    } else if (keyObj instanceof String && NumberUtils.isNumber(String.valueOf(keyObj))) {
                        col.put("index", Integer.parseInt(String.valueOf(keyObj)));
                        col.remove("name");
                    }
                } else if (!column.containsKey("index")) {
                    throw new RdosDefineException("column必须填写名称[key]或索引[index]");
                }
            }

            if (column.containsKey("value")) {
                col.put("value", column.get("value"));
            }

            if (column.containsKey("format")) {
                col.put("format", column.get("format"));
            }
            cols.add(col);
        }
        return cols;
    }
}
