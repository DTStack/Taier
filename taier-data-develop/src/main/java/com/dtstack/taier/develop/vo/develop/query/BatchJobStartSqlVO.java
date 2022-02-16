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

package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@ApiModel("实例运行sql信息")
public class BatchJobStartSqlVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private String dtToken;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "任务Id", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "唯一标识", example = "标识", required = true)
    private String uniqueKey;

    @ApiModelProperty(value = "sql语句", example = "show tables;", required = true)
    private String sql;

    @ApiModelProperty(value = "任务前置执行语句", required = true)
    private List<Map> taskVariables;

    @ApiModelProperty(value = "是否是DDL语句", example = "false", required = true)
    private Integer isCheckDDL;

    @ApiModelProperty(value = "是否终止", example = "false", required = true)
    private Boolean isEnd;

    @ApiModelProperty(value = "任务参数", example = "1", required = true)
    private String taskParams;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDtToken() {
        return dtToken;
    }

    public void setDtToken(String dtToken) {
        this.dtToken = dtToken;
    }

    public Boolean getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(Boolean root) {
        isRoot = root;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Map> getTaskVariables() {
        return taskVariables;
    }

    public void setTaskVariables(List<Map> taskVariables) {
        this.taskVariables = taskVariables;
    }

    public Integer getIsCheckDDL() {
        return isCheckDDL;
    }

    public void setIsCheckDDL(Integer isCheckDDL) {
        this.isCheckDDL = isCheckDDL;
    }

    public Boolean getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(Boolean end) {
        isEnd = end;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }
}
