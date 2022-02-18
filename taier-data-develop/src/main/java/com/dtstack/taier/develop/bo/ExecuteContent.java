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

/**
 * @author jiangbo
 * @date 2019/6/15
 */
public class ExecuteContent {

    private Long tenantId;
    private Long projectId;
    private String projectName;
    private Long userId;

    private String sql;

    /**
     * 批量的sql
     */
    private List<String> sqlList;

    private List<ParseResult> parseResultList;

    private ParseResult parseResult;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务类型
     */
    private Integer taskType;

    private boolean isdirtyDataTable;

    /**
     * 是否为root用户
     */
    private Boolean isRootUser;

    /**
     * 是否检查语法
     */
    private boolean checkSyntax;

    /**
     * ?
     */
    private String preJobId;

    private boolean isExecuteSqlLater;

    private String sessionKey;

    private String dtToken;

    private Boolean isEnd;

//    public ExecuteContent setParseResultList(List<ParseResult> parseResultList) {
//        this.parseResultList = parseResultList;
//        return this;
//    }

    public ExecuteContent setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
        return this;
    }


    private String database;

    public ExecuteContent setEnd(Boolean end) {
        isEnd = end;
        return this;
    }

    public ExecuteContent setDtToken(String dtToken) {
        this.dtToken = dtToken;
        return this;
    }

    public ExecuteContent setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
        return this;
    }

    public ExecuteContent setExecuteSqlLater(boolean executeSqlLater) {
        isExecuteSqlLater = executeSqlLater;
        return this;
    }

    public ExecuteContent setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }


    public ExecuteContent setSql(String sql) {
        this.sql = sql;
        return this;
    }

//    public ExecuteContent setParseResult(ParseResult parseResult) {
//        this.parseResult = parseResult;
//        return this;
//    }

    public ExecuteContent setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public ExecuteContent setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public ExecuteContent setIsdirtyDataTable(boolean isdirtyDataTable) {
        this.isdirtyDataTable = isdirtyDataTable;
        return this;
    }

    public ExecuteContent setRootUser(Boolean rootUser) {
        isRootUser = rootUser;
        return this;
    }

    public ExecuteContent setCheckSyntax(boolean checkSyntax) {
        this.checkSyntax = checkSyntax;
        return this;
    }

    public ExecuteContent setPreJobId(String preJobId) {
        this.preJobId = preJobId;
        return this;
    }

	public ExecuteContent setDatabase(String database) {
		this.database = database;
		return this;
	}

    public Long getTenantId() {
        return tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
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

    public Integer getTaskType() {
        return taskType;
    }

    public boolean isIsdirtyDataTable() {
        return isdirtyDataTable;
    }

    public Boolean getRootUser() {
        return isRootUser;
    }

    public boolean isCheckSyntax() {
        return checkSyntax;
    }

    public String getPreJobId() {
        return preJobId;
    }

    public boolean isExecuteSqlLater() {
        return isExecuteSqlLater;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getDtToken() {
        return dtToken;
    }

    public Boolean getEnd() {
        return isEnd;
    }

    public String getDatabase() {
        return database;
    }
}
