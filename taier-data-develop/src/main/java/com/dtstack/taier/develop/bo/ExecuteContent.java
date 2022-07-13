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


package com.dtstack.taier.develop.bo;

import com.dtstack.taier.develop.sql.ParseResult;

import java.util.List;

public class ExecuteContent {

    private Long tenantId;

    private Long userId;

    private String sql;

    /**
     * 批量的sql
     */
    private List<String> sqlList;

    private ParseResult parseResult;

    private List<ParseResult> parseResultList;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务类型
     */
    private Integer taskType;

    private String jobId;

    private String database;

    public ExecuteContent setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
        return this;
    }

    public ExecuteContent setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public ExecuteContent setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public ExecuteContent setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public ExecuteContent setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public ExecuteContent setTaskType(Integer taskType) {
        this.taskType = taskType;
        return this;
    }

    public ExecuteContent setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public ExecuteContent setDatabase(String database) {
        this.database = database;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSql() {
        return sql;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public List<ParseResult> getParseResultList() {
        return parseResultList;
    }

    public void setParseResultList(List<ParseResult> parseResultList) {
        this.parseResultList = parseResultList;
    }

    public ParseResult getParseResult() {
        return parseResult;
    }

    public void setParseResult(ParseResult parseResult) {
        this.parseResult = parseResult;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getJobId() {
        return jobId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public String getDatabase() {
        return database;
    }
}
