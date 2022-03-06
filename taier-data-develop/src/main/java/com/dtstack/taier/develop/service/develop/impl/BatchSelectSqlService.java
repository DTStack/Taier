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

import com.dtstack.taier.common.enums.ComputeType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.BatchSelectSql;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopHiveSelectSqlDao;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteSelectSqlData;
import com.dtstack.taier.develop.service.develop.IBatchSelectSqlService;
import com.dtstack.taier.develop.service.develop.MultiEngineServiceFactory;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Objects;

/**
 * 执行选中的sql或者脚本
 * @author jiangbo
 */
@Service
public class BatchSelectSqlService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BatchSelectSqlService.class);

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private DevelopHiveSelectSqlDao developHiveSelectSqlDao;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    /**
     * 查询sql运行结果
     * @param jobId
     * @param taskId
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param type
     * @param sqlId
     * @return
     * @throws Exception
     */
    public ExecuteResultVO selectData(String jobId,
                                      Long taskId,
                                      Long tenantId,
                                      Long userId,
                                      Boolean isRoot,
                                      Integer type,
                                      String sqlId) throws Exception {
        ExecuteSelectSqlData selectSqlData = beforeGetResult(jobId, taskId, tenantId, type, sqlId);
        return selectSqlData.getIBatchSelectSqlService().selectData(selectSqlData.getBatchTask(), selectSqlData.getBatchHiveSelectSql(), tenantId, userId, isRoot, selectSqlData.getTaskType());
    }

    /**
     * 查询sql运行状态
     * @param jobId
     * @param taskId
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param type
     * @param sqlId
     * @return
     * @throws Exception
     */
    public ExecuteResultVO selectStatus(String jobId,
                                        Long taskId,
                                        Long tenantId,
                                        Long userId,
                                        Boolean isRoot,
                                        Integer type,
                                        String sqlId) throws Exception {
        ExecuteSelectSqlData selectSqlData = beforeGetResult(jobId, taskId, tenantId, type, sqlId);
        return selectSqlData.getIBatchSelectSqlService().selectStatus(selectSqlData.getBatchTask(), selectSqlData.getBatchHiveSelectSql(), tenantId, userId, isRoot, selectSqlData.getTaskType());
    }

    /**
     * 查询sql运行日志
     * @param jobId
     * @param taskId
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param type
     * @param sqlId
     * @return
     * @throws Exception
     */
    public ExecuteResultVO selectRunLog(String jobId,
                                        Long taskId,
                                        Long tenantId,
                                        Long userId,
                                        Boolean isRoot,
                                        Integer type,
                                        String sqlId) throws Exception {
        ExecuteSelectSqlData selectSqlData = beforeGetResult(jobId, taskId, tenantId, type, sqlId);
        return selectSqlData.getIBatchSelectSqlService().selectRunLog(selectSqlData.getBatchTask(), selectSqlData.getBatchHiveSelectSql(), tenantId, userId, isRoot, selectSqlData.getTaskType());
    }

    /**
     * sql查询前置处理
     *
     * @param jobId
     * @param taskId
     * @param tenantId
     * @param type
     * @param sqlId
     * @return
     */
    private ExecuteSelectSqlData beforeGetResult(String jobId, Long taskId, Long tenantId, Integer type, String sqlId){
        BatchSelectSql batchHiveSelectSql = developHiveSelectSqlDao.getByJobId(StringUtils.isNotEmpty(sqlId) ? sqlId : jobId, tenantId, null);
        Preconditions.checkNotNull(batchHiveSelectSql, "不存在该临时查询");
        if (StringUtils.isNotEmpty(sqlId)){
            batchHiveSelectSql.setFatherJobId(jobId);
            batchHiveSelectSql.setJobId(sqlId);
        }
        IBatchSelectSqlService selectSqlService = multiEngineServiceFactory.getBatchSelectSqlService(batchHiveSelectSql.getTaskType());
        Preconditions.checkNotNull(selectSqlService, String.format("不支持此任务类型 %d", batchHiveSelectSql.getTaskType()));
        Task task = batchTaskService.getOneWithError(taskId);;
        Integer taskType = null;
        if (Objects.nonNull(task)) {
            taskType = task.getTaskType();
        }
        if (Objects.isNull(taskType)) {
            throw new DtCenterDefException("任务类型为空");
        }
        return new ExecuteSelectSqlData(batchHiveSelectSql, task, taskType, selectSqlService);
    }


    public BatchSelectSql getByJobId(String jobId, Long tenantId, Integer isDeleted){
        BatchSelectSql selectSql = developHiveSelectSqlDao.getByJobId(jobId,tenantId, isDeleted);
        if (selectSql == null){
            throw new RdosDefineException("select job not exists");
        }
        return selectSql;
    }

    public void stopSelectJob(String jobId,Long tenantId){
        try {
            actionService.stop(Collections.singletonList(jobId), ComputeType.BATCH.getType());
            // 这里用逻辑删除，是为了在调度端删除可能生成的临时表
            developHiveSelectSqlDao.deleteByJobId(jobId, tenantId);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, int isSelectSql, Long tenantId, String sql, Long userId, Integer taskType) {
        this.addSelectSql(jobId, tempTable, isSelectSql, tenantId, sql, userId, null, taskType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, Integer isSelectSql, Long tenantId, String sql, Long userId, String parsedColumns, Integer taskType){
        BatchSelectSql hiveSelectSql = new BatchSelectSql();
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

    public int updateGmtModify(String jobId, Long tenantId){
        return developHiveSelectSqlDao.updateGmtModify(jobId, tenantId);
    }
}
