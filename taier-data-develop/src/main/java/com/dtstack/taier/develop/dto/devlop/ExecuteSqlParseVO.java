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

import java.util.List;


/**
 * @author yunliu
 * @date 2020-04-27 09:24
 * @description
 */
public class ExecuteSqlParseVO {


    private String msg;

    private Integer status;


    /**
     * 发送到引擎生成的jobid
     */
    private String  jobId;

    private Integer engineType;

    private String sqlText;

    /**
     * sql结果id对应的集合
     */
    private List<SqlResultVO> sqlIdList;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public List<SqlResultVO> getSqlIdList() {
        return sqlIdList;
    }

    public void setSqlIdList(List<SqlResultVO> sqlIdList) {
        this.sqlIdList = sqlIdList;
    }
}
