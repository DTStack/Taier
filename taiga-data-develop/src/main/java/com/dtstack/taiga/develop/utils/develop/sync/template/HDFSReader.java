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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.develop.common.template.Reader;
import com.dtstack.taiga.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taiga.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public class HDFSReader extends HDFSBase implements Reader {

    private String jdbcUrl;
    private String table;
    private String partition;
    private String username;
    private String password;
    private String fileName;

    @Override
    public JSONObject toReaderJson() {

        JSONObject parameter = new JSONObject(true);

        parameter.put("path", this.getPath());
        parameter.put("defaultFS", this.getDefaultFS());
        parameter.put("column", ColumnUtil.getColumns(this.column, PluginName.HDFS_R));
        parameter.put("fileType", this.getFileType());
        parameter.put("fieldDelimiter", this.getFieldDelimiter());
        parameter.put("encoding", this.getEncoding());
        parameter.put("hadoopConfig", this.getHadoopConfig());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());
        parameter.put("sftpConf", this.getSftpConf());
        parameter.put("remoteDir", this.getRemoteDir());

        if(StringUtils.isNotEmpty(partition)) {
            parameter.put("partition", getPartition());
            parameter.put("fileName", getFileName());
        }

        if(StringUtils.isNotEmpty(username)) {
            parameter.put("username", this.getUsername());
        }

        if(StringUtils.isNotEmpty(password)) {
            parameter.put("password", this.getPassword());
        }
        if(StringUtils.isNotEmpty(jdbcUrl)) {
            JSONObject connection = new JSONObject(2);
            connection.put("jdbcUrl", this.getJdbcUrl());
            connection.put("table", StringUtils.isNotBlank(this.getTable()) ? Lists.newArrayList(this.getTable()) : Lists.newArrayList());
            parameter.put("connection", Lists.newArrayList(connection));
        }

        JSONObject reader = new JSONObject(true);

        reader.put("name", PluginName.HDFS_R);
        reader.put("parameter", parameter);

        return reader;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
