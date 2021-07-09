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


package com.dtstack.batch.bo;

import lombok.Data;

import java.util.List;

/**
 * @author jiangbo
 * @date 2019/6/15
 */
@Data
public class ExecuteContent {

    private Long dtuicTenantId;
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
     * 关联的id，任务或脚本id
     */
    private Long relationId;

    /**
     * 关联对象类型，0-脚本, 1-任务
     */
    private Integer relationType;

    /**
     * 管理对象的具体类型，比如任务的具体类型
     */
    private Integer detailType;

    private boolean isdirtyDataTable;

    /**
     * 是否为root用户
     */
    private boolean isRootUser;

    /**
     * 是否检查语法
     */
    private boolean checkSyntax;

    /**
     * ?
     */
    private String preJobId;

    private Integer engineType;

    private Integer tableType;

    private boolean isExecuteSqlLater;

    private String sessionKey;

    private String dtToken;

    private Boolean isEnd;

    public ExecuteContent setParseResultList(List<ParseResult> parseResultList) {
        this.parseResultList = parseResultList;
        return this;
    }

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

    public ExecuteContent setTableType(Integer tableType) {
        this.tableType = tableType;
        return this;
    }

    public ExecuteContent setEngineType(Integer engineType) {
        this.engineType = engineType;
        return this;
    }

    public ExecuteContent setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
        return this;
    }

    public ExecuteContent setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public ExecuteContent setParseResult(ParseResult parseResult) {
        this.parseResult = parseResult;
        return this;
    }

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

    public ExecuteContent setRelationId(Long relationId) {
        this.relationId = relationId;
        return this;
    }

    public ExecuteContent setRelationType(Integer relationType) {
        this.relationType = relationType;
        return this;
    }

    public ExecuteContent setDetailType(Integer detailType) {
        this.detailType = detailType;
        return this;
    }

    public ExecuteContent setIsdirtyDataTable(boolean isdirtyDataTable) {
        this.isdirtyDataTable = isdirtyDataTable;
        return this;
    }

    public ExecuteContent setRootUser(boolean rootUser) {
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

}
