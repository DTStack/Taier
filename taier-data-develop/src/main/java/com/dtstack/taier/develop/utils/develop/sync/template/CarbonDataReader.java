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

package com.dtstack.taier.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sanyue
 * @date 2018/11/26
 */
public class CarbonDataReader extends CarbonDataBase implements Reader {

    private String filter;

    private List<Map<String, Object>> column;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Map<String, Object>> getColumn() {
        return column;
    }

    public void setColumn(List<Map<String, Object>> column) {
        this.column = column;
    }

    @Override
    public JSONObject toReaderJson() {
        JSONObject result = new JSONObject(2);
        result.put("name", PluginName.CarbonData_R);

        JSONObject parameter = new JSONObject(6);
        parameter.put("path", getPath());
        parameter.put("hadoopConfig", getHadoopConfig());
        parameter.put("table", getTable());
        parameter.put("database", getDatabase());
        parameter.put("filter", getFilter());
        parameter.put("column", getColumnList(getColumn()));
        parameter.put("defaultFS", getDefaultFS());
        parameter.putAll(super.getExtralConfigMap());
        result.put("parameter", parameter);
        return result;
    }



    private List<Map<String, String>> getColumnList(List<Map<String, Object>> columns) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, Object> map : columns) {
            Map<String, String> map1 = new HashMap<>();
            map1.put("name", (String) map.get("key"));
            map1.put("type", (String) map.get("type"));
            result.add(map1);
        }
        return result;
    }

    /**
     * "reader": {
     "name": "carbondatareader",
     "parameter": {
     "path": "hdfs://ns1/user/hive/warehouse/carbon.store1/sb/tb2000",
     "hadoopConfig": {
     "dfs.ha.namenodes.ns1": "nn1,nn2",
     "dfs.namenode.rpc-address.ns1.nn2": "rdos2:9000",
     "dfs.client.failover.proxy.provider.ns1": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider",
     "dfs.namenode.rpc-address.ns1.nn1": "rdos1:9000",
     "dfs.nameservices": "ns1",
     "fs.defaultFS": "hdfs://ns1"
     },
     "table": "tb2000",
     "database": "sb",
     "filter": " b = 100",
     "column": [
     {
     "name": "a",
     "type": "string"
     },
     {
     "name": "b",
     "type": "int"
     }
     ]
     }
     },
     */
    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        JSONObject parameter = data.getJSONObject("parameter");
        if (parameter == null) {
            throw new RdosDefineException("parameter 不能为空");
        } else {
            String path = parameter.getString("path");
            if (StringUtils.isEmpty(path)) {
                throw new RdosDefineException("目标源的表路径不能为空");
            }
            String table = parameter.getString("table");
            if (StringUtils.isEmpty(table)) {
                throw new RdosDefineException("输入源的表名不能为空");
            }
            String database = parameter.getString("database");
            if (StringUtils.isEmpty(database)) {
                throw new RdosDefineException("输入源的数据库名不能为空");
            }
            JSONArray columnArray = parameter.getJSONArray("column");
            if (columnArray == null || columnArray.size() == 0) {
                throw new RdosDefineException("输入源的列名列表不能为空");
            }
            for (int i = 0; i < columnArray.size(); i++) {
                JSONObject obj = columnArray.getJSONObject(i);
                if (obj == null || StringUtils.isEmpty(obj.getString("name")) || StringUtils.isEmpty(obj.getString("type"))) {
                    throw new RdosDefineException("输入源列名格式错误");
                }
            }
        }
    }
}
