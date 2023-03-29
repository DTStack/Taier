/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.scheduler.executor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EJobCacheStage;
import com.dtstack.taier.common.source.SourceDTOLoader;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.dtstack.taier.scheduler.jobdealer.JobDealer;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobExpandService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * rdb executor
 *
 * @author ：wangchuan
 * date：Created in 14:04 2022/10/8
 * company: www.dtstack.com
 */
public class RdbJobExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(RdbJobExecutor.class);

    private final JobDealer jobDealer;

    private final ScheduleJobService scheduleJobService;

    private final ScheduleJobExpandService scheduleJobExpandService;

    private final ScheduleJobCacheService scheduleJobCacheService;

    private final SourceDTOLoader sourceDTOLoader;

    public RdbJobExecutor(ApplicationContext applicationContext, String jobResource) {
        this.jobDealer = applicationContext.getBean(JobDealer.class);
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobExpandService = applicationContext.getBean(ScheduleJobExpandService.class);
        this.scheduleJobCacheService = applicationContext.getBean(ScheduleJobCacheService.class);
        this.sourceDTOLoader = applicationContext.getBean(SourceDTOLoader.class);
        Thread.currentThread().setName(jobResource + "-RdbJobExecutor");
    }

    public JudgeResult submitJob(JobClient jobClient) {
        JSONObject jsonObject = getJsonObject(jobClient);
        scheduleJobService.updateJobSubmitSuccess(jobClient.getJobId(), jobClient.getJobId(), null);
        scheduleJobExpandService.updateExtraInfoAndLog(jobClient.getJobId(), null, jsonObject.toJSONString(), null);
        jobDealer.updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
        executeJob(jobClient);
        return JudgeResult.ok();
    }

    public void executeJob(JobClient jobClient) {
        jobClient.doStatusCallBack(TaskStatus.RUNNING.getStatus());
        LOGGER.info("jobId:{} taskType:{} submit to job start run", jobClient.getJobId(), jobClient.getTaskType());
        // executeBatchQuery 执行不成功 会执行抛异常，不会返回false
        ISourceDTO sourceDTO = sourceDTOLoader.buildSourceDTO(jobClient.getDatasourceId());
        if (sourceDTO instanceof RdbmsSourceDTO) {
            RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) sourceDTO;
            try {
                rdbmsSourceDTO.setProperties(JSONObject.toJSONString(jobClient.getConfProperties()));
            } catch (Exception e) {
            }
            sourceDTO = rdbmsSourceDTO;
        }
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        client.executeBatchQuery(sourceDTO, SqlQueryDTO.builder().sql(jobClient.getSql()).build());
        try {
            RetryUtil.executeWithRetry(() -> {
                updateJobStatus(jobClient, TaskStatus.FINISHED.getStatus());
                scheduleJobCacheService.deleteByJobId(jobClient.getJobId());
                return null;
            }, 3, 200, false);
        } catch (Exception e) {
            LOGGER.error("jobId:{} taskType:{} update  job to finish error", jobClient.getJobId(), jobClient.getTaskType(), e);
        }
    }

    @NotNull
    private JSONObject getJsonObject(JobClient jobClient) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobid", jobClient.getJobId());
        jsonObject.put("msg_info", "submit job is success");
        return jsonObject;
    }

    private void updateJobStatus(JobClient jobClient, Integer status) {
        LOGGER.info("jobId:{} status :{}", jobClient.getJobId(), status);
        scheduleJobService.updateJobStatusAndExecTime(jobClient.getJobId(), status);
    }
}
