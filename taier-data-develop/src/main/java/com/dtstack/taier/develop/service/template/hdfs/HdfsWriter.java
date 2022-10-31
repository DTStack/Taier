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

package com.dtstack.taier.develop.service.template.hdfs;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.job.WriteMode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: zhichen
 * create: 2019/04/17
 */
public class HdfsWriter extends HdfsWriterBase  {

    private String writeMode = WriteMode.APPEND.getVal();
    private String fileName;
    protected String encoding = "utf-8";
    private String jdbcUrl;
    private String table;
    private String partition;
    private String username;
    private String password;
    protected String schema;
    protected String tablesColumn;
    protected String analyticalRules;
    protected Integer maxFileSize;
    protected String distributeTable;

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTablesColumn() {
        return tablesColumn;
    }

    public void setTablesColumn(String tablesColumn) {
        this.tablesColumn = tablesColumn;
    }

    public String getAnalyticalRules() {
        return analyticalRules;
    }

    public void setAnalyticalRules(String analyticalRules) {
        this.analyticalRules = analyticalRules;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getDistributeTable() {
        return distributeTable;
    }

    public void setDistributeTable(String distributeTable) {
        this.distributeTable = distributeTable;
    }

    public String getPartitionType() {
        return partitionType;
    }

    public void setPartitionType(String partitionType) {
        this.partitionType = partitionType;
    }

    protected String partitionType;

//    @Override
    public void checkFormat(JSONObject data) {
        data = data.getJSONObject("parameter");
        JSONObject hadoopConfigMap = data.getJSONObject("hadoopConfig");
        if (hadoopConfigMap == null) {
            throw new RdosDefineException("Hdfs数据源 hadoopConfig不能为空");
        }
        if (StringUtils.isEmpty(hadoopConfigMap.getString("fs.defaultFS"))) {
            throw new RdosDefineException("Hdfs数据源 hadoopConfigMap.fs.defaultFS不能为空");
        }
        String path = data.getString("path");
        if (path == null) {
            throw new RdosDefineException("Hdfs数据源 path不能为空");
        }
    }

    @Override
    public String pluginName() {
        return PluginName.HIVE_W;
    }
}
