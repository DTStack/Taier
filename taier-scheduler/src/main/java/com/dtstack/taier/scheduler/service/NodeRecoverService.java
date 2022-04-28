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

package com.dtstack.taier.scheduler.service;

import com.dtstack.taier.common.enums.EJobCacheStage;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.jobdealer.JobDealer;
import com.dtstack.taier.scheduler.server.listener.JobSchedulerListener;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/29
 */
@Service
public class NodeRecoverService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeRecoverService.class);

    @Autowired
    private JobSchedulerListener jobExecutorTrigger;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobCacheService ScheduleJobCacheService;

    @Autowired
    private JobDealer jobDealer;

    /**
     * 接收 master 节点容灾后的消息
     */
    public void masterTriggerNode() {
        LOGGER.info("--- accept masterTriggerNode");
        try {
            jobExecutorTrigger.recoverOtherNode();
            LOGGER.info("--- deal recoverOtherNode done ------");
            recoverJobCaches();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void recoverJobCaches() {
        String localAddress = environmentContext.getLocalAddress();
        try {
            long startId = 0L;
            while (true) {
                List<ScheduleEngineJobCache> jobCaches = ScheduleJobCacheService.listByStage(startId, localAddress, EJobCacheStage.SUBMITTED.getStage(),null);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                List<JobClient> afterJobClients = new ArrayList<>(jobCaches.size());
                for (ScheduleEngineJobCache jobCache : jobCaches) {
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        afterJobClients.add(jobClient);
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        //数据转换异常--打日志
                        jobDealer.dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + ExceptionUtil.getErrorMessage(e));
                    }
                }
                if (CollectionUtils.isNotEmpty(afterJobClients)) {
                    jobDealer.afterSubmitJobVast(afterJobClients);
                }
            }
        } catch (Exception e) {
            LOGGER.error("----broker:{} RecoverDealer error:", localAddress, e);
        }
    }

}
