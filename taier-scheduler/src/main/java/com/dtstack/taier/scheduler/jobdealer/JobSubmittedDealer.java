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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EJobCacheStage;
import com.dtstack.taier.common.util.JobGraphUtil;
import com.dtstack.taier.dao.domain.ScheduleJobHistory;
import com.dtstack.taier.dao.mapper.ScheduleJobHistoryMapper;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.constrant.JobResultConstant;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.scheduler.jobdealer.cache.ShardCache;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobExpandService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
@Component
public class JobSubmittedDealer implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(JobSubmittedDealer.class);

    private LinkedBlockingQueue<JobClient> queue;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobCacheService scheduleJobCacheService;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private JobRestartDealer jobRestartDealer;

    @Autowired
    private ScheduleJobHistoryMapper historyMapper;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;


    public JobSubmittedDealer() {
        queue = JobSubmitDealer.getSubmittedQueue();
    }

    @Override
    public void run() {
        while (true) {
            JobClient jobClient = null;
            try {
                jobClient = queue.take();

                if (jobRestartDealer.checkAndRestartForSubmitResult(jobClient)) {
                    LOGGER.warn("failed submit job restarting, jobId:{} jobResult:{} ...", jobClient.getJobId(), jobClient.getJobResult());
                    continue;
                }

                LOGGER.info("success submit job to Engine, jobId:{} jobResult:{} ...", jobClient.getJobId(), jobClient.getJobResult());

                //存储执行日志
                if (StringUtils.isNotBlank(jobClient.getEngineTaskId()) || StringUtils.isNotBlank(jobClient.getApplicationId())) {
                    JobResult jobResult = jobClient.getJobResult();
                    String appId = jobResult.getData(JobResult.JOB_ID_KEY);
                    JSONObject jobExtraInfo = jobResult.getExtraInfoJson();
                    jobExtraInfo.put(JobResultConstant.JOB_GRAPH,JobGraphUtil.formatJSON(jobClient.getEngineTaskId(), jobExtraInfo.getString(JobResultConstant.JOB_GRAPH), jobClient.getComputeType()));
                    scheduleJobService.updateJobSubmitSuccess(jobClient.getJobId(), jobClient.getEngineTaskId(), appId);
                    scheduleJobExpandService.updateExtraInfoAndLog(jobClient.getJobId(),jobExtraInfo.toJSONString(),jobClient.getJobResult().getJsonStr(),null);
                    jobDealer.updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
                    jobClient.doStatusCallBack(TaskStatus.SUBMITTED.getStatus());
                    JobClient finalJobClient = jobClient;
                    shardCache.updateLocalMemTaskStatus(jobClient.getJobId(), TaskStatus.SUBMITTED.getStatus(), (jobId) -> {
                        LOGGER.warn("success submit job to Engine, jobId:{} jobResult:{} but shareManager is not found ...", jobId, finalJobClient.getJobResult());
                        finalJobClient.doStatusCallBack(TaskStatus.CANCELED.getStatus());
                    });
                    saveHistory(jobClient);
                } else {
                    jobClientFail(jobClient.getJobId(), jobClient.getJobResult().getJsonStr());
                }
            } catch (Throwable e) {
                LOGGER.error("jobId submitted {} jobStatus dealer run error", null == jobClient ? "" : jobClient.getJobId(), e);
                if (null != jobClient) {
                    jobClientFail(jobClient.getJobId(), JobResult.createErrorResult(e).getJsonStr());
                }
            }
        }
    }

    private void saveHistory(JobClient jobClient) {
        ScheduleJobHistory scheduleJobHistory = new ScheduleJobHistory();
        scheduleJobHistory.setJobId(jobClient.getJobId());
        scheduleJobHistory.setEngineJobId(jobClient.getEngineTaskId());
        scheduleJobHistory.setApplicationId(jobClient.getApplicationId());
        scheduleJobHistory.setExecStartTime(DateTime.now().toDate());
        historyMapper.insert(scheduleJobHistory);
    }

    private void jobClientFail(String jobId, String info) {
        try {
            scheduleJobService.jobFail(jobId, TaskStatus.FAILED.getStatus(), info);
            LOGGER.info("jobId:{} update job status:{}, job is finished.", jobId, TaskStatus.FAILED.getStatus());
            scheduleJobCacheService.deleteByJobId(jobId);
        } catch (Exception e) {
            LOGGER.error("jobId:{} update job fail {}  error", jobId, info, e);
        }
    }

}
