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

package com.dtstack.taier.scheduler.server.listener;

import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.scheduler.server.queue.QueueInfo;
import com.dtstack.taier.scheduler.server.scheduler.AbstractJobSummitScheduler;
import com.dtstack.taier.scheduler.server.scheduler.CycleJobScheduler;
import com.dtstack.taier.scheduler.server.scheduler.FillDataJobScheduler;
import com.dtstack.taier.scheduler.server.scheduler.RestartJobScheduler;
import com.dtstack.taier.scheduler.zookeeper.ZkService;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class JobSchedulerListener implements DisposableBean, ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerListener.class);

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private RestartJobScheduler restartJobScheduler;

    @Autowired
    private FillDataJobScheduler fillDataJobScheduler;

    @Autowired
    private CycleJobScheduler cycleJobScheduler;

    @Autowired
    private ZkService zkService;

    @Autowired
    private EnvironmentContext environmentContext;

    private List<AbstractJobSummitScheduler> executors = new ArrayList<>(EScheduleType.values().length);

    private ExecutorService executorService;


    /**
     * 同步所有节点的 type类型下的 job实例信息
     * key1: nodeAddress,
     * key2: scheduleType
     */
    public Map<String, Map<Integer, QueueInfo>> getAllNodesJobQueueInfo() {

        List<String> allNodeAddress = zkService.getAliveBrokersChildren();
        Pair<String, String> cycTime = getCycTimeLimit();
        Map<String, Map<Integer, QueueInfo>> allNodeJobInfo = Maps.newHashMap();
        for (String nodeAddress : allNodeAddress) {
            if (StringUtils.isBlank(nodeAddress)) {
                continue;
            }
            allNodeJobInfo.computeIfAbsent(nodeAddress, na -> {
                Map<Integer, QueueInfo> nodeJobInfo = Maps.newHashMap();
                for (EScheduleType scheduleType : EScheduleType.values()) {
                    executors.forEach(executor -> nodeJobInfo.computeIfAbsent(scheduleType.getType(), k -> {
                        int queueSize = scheduleJobMapper.countTasksByCycTimeTypeAndAddress(nodeAddress,scheduleType.getType(), cycTime.getLeft(), cycTime.getRight());
                        QueueInfo queueInfo = new QueueInfo();
                        queueInfo.setSize(queueSize);
                        return queueInfo;
                    }));
                }
                return nodeJobInfo;
            });
        }
        return allNodeJobInfo;
    }

    public Pair<String, String> getCycTimeLimit() {
        Integer dayGap = environmentContext.getJobCycTimeGap();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayGap-1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String startTime = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, dayGap+1);
        String endTime = sdf.format(calendar.getTime());
        return new ImmutablePair<>(startTime, endTime);
    }

    @Override
    public void destroy() throws Exception {
        for (AbstractJobSummitScheduler scheduler : executors) {
            scheduler.stop();
        }

        executorService.shutdownNow();
    }

    public void recoverOtherNode() {
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        LOGGER.info("Initializing " + this.getClass().getName());
        if (!environmentContext.isOpenJobSchedule()) {
            LOGGER.info("job schedule is not open!!!");
            return;
        }
        executors.add(fillDataJobScheduler);
        executors.add(cycleJobScheduler);
        executors.add(restartJobScheduler);

        executorService = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("ExecutorDealer"));
        for (AbstractJobSummitScheduler scheduler : executors) {
            executorService.submit(scheduler);
        }
    }
}
