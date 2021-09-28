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

package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.EngineJobCheckpoint;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.pluginapi.pojo.CheckResult;
import com.dtstack.engine.pluginapi.pojo.ParamAction;
import com.dtstack.engine.master.impl.pojo.ParamActionExt;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.JobIdentifier;
import com.dtstack.engine.pluginapi.enums.ComputeType;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.pluginapi.enums.EDeployMode;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Reason: 查询实时任务数据
 * Date: 2018/10/11
 * Company: www.dtstack.com
 * @author jiangbo
 */
@Service
public class StreamTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamTaskService.class);

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private WorkerOperator workerOperator;

    /**
     * 查询 生成失败的 checkPoint
     */
    public List<EngineJobCheckpoint> getFailedCheckPoint(String taskId, Long triggerStart, Long triggerEnd, Integer size){
        List<EngineJobCheckpoint> failedCheckPointList = engineJobCheckpointDao.listFailedByTaskIdAndRangeTime(taskId, triggerStart, triggerEnd, size);
        if(CollectionUtils.isNotEmpty(failedCheckPointList)) {
            engineJobCheckpointDao.updateFailedCheckpoint(failedCheckPointList);
        }
        return failedCheckPointList;
    }

    /**
     * 查询checkPoint
     */
    public List<EngineJobCheckpoint> getCheckPoint( String taskId, Long triggerStart, Long triggerEnd){
        return engineJobCheckpointDao.listByTaskIdAndRangeTime(taskId, triggerStart, triggerEnd);
    }

    /**
     * 查询checkPoint
     */
    public EngineJobCheckpoint getSavePoint( String taskId){
        return engineJobCheckpointDao.findLatestSavepointByTaskId(taskId);
    }

    public EngineJobCheckpoint getByTaskIdAndEngineTaskId( String taskId,  String engineTaskId){
        return engineJobCheckpointDao.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
    }

    /**
     * 查询stream job
     */
    public List<ScheduleJob> getEngineStreamJob( List<String> taskIds){

        if(CollectionUtils.isEmpty(taskIds)){
            return Collections.EMPTY_LIST;
        }
        List<ScheduleJob> jobs = scheduleJobDao.getRdosJobByJobIds(taskIds);

        if (CollectionUtils.isNotEmpty(jobs)){
            for (ScheduleJob scheduleJob : jobs) {
                scheduleJob.setStatus(RdosTaskStatus.getShowStatus(scheduleJob.getStatus()));
            }
        }
        return jobs;
    }

    /**
     * 获取某个状态的任务task_id
     */
    public List<String> getTaskIdsByStatus( Integer status){
        return scheduleJobDao.getJobIdsByStatus(status, ComputeType.STREAM.getType());
    }

    /**
     * 获取任务的状态
     */
    public Integer getTaskStatus(String taskId) {
        Integer status = null;
        if (StringUtils.isNotEmpty(taskId)) {
            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
            if (scheduleJob != null) {
                status = scheduleJob.getStatus();
                return RdosTaskStatus.getShowStatus(status);
            }
        }

        return null;
    }

    /**
     * 获取实时计算运行中任务的日志URL
     * @param taskId
     * @return
     */
    public List<String> getRunningTaskLogUrl( String taskId) {

        Preconditions.checkState(StringUtils.isNotEmpty(taskId), "taskId can't be empty");

        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
        Preconditions.checkNotNull(scheduleJob, "can't find record by taskId" + taskId);

        //只获取运行中的任务的log—url
        Integer status = scheduleJob.getStatus();
        if (!RdosTaskStatus.RUNNING.getStatus().equals(status)) {
            throw new RdosDefineException(String.format("job:%s not running status ", taskId), ErrorCode.INVALID_TASK_STATUS);
        }

        String applicationId = scheduleJob.getApplicationId();

        if (StringUtils.isEmpty(applicationId)) {
            throw new RdosDefineException(String.format("job %s not running in perjob", taskId), ErrorCode.INVALID_TASK_RUN_MODE);
        }

        JobClient jobClient = null;
        JobIdentifier jobIdentifier = null;

        //如何获取url前缀
        try{
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(taskId);
            if (engineJobCache == null) {
                throw new RdosDefineException(String.format("job:%s not exist in job cache table ", taskId),ErrorCode.JOB_CACHE_NOT_EXIST);
            }
            String jobInfo = engineJobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);

            jobIdentifier = new JobIdentifier(scheduleJob.getEngineJobId(), applicationId, taskId,scheduleJob.getDtuicTenantId(),engineJobCache.getEngineType(),
                    EDeployMode.PERJOB.getType(),paramAction.getUserId(),null,paramAction.getComponentVersion());
            jobClient = new JobClient(paramAction);

            return workerOperator.getRollingLogBaseInfo(jobIdentifier);

        }catch (Exception e){
            if (e instanceof RdosDefineException) {
                throw (RdosDefineException) e;
            } else {
                if (jobClient != null) {
                    RdosTaskStatus jobStatus = workerOperator.getJobStatus(jobIdentifier);
                    Integer statusCode = jobStatus.getStatus();
                    if (RdosTaskStatus.getStoppedStatus().contains(statusCode)) {
                        throw new RdosDefineException(String.format("job:%s had stop ", taskId), ErrorCode.INVALID_TASK_STATUS, e);
                    }
                }
                throw new RdosDefineException(String.format("get job:%s ref application url error..", taskId), ErrorCode.UNKNOWN_ERROR, e);
            }

        }

    }

    public CheckResult grammarCheck(ParamActionExt paramActionExt) {
        LOGGER.info("grammarCheck actionParam: {}", JSONObject.toJSONString(paramActionExt));
        CheckResult checkResult = null;
        try {
            JobClient jobClient = new JobClient(paramActionExt);
            checkResult = workerOperator.grammarCheck(jobClient);
        } catch (Exception e) {
            checkResult = CheckResult.exception(ExceptionUtil.getErrorMessage(e));
        }
        return checkResult;
    }
}
