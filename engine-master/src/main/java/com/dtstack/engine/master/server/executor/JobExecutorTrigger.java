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

package com.dtstack.engine.master.server.executor;

import com.dtstack.engine.mapper.ScheduleJobMapper;
import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.server.queue.QueueInfo;
import com.dtstack.engine.master.server.scheduler.JobRichOperator;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class JobExecutorTrigger implements DisposableBean, ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorTrigger.class);

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private CronJobExecutor cronJobExecutor;

    @Autowired
    private FillJobExecutor fillJobExecutor;

    @Autowired
    private RestartJobExecutor restartJobExecutor;

    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private ZkService zkService;

    @Autowired
    private EnvironmentContext environmentContext;

    private List<AbstractJobExecutor> executors = new ArrayList<>(EScheduleType.values().length);

    private ExecutorService executorService;


    /**
     * 同步所有节点的 type类型下的 job实例信息
     * key1: nodeAddress,
     * key2: scheduleType
     */
    public Map<String, Map<Integer, QueueInfo>> getAllNodesJobQueueInfo() {

        List<String> allNodeAddress = zkService.getAliveBrokersChildren();
        Pair<String, String> cycTime = jobRichOperator.getCycTimeLimit();
        Map<String, Map<Integer, QueueInfo>> allNodeJobInfo = Maps.newHashMap();
        for (String nodeAddress : allNodeAddress) {
            if (StringUtils.isBlank(nodeAddress)) {
                continue;
            }
            allNodeJobInfo.computeIfAbsent(nodeAddress, na -> {
                Map<Integer, QueueInfo> nodeJobInfo = Maps.newHashMap();
                executors.forEach(executor -> nodeJobInfo.computeIfAbsent(executor.getScheduleType().getType(), k -> {
                    int queueSize = scheduleJobMapper.countTasksByCycTimeTypeAndAddress(nodeAddress, executor.getScheduleType().getType(), cycTime.getLeft(), cycTime.getRight());
                    QueueInfo queueInfo = new QueueInfo();
                    queueInfo.setSize(queueSize);
                    return queueInfo;
                }));
                return nodeJobInfo;
            });
        }
        return allNodeJobInfo;
    }

    @Override
    public void destroy() throws Exception {
        for (AbstractJobExecutor executor : executors) {
            executor.stop();
        }

        executorService.shutdownNow();
    }

    public void recoverOtherNode() {
        for (AbstractJobExecutor executor : executors) {
            executor.recoverOtherNode();
        }
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        LOGGER.info("Initializing " + this.getClass().getName());
        if (!environmentContext.openJobSchedule()) {
            LOGGER.info("job schedule is not open!!!");
            return;
        }
        executors.add(fillJobExecutor);
        executors.add(cronJobExecutor);
        executors.add(restartJobExecutor);

        executorService = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("ExecutorDealer"));
        for (AbstractJobExecutor executor : executors) {
            executorService.submit(executor);
        }
    }
}
