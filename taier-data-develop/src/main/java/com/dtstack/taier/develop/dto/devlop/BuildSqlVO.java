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

package com.dtstack.taier.develop.dto.devlop;


/**
 * @author yunliu
 * @company：dtstack.com
 * @date 2020-04-28 15:43
 * @description 解析sql返回的参数
 */
public class BuildSqlVO {

    /**
     * 生成的jobid
     */
    private String jobId;

    /**
     * 解析之后的sql
     */
    private String sql;

    /**
     * sql运行需要的参数
     */
    private String taskParam;

    /**
     * 生成的临时表名
     */
    private String tempTable;

    /**
     * 是否是查询sql
     */
    private Integer isSelectSql = 0;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 原本的sql
     */
    private String originSql;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 解析出来的列名
     */
    private String parsedColumns;
    /**
     * 引擎类型
     */
    private Integer engineType = 0;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(String taskParam) {
        this.taskParam = taskParam;
    }

    public String getTempTable() {
        return tempTable;
    }

    public void setTempTable(String tempTable) {
        this.tempTable = tempTable;
    }

    public Integer getIsSelectSql() {
        return isSelectSql;
    }

    public void setIsSelectSql(Integer isSelectSql) {
        this.isSelectSql = isSelectSql;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getOriginSql() {
        return originSql;
    }

    public void setOriginSql(String originSql) {
        this.originSql = originSql;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getParsedColumns() {
        return parsedColumns;
    }

    public void setParsedColumns(String parsedColumns) {
        this.parsedColumns = parsedColumns;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }
}
