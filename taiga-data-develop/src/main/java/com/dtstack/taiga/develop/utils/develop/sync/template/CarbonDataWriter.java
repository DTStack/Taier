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

package com.dtstack.taiga.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.common.template.Writer;
import com.dtstack.taiga.develop.utils.develop.sync.job.PluginName;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sanyue
 * @date 2018/11/26
 */
public class CarbonDataWriter extends CarbonDataBase implements Writer {

    private String writeMode;

    private List<Map<String, Object>> column;

    private String partition;

    private String jdbcUrl;


    public String getWriteMode() {
        if(writeMode != null && writeMode.trim().length() != 0) {
            if(writeMode.equalsIgnoreCase("replace")) {
                writeMode = "overwrite";
            } else if(writeMode.equalsIgnoreCase("insert")) {
                writeMode = "append";
            }
        } else {
            writeMode = "overwrite";
        }
        return writeMode;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public List<Map<String, Object>> getColumn() {
        return column;
    }

    public void setColumn(List<Map<String, Object>> column) {
        this.column = column;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject result = new JSONObject(2);
        result.put("name", PluginName.CarbonData_W);

        JSONObject parameter = new JSONObject(7);
        parameter.put("path", getPath());
        parameter.put("hadoopConfig", getHadoopConfig());
        parameter.put("table", getTable());
        parameter.put("database", getDatabase());
        parameter.put("writeMode", getWriteMode());
        parameter.put("column", getColumnList(getColumn()));
        parameter.put("defaultFS", getDefaultFS());
        if (StringUtils.isNotBlank(getPartition())) {
            parameter.put("partition", getPartition());
        }
        parameter.putAll(super.getExtralConfigMap());
        if(StringUtils.isNotEmpty(jdbcUrl)) {
            JSONObject connection = new JSONObject(2);
            connection.put("jdbcUrl", this.getJdbcUrl());
            connection.put("table", StringUtils.isNotBlank(this.getTable()) ? Lists.newArrayList(this.getTable()) : Lists.newArrayList());
            parameter.put("connection", Lists.newArrayList(connection));
        }
        result.put("parameter", parameter);
        return result;
    }

    private List<String> getColumnList(List<Map<String, Object>> columns) {
        List<String> result = new ArrayList<>();
        for (Map<String, Object> map : columns) {
            result.add((String) map.get("key"));
        }
        return result;
    }

    /**
     * "writer": {
     * "name": "carbondatawriter",
     * "parameter": {
     * "path": "hdfs://ns1/user/hive/warehouse/carbon.store1/sb/sb500",
     * "hadoopConfig": {
     * "dfs.ha.namenodes.ns1": "nn1,nn2",
     * "dfs.namenode.rpc-address.ns1.nn2": "rdos2:9000",
     * "dfs.client.failover.proxy.provider.ns1": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider",
     * "dfs.namenode.rpc-address.ns1.nn1": "rdos1:9000",
     * "dfs.nameservices": "ns1",
     * "fs.defaultFS": "hdfs://ns1"
     * },
     * "table": "sb500",
     * "database": "sb",
     * "writeMode": "overwrite",
     * "column": [
     * "a",
     * "b"
     * ]
     * }
     * }
     */


    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
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
                throw new RdosDefineException("目标源的表名不能为空");
            }
            String database = parameter.getString("database");
            if (StringUtils.isEmpty(database)) {
                throw new RdosDefineException("目标源的数据库名不能为空");
            }
            String writeMode = parameter.getString("writeMode");
            if (StringUtils.isEmpty(writeMode)) {
                throw new RdosDefineException("目标源的写入模式不能为空");
            }
            JSONArray columnArray = parameter.getJSONArray("column");
            if (columnArray == null || columnArray.size() == 0) {
                throw new RdosDefineException("目标源的列名列表不能为空");
            }
            for (int i = 0; i < columnArray.size(); i++) {
                String obj = columnArray.getString(i);
                if (StringUtils.isEmpty(obj)) {
                    throw new RdosDefineException("目标源列名格式错误");
                }
            }
        }
    }
}
