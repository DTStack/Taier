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

package com.dtstack.taier.develop.service.template.hive;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.enums.develop.FileType;
import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;
import java.util.Map;

/**
 * @author sanyue
 * @date 2018/9/13
 */
public class Hive2XWriterParam extends DaPluginParam {

    protected String table;
    protected String path = "";
    protected String defaultFS;
    protected String fileType = FileType.ORCFILE.getVal();
    protected String encoding = "utf-8";
    protected String fieldDelimiter = "\001";
    protected Map<String,Object> hadoopConfig;
    protected long interval;
    protected List column;
    protected String writeMode;
    protected String fileName;
    protected String partition;

    protected String password;
    protected String username;
    protected String schema;
    protected String jdbcUrl;
    protected String tablesColumn;
    protected String hdfsWriter;
    protected String analyticalRules;
    protected Integer maxFileSize;
    protected JSONObject distributeTable;
    protected int partitionType;
    protected String remoteDir;
    protected Map<String, Object> sftpConf;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDefaultFS() {
        return defaultFS;
    }

    public void setDefaultFS(String defaultFS) {
        this.defaultFS = defaultFS;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public Map<String, Object> getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(Map<String, Object> hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
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

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getTablesColumn() {
        return tablesColumn;
    }

    public void setTablesColumn(String tablesColumn) {
        this.tablesColumn = tablesColumn;
    }

    public String getHdfsWriter() {
        return hdfsWriter;
    }

    public void setHdfsWriter(String hdfsWriter) {
        this.hdfsWriter = hdfsWriter;
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

    public JSONObject getDistributeTable() {
        return distributeTable;
    }

    public void setDistributeTable(JSONObject distributeTable) {
        this.distributeTable = distributeTable;
    }

    public int getPartitionType() {
        return partitionType;
    }

    public void setPartitionType(int partitionType) {
        this.partitionType = partitionType;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public Map<String, Object> getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(Map<String, Object> sftpConf) {
        this.sftpConf = sftpConf;
    }

}
