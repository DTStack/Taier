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


import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.pluginapi.pojo.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2019/12/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class ImpalaHdfsBase extends HDFSBase {
    /**
     * remoteDir
     * sftpConf
     * 用于kerberosConfig  如果hdfs没有开启 kerberos 无需配置
     * impala 暂时不支持这两个参数
     */

    protected String partition;
    protected String writeMode;
    protected String password;
    protected String username;
    protected String jdbcUrl;
    private String table;
    protected JSONObject kerberosConfig;

    protected List<Column> allColumns = new ArrayList<>();
    protected List<Column> partitionColumns = new ArrayList<>();
    protected List<String> fullColumnNames = new ArrayList<>();
    protected List<String> fullColumnTypes = new ArrayList<>();

    protected String fileName = "";

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public JSONObject getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(JSONObject kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public List<Column> getAllColumns() {
        return allColumns;
    }

    public void setAllColumns(List<Column> allColumns) {
        this.allColumns = allColumns;
    }

    public List<Column> getPartitionColumns() {
        return partitionColumns;
    }

    public void setPartitionColumns(List<Column> partitionColumns) {
        this.partitionColumns = partitionColumns;
    }

    public List<String> getFullColumnNames() {
        return fullColumnNames;
    }

    public void setFullColumnNames(List<String> fullColumnNames) {
        this.fullColumnNames = fullColumnNames;
    }

    public List<String> getFullColumnTypes() {
        return fullColumnTypes;
    }

    public void setFullColumnTypes(List<String> fullColumnTypes) {
        this.fullColumnTypes = fullColumnTypes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
