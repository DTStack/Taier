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


/**
 * date: 2022/1/24 2:29 下午
 * author: zhaiyue
 */
public class BatchFunction extends TenantEntity{

    /**
     * 函数名称
     */
    private String name;

    /**
     * main函数类名
     */
    private String className;

    /**
     * 函数用途
     */
    private String purpose;

    /**
     * 函数命令格式
     */
    private String commandFormate;

    /**
     * 函数参数说明
     */
    private String paramDesc;

    /**
     * 父文件夹id
     */
    private Long nodePid;

    private Long createUserId;

    private Long modifyUserId;

    /**
     * 0：自定义函数  1：系统函数  2：存储过程
     */
    private Integer type;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 导入导出添加，函数资源名称
     */
    private String resourceName;

    /**
     * 存储过程sql
     */
    private String sqlText;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCommandFormate() {
        return commandFormate;
    }

    public void setCommandFormate(String commandFormate) {
        this.commandFormate = commandFormate;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public Long getNodePid() {
        return nodePid;
    }

    public void setNodePid(Long nodePid) {
        this.nodePid = nodePid;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }
}
