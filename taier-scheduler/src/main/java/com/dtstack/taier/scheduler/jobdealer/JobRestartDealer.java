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

package com.dtstack.taier.scheduler.jobdealer;

import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.mapper.ScheduleEngineJobRetryMapper;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.jobdealer.bo.EngineJobRetry;
import com.dtstack.taier.scheduler.jobdealer.cache.ShardCache;
import com.dtstack.taier.scheduler.service.EngineJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;


/**
 * 注意如果是由于资源不足导致的任务失败应该减慢发送速度
 * Date: 2018/3/22
 * Company: www.dtstack.com
 * @author xuchao
 */
@Component
public class JobRestartDealer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRestartDealer.class);

    @Autowired
    private EngineJobCacheService engineJobCacheService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleEngineJobRetryMapper engineJobRetryMapper;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private JobDealer jobDealer;

    /**
     * 对提交结果判定是否重试
     * 不限制重试次数
     * @param jobClient
     * @return
     */
    public boolean checkAndRestartForSubmitResult(JobClient jobClient){
        if(!checkSubmitResult(jobClient)){
            return false;
        }

        int alreadyRetryNum = getAlreadyRetryNum(jobClient.getJobId());
        if (alreadyRetryNum >= jobClient.getMaxRetryNum()) {
            LOGGER.info("[retry=false] jobId:{} alreadyRetryNum:{} maxRetryNum:{}, alreadyRetryNum >= maxRetryNum.", jobClient.getJobId(), alreadyRetryNum, jobClient.getMaxRetryNum());
            return false;
        }

        boolean retry = restartJob(jobClient,null);
        LOGGER.info("【retry={}】 jobId:{} alreadyRetryNum:{} will retry and add into queue again.", retry, jobClient.getJobId(), alreadyRetryNum);

        return retry;
    }

    private boolean checkSubmitResult(JobClient jobClient){
        if(jobClient.getJobResult() == null){
            //未提交过
            return true;
        }

        if(!jobClient.getJobResult().getCheckRetry()){
            LOGGER.info("[retry=false] jobId:{} jobResult.checkRetry:{} jobResult.msgInfo:{} check retry is false.", jobClient.getJobId(), jobClient.getJobResult().getCheckRetry(), jobClient.getJobResult().getMsgInfo());
            return false;
        }

        if(!jobClient.getIsFailRetry()){
            LOGGER.info("[retry=false] jobId:{} isFailRetry:{} isFailRetry is false.", jobClient.getJobId(), jobClient.getIsFailRetry());
            return false;
        }

        return true;
    }

    /***
     * 对任务状态判断是否需要重试
     * @param status
     * @param scheduleJob
     * @param jobCache
     * @return
     */
    public boolean checkAndRestart(Integer status, ScheduleJob scheduleJob, ScheduleEngineJobCache jobCache, BiConsumer<ScheduleJob,JobClient> saveRetryFunction){
        Pair<Boolean, JobClient> checkResult = checkJobInfo(scheduleJob.getJobId(), jobCache, status);
        if(!checkResult.getKey()){
            return false;
        }


        JobClient jobClient = checkResult.getValue();
        // 是否需要重新提交
        int alreadyRetryNum = getAlreadyRetryNum(scheduleJob.getJobId());
        if (alreadyRetryNum >= jobClient.getMaxRetryNum()) {
            LOGGER.info("[retry=false] jobId:{} alreadyRetryNum:{} maxRetryNum:{}, alreadyRetryNum >= maxRetryNum.", jobClient.getJobId(), alreadyRetryNum, jobClient.getMaxRetryNum());
            return false;
        }

        // 通过engineJobId或appId获取日志
        jobClient.setEngineTaskId(scheduleJob.getEngineJobId());
        jobClient.setApplicationId(scheduleJob.getApplicationId());

        jobClient.setCallBack((jobStatus)-> updateJobStatus(scheduleJob.getJobId(), jobStatus));

        boolean retry = restartJob(jobClient,saveRetryFunction);
        LOGGER.info("【retry={}】 jobId:{} alreadyRetryNum:{} will retry and add into queue again.", retry, jobClient.getJobId(), alreadyRetryNum);

        return retry;
    }



    private Pair<Boolean, JobClient> checkJobInfo(String jobId, ScheduleEngineJobCache jobCache, Integer status) {
        Pair<Boolean, JobClient> check = new Pair<>(false, null);

        if(!TaskStatus.FAILED.getStatus().equals(status) && !TaskStatus.SUBMITFAILD.getStatus().equals(status)){
            return check;
        }

        try {
            String jobInfo = jobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);

            if(!jobClient.getIsFailRetry()){
                LOGGER.info("[retry=false] jobId:{} isFailRetry:{} isFailRetry is false.", jobClient.getJobId(), jobClient.getIsFailRetry());
                return check;
            }

            return new Pair<>(true, jobClient);
        } catch (Exception e){
            // 解析任务的jobInfo反序列到ParamAction失败，任务不进行重试.
            LOGGER.error("[retry=false] jobId:{} default not retry, because getIsFailRetry happens error:.", jobId, e);
            return check;
        }
    }

    private boolean restartJob(JobClient jobClient,BiConsumer<ScheduleJob,JobClient> saveRetryFunction){
        ScheduleEngineJobCache jobCache = engineJobCacheService.getByJobId(jobClient.getJobId());
        if (jobCache == null) {
            LOGGER.info("jobId:{} restart but jobCache is null.", jobClient.getJobId());
            return false;
        }
        String jobInfo = jobCache.getJobInfo();
        try {
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            jobClient.setSql(paramAction.getSqlText());
            //添加到重试队列中
            boolean isAdd = jobDealer.addRestartJob(jobClient);
            if (isAdd) {
                String jobId = jobClient.getJobId();
                //重试任务更改在zk的状态，统一做状态清理
                shardCache.updateLocalMemTaskStatus(jobId, TaskStatus.RESTARTING.getStatus());

                //重试的任务不置为失败，waitengine
                ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobClient.getJobId());
                if (saveRetryFunction != null) {
                    saveRetryFunction.accept(scheduleJob, jobClient);
                } else {
                    jobRetryRecord(scheduleJob, jobClient, null);
                }

                scheduleJobService.updateStatus(jobId, TaskStatus.RESTARTING.getStatus());
                LOGGER.info("jobId:{} update job status:{}.", jobId, TaskStatus.RESTARTING.getStatus());

                //update retryNum
                increaseJobRetryNum(jobClient.getJobId());
            }
            return isAdd;
        } catch (Exception e) {
            LOGGER.error("jobId:{} restart but convert paramAction error: ", jobClient.getJobId(), e);
            return false;
        }
    }

    public void jobRetryRecord(ScheduleJob scheduleJob, JobClient jobClient,String engineLog) {
        try {
            EngineJobRetry batchJobRetry = EngineJobRetry.toEntity(scheduleJob, jobClient,engineLog);
            batchJobRetry.setStatus(TaskStatus.RESTARTING.getStatus());
            engineJobRetryMapper.insert(batchJobRetry);
        } catch (Throwable e ){
            LOGGER.error("",e);
        }
    }

    private void updateJobStatus(String jobId, Integer status) {
        scheduleJobService.updateStatus(jobId, status);
        LOGGER.info("jobId:{} update job status:{}.", jobId, status);
    }

    /**
     * 获取任务已经重试的次数
     */
    private Integer getAlreadyRetryNum(String jobId){
        ScheduleJob rdosEngineBatchJob = scheduleJobService.getByJobId(jobId);
        return rdosEngineBatchJob == null || rdosEngineBatchJob.getRetryNum() == null ? 0 : rdosEngineBatchJob.getRetryNum();
    }

    private void increaseJobRetryNum(String jobId){
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
        if (scheduleJob == null) {
            return;
        }
        Integer retryNum = scheduleJob.getRetryNum() == null ? 0 : scheduleJob.getRetryNum();
        retryNum++;
        scheduleJobService.updateRetryNum(jobId, retryNum);
    }
}
