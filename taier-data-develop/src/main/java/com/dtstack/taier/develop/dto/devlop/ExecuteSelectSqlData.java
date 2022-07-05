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

import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.service.develop.IDevelopSelectSqlService;

public class ExecuteSelectSqlData {

    /**
     * 查询sql信息
     */
    private DevelopSelectSql developHiveSelectSql;

    /**
     * 任务信息
     */
    private Task task;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * sql执行Service
     */
    private IDevelopSelectSqlService IDevelopSelectSqlService;

    public ExecuteSelectSqlData(DevelopSelectSql developHiveSelectSql, Task task, Integer taskType, com.dtstack.taier.develop.service.develop.IDevelopSelectSqlService IDevelopSelectSqlService) {
        this.developHiveSelectSql = developHiveSelectSql;
        this.task = task;
        this.taskType = taskType;
        this.IDevelopSelectSqlService = IDevelopSelectSqlService;
    }

    public DevelopSelectSql getBatchHiveSelectSql() {
        return developHiveSelectSql;
    }

    public void setBatchHiveSelectSql(DevelopSelectSql developHiveSelectSql) {
        this.developHiveSelectSql = developHiveSelectSql;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public com.dtstack.taier.develop.service.develop.IDevelopSelectSqlService getIBatchSelectSqlService() {
        return IDevelopSelectSqlService;
    }

    public void setIBatchSelectSqlService(com.dtstack.taier.develop.service.develop.IDevelopSelectSqlService IDevelopSelectSqlService) {
        this.IDevelopSelectSqlService = IDevelopSelectSqlService;
    }
}
