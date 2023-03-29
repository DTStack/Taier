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

package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.taier.common.enums.TempJobType;
import com.google.common.base.Charsets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@TableName("develop_select_sql")
public class DevelopSelectSql extends TenantEntity {


    /**
     * 实例id
     */
    private String jobId;

    /**
     * 临时表名称（临时表用于存放查询的结果）
     */
    private String tempTableName;

    /**
     * SQL语法的类型
     */
    private int isSelectSql;

    /**
     * 任务的SQL
     */
    private String sqlText;

    /**
     * 字段信息
     */
    private String parsedColumns;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 引擎类型
     */
    private int taskType;


    /**
     * 数据源id
     */
    private Long datasourceId;

    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public int getIsSelectSql() {
        return isSelectSql;
    }

    public void setIsSelectSql(int isSelectSql) {
        this.isSelectSql = isSelectSql;
    }

    /**
     * 如果是数据同步任务则需要解密
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getCorrectSqlText() throws UnsupportedEncodingException {
        String sql;
        if (isSelectSql == TempJobType.SYNC_TASK.getType()) {
            sql = URLDecoder.decode(getSqlText(), Charsets.UTF_8.name());
        } else {
            sql = getSqlText();
        }
        return sql;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTempTableName() {
        return tempTableName;
    }

    public void setTempTableName(String tempTableName) {
        this.tempTableName = tempTableName;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getParsedColumns() {
        return parsedColumns;
    }

    public void setParsedColumns(String parsedColumns) {
        this.parsedColumns = parsedColumns;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }
}
