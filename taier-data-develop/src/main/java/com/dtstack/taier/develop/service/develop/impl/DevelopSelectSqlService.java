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

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.common.enums.EComputeType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopHiveSelectSqlMapper;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.TaskConfiguration;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 执行选中的sql或者脚本
 *
 * @author jiangbo
 */
@Service
public class DevelopSelectSqlService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DevelopSelectSqlService.class);

    @Autowired
    private DevelopHiveSelectSqlMapper developHiveSelectSqlDao;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private TaskConfiguration taskConfiguration;

    private static final String TASK_NAME_PREFIX = "run_%s_task_%s";


    public DevelopSelectSql getSelectSql(Long tenantId, String s, Integer o) {
        return developHiveSelectSqlDao.getByJobId(s, tenantId, o);
    }

    public DevelopSelectSql getByJobId(String jobId, Long tenantId, Integer isDeleted) {
        DevelopSelectSql selectSql = getSelectSql(tenantId, jobId, isDeleted);
        if (selectSql == null) {
            throw new RdosDefineException("select job not exists");
        }
        return selectSql;
    }

    public void stopSelectJob(String jobId, Long tenantId) {
        try {
            actionService.stop(Collections.singletonList(jobId), ComputeType.BATCH.getType());
            // 这里用逻辑删除，是为了在调度端删除可能生成的临时表
            developHiveSelectSqlDao.deleteByJobId(jobId, tenantId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, int isSelectSql, Long tenantId, String sql, Long userId, Integer taskType) {
        this.addSelectSql(jobId, tempTable, isSelectSql, tenantId, sql, userId, null, taskType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, Integer isSelectSql, Long tenantId, String sql, Long userId, String parsedColumns, Integer taskType) {
        DevelopSelectSql hiveSelectSql = new DevelopSelectSql();
        hiveSelectSql.setJobId(jobId);
        hiveSelectSql.setTempTableName(tempTable);
        hiveSelectSql.setTenantId(tenantId);
        hiveSelectSql.setIsSelectSql(isSelectSql);
        hiveSelectSql.setSqlText(sql);
        hiveSelectSql.setUserId(userId);
        hiveSelectSql.setParsedColumns(parsedColumns);
        hiveSelectSql.setTaskType(taskType);
        developHiveSelectSqlDao.insert(hiveSelectSql);
    }

    /**
     * 使用任务的方式运行sql
     *
     * @param parseResult
     * @param userId
     * @param taskType
     * @param preJobId
     * @return
     */
    public String runSqlByTask(ParseResult parseResult, Long userId,
                               Task task, Integer taskType, String preJobId) {
        ITaskRunner iTaskRunner = taskConfiguration.get(taskType);
        try {
            BuildSqlVO buildSqlVO = iTaskRunner.buildSql(parseResult, userId, task);
            // 发送sql任务
            sendSqlTask(buildSqlVO.getSql(), buildSqlVO.getTaskParam(), preJobId, task, taskType);
            // 记录job
            addSelectSql(preJobId, buildSqlVO.getTempTable(), buildSqlVO.getIsSelectSql(), task.getTenantId(),
                    parseResult.getOriginSql(), userId, buildSqlVO.getParsedColumns(), taskType);
            return preJobId;
        } catch (Exception e) {
            throw new RdosDefineException("任务执行sql失败", e);
        }
    }

    public String sendSqlTask(String sql, String taskParams, String jobId, Task task, Integer taskType) {
        ParamActionExt paramActionExt = new ParamActionExt();
        paramActionExt.setTaskType(taskType);
        paramActionExt.setSqlText(sql);
        paramActionExt.setComputeType(EComputeType.BATCH.getType());
        paramActionExt.setJobId(jobId);
        paramActionExt.setName(String.format(TASK_NAME_PREFIX, "sql", System.currentTimeMillis()));
        paramActionExt.setTaskParams(taskParams);
        paramActionExt.setTenantId(task.getTenantId());
        paramActionExt.setQueueName(task.getQueueName());
        paramActionExt.setDatasourceId(task.getDatasourceId());
        actionService.start(paramActionExt);
        return jobId;
    }

}
