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

import com.dtstack.taier.dao.domain.BatchSelectSql;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.service.develop.IBatchSelectSqlService;

public class ExecuteSelectSqlData {

    /**
     * 查询sql信息
     */
    private BatchSelectSql batchHiveSelectSql;

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
    private IBatchSelectSqlService IBatchSelectSqlService;

    public ExecuteSelectSqlData(BatchSelectSql batchHiveSelectSql, Task task, Integer taskType, com.dtstack.taier.develop.service.develop.IBatchSelectSqlService IBatchSelectSqlService) {
        this.batchHiveSelectSql = batchHiveSelectSql;
        this.task = task;
        this.taskType = taskType;
        this.IBatchSelectSqlService = IBatchSelectSqlService;
    }

    public BatchSelectSql getBatchHiveSelectSql() {
        return batchHiveSelectSql;
    }

    public void setBatchHiveSelectSql(BatchSelectSql batchHiveSelectSql) {
        this.batchHiveSelectSql = batchHiveSelectSql;
    }

    public Task getBatchTask() {
        return task;
    }

    public void setBatchTask(Task task) {
        this.task = task;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public com.dtstack.taier.develop.service.develop.IBatchSelectSqlService getIBatchSelectSqlService() {
        return IBatchSelectSqlService;
    }

    public void setIBatchSelectSqlService(com.dtstack.taier.develop.service.develop.IBatchSelectSqlService IBatchSelectSqlService) {
        this.IBatchSelectSqlService = IBatchSelectSqlService;
    }
}
