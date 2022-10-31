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

package com.dtstack.taier.scheduler.server.scheduler;

import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.scheduler.enums.JobPhaseStatus;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.server.scheduler.interceptor.SubmitInterceptor;
import com.dtstack.taier.scheduler.service.ScheduleJobJobService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.utils.JobExecuteOrderUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 4:26 PM
 * @Email: dazhi@dtstack.com
 * @Description: 周期调度
 */
@Component
public class CycleJobScheduler extends AbstractJobSummitScheduler {

    private final Logger LOGGER = LoggerFactory.getLogger(FillDataJobScheduler.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired(required = false)
    private List<SubmitInterceptor> submitInterceptorList;

    private final String DATA_YMD = "yyyyMMdd";


    @Override
    protected List<ScheduleJobDetails> listExecJob(Long startSort, String nodeAddress, Boolean isEq) {
        List<ScheduleJob> scheduleJobList = scheduleJobService.listCycleJob(startSort, nodeAddress, getScheduleType().getType(), isEq, JobPhaseStatus.CREATE.getCode());

        List<String> jobKeys = scheduleJobList.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());
        List<ScheduleJobJob> scheduleJobJobList = scheduleJobJobService.listByJobKeys(jobKeys);
        Map<String, List<ScheduleJobJob>> jobJobMap = scheduleJobJobList.stream().collect(Collectors.groupingBy(ScheduleJobJob::getJobKey));
        List<ScheduleJobDetails> scheduleJobDetailsList = new ArrayList<>(scheduleJobList.size());

        for (ScheduleJob scheduleJob : scheduleJobList) {
            ScheduleJobDetails scheduleJobDetails = new ScheduleJobDetails();
            scheduleJobDetails.setScheduleJob(scheduleJob);
            scheduleJobDetails.setJobJobList(jobJobMap.get(scheduleJob.getJobKey()));
            scheduleJobDetailsList.add(scheduleJobDetails);
        }

        return scheduleJobDetailsList;
    }

    @Override
    protected Long getMinSort() {
        String triggerTime = new DateTime().toString(DATA_YMD);
        triggerTime += "000000";
        return JobExecuteOrderUtil.buildJobExecuteOrder(triggerTime,0);
    }

    @Override
    protected List<SubmitInterceptor> getInterceptor() {
        return submitInterceptorList;
    }

//    @Override
//    protected List<JudgeJobExecOperator> getJudgeJobExecOperator() {
//        if (CollectionUtils.isNotEmpty(judgeJobExecOperators)) {
//            return judgeJobExecOperators;
//        }
//        return Lists.newArrayList();
//    }
//
//    @Override
//    protected List<JudgeNoPassJobHandler> getJudgeNoPassJobHandler() {
//        if (CollectionUtils.isNotEmpty(judgeNoPassJobHandlers)) {
//            return judgeNoPassJobHandlers;
//        }
//        return Lists.newArrayList();
//    }

    public EScheduleType getScheduleType() {
        return EScheduleType.NORMAL_SCHEDULE;
    }

    @Override
    public String getSchedulerName() {
        return getScheduleType().name();
    }
}
