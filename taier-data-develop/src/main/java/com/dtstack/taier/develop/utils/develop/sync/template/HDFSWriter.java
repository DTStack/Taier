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
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.utils.develop.common.enums.StoredType;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.job.WriteMode;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public class HDFSWriter extends HDFSBase implements Writer {

    private String writeMode = WriteMode.APPEND.getVal();
    private String fileName = StoredType.TEXTFILE.getValue();
    private List<String> fullColumnName = new ArrayList<>();
    private List<String> fullColumnType = new ArrayList<>();

    private String jdbcUrl;
    private String table;
    private String partition;
    private String username;
    private String password;

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

    public List<String> getFullColumnName() {
        return fullColumnName;
    }

    public List<String> getFullColumnType() {
        return fullColumnType;
    }

    public void setFullColumnName(List<String> fullColumnName) {
        this.fullColumnName = fullColumnName;
    }

    public void setFullColumnType(List<String> fullColumnType) {
        this.fullColumnType = fullColumnType;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);

        parameter.put("path", this.getPath());
        parameter.put("defaultFS", this.getDefaultFS());
        parameter.put("column", ColumnUtil.getColumns(this.column, PluginName.HDFS_W));
        parameter.put("fileType", this.getFileType());
        parameter.put("fieldDelimiter", this.getFieldDelimiter());
        parameter.put("encoding", this.getEncoding());
        parameter.put("fileName", this.getFileName());
        parameter.put("writeMode", this.getWriteMode());
        parameter.put("hadoopConfig", this.getHadoopConfig());
        parameter.put("sourceIds",getSourceIds());

        if(StringUtils.isNotEmpty(partition)) {
            parameter.put("partition", partition);
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

        if(fullColumnName.size() != 0) {
            parameter.put("fullColumnName", fullColumnName);
        }

        if(fullColumnType.size() != 0) {
            parameter.put("fullColumnType", fullColumnType);
        }
        parameter.putAll(super.getExtralConfigMap());
        parameter.put("sftpConf", this.getSftpConf());
        parameter.put("remoteDir", this.getRemoteDir());

        JSONObject write = new JSONObject(true);

        write.put("name", PluginName.HDFS_W);
        write.put("parameter", parameter);

        return write;
    }

    @Override
    public List<JSONObject> getColumn() {
        List<JSONObject> jsons = super.getColumn();
        if (jsons != null && !jsons.isEmpty()) {
            for (JSONObject json : jsons) {
                json.put("name", json.getString("key"));
                json.remove("key");
            }
        }
        return jsons;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
