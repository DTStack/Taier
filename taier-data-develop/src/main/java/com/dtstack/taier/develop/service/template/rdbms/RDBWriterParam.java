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

package com.dtstack.taier.develop.service.template.rdbms;


import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;

/**
 * 对接前端的字段
 */
public class RDBWriterParam extends DaPluginParam {

    /**
     * 密码
     */
    private String password;
    /**
     * 用户名
     */
    private String username;
    /**
     * jdbcurl
     */
    private String jdbcUrl;
    /**
     * 关系型数据库实时采集类型 1 binlog | 2 间隔轮询
     */
    private Integer rdbmsDaType;

    /**
     * 增量标识字段
     */
    private String increColumn;

    /**
     * 采集起点
     */
    private String startLocation;

    /**
     * schema
     */
    private String schema;

    /**
     * 轮询时间间隔
     */
    private Long pollingInterval;

    /**
     * 表名
     */
    private String table;
    /**
     * 采集字段
     */
    private List<String> tableFields;
    private List column;
    private String preSql;
    private String postSql;
    private String writeMode;

    private List<Long> sourceIds;

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

    public Integer getRdbmsDaType() {
        return rdbmsDaType;
    }

    public void setRdbmsDaType(Integer rdbmsDaType) {
        this.rdbmsDaType = rdbmsDaType;
    }

    public String getIncreColumn() {
        return increColumn;
    }

    public void setIncreColumn(String increColumn) {
        this.increColumn = increColumn;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(Long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getTableFields() {
        return tableFields;
    }

    public void setTableFields(List<String> tableFields) {
        this.tableFields = tableFields;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

    public String getPreSql() {
        return preSql;
    }

    public void setPreSql(String preSql) {
        this.preSql = preSql;
    }

    public String getPostSql() {
        return postSql;
    }

    public void setPostSql(String postSql) {
        this.postSql = postSql;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    @Override
    public List<Long> getSourceIds() {
        return sourceIds;
    }

    @Override
    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }
}

