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

import com.dtstack.taier.common.enums.EScheduleJobDistributeType;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.server.distribute.ScheduleJobDistributeContext;
import com.dtstack.taier.scheduler.server.distribute.ScheduleJobDistributeStrategy;
import com.dtstack.taier.scheduler.server.distribute.TaskTypeCycTimeScheduleJobDistributeStrategy;
import com.dtstack.taier.scheduler.zookeeper.ZkService;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScheduleJobServiceTest {

    private ScheduleJobService scheduleJobService;

    private ScheduleJobJobService scheduleJobJobService;

    private ScheduleJobExpandService scheduleJobExpandService;

    private StubDistributeStrategy defaultStrategy;

    private StubDistributeStrategy taskTypeCycTimeStrategy;

    @Before
    public void setUp() {
        scheduleJobService = spy(new ScheduleJobService());
        scheduleJobJobService = mock(ScheduleJobJobService.class);
        scheduleJobExpandService = mock(ScheduleJobExpandService.class);
        EnvironmentContext environmentContext = mock(EnvironmentContext.class);

        when(environmentContext.getBatchInsertSize()).thenReturn(10);
        when(environmentContext.getBatchJobJobInsertSize()).thenReturn(10);
        when(environmentContext.getBuildJobErrorRetry()).thenReturn(1);
        doReturn(true).when(scheduleJobService).saveBatch(any(Collection.class));
        when(scheduleJobJobService.saveBatch(any(Collection.class))).thenReturn(true);
        when(scheduleJobExpandService.saveBatch(any(Collection.class))).thenReturn(true);

        defaultStrategy = new StubDistributeStrategy(EScheduleJobDistributeType.DEFAULT, "default-node");
        taskTypeCycTimeStrategy = new StubDistributeStrategy(EScheduleJobDistributeType.TASK_TYPE_CYCTIME, "cyc-node");

        ReflectionTestUtils.setField(scheduleJobService, "environmentContext", environmentContext);
        ReflectionTestUtils.setField(scheduleJobService, "scheduleJobJobService", scheduleJobJobService);
        ReflectionTestUtils.setField(scheduleJobService, "scheduleJobExpandService", scheduleJobExpandService);
        ReflectionTestUtils.setField(scheduleJobService, "distributeStrategies", Lists.newArrayList(defaultStrategy, taskTypeCycTimeStrategy));
    }

    @Test
    public void testInsertJobListUseTaskTypeCycTimeStrategy() {
        ScheduleJobDetails jobDetails = buildJobDetails("job-1");
        ScheduleJobDistributeContext distributeContext = new ScheduleJobDistributeContext();

        Long minJobId = scheduleJobService.insertJobList(Lists.newArrayList(jobDetails),
                EScheduleType.NORMAL_SCHEDULE.getType(),
                EScheduleJobDistributeType.TASK_TYPE_CYCTIME,
                distributeContext);

        Assert.assertEquals("cyc-node", jobDetails.getScheduleJob().getNodeAddress());
        Assert.assertEquals(Long.valueOf(1L), minJobId);
        Assert.assertEquals(0, defaultStrategy.getInvokeCount());
        Assert.assertEquals(1, taskTypeCycTimeStrategy.getInvokeCount());
        Assert.assertSame(distributeContext, taskTypeCycTimeStrategy.getLastContext());
        verify(scheduleJobService, times(1)).saveBatch(any(Collection.class));
        verify(scheduleJobJobService, times(1)).saveBatch(any(Collection.class));
        verify(scheduleJobExpandService, times(1)).saveBatch(any(Collection.class));
    }

    @Test
    public void testInsertJobListUseDefaultStrategyByCompatibleMethod() {
        ScheduleJobDetails jobDetails = buildJobDetails("job-2");

        scheduleJobService.insertJobList(Lists.newArrayList(jobDetails), EScheduleType.FILL_DATA.getType());

        Assert.assertEquals("default-node", jobDetails.getScheduleJob().getNodeAddress());
        Assert.assertEquals(1, defaultStrategy.getInvokeCount());
        Assert.assertEquals(0, taskTypeCycTimeStrategy.getInvokeCount());
    }

    @Test
    public void testInsertJobListFallbackToDefaultWhenStrategyNotFound() {
        ReflectionTestUtils.setField(scheduleJobService, "distributeStrategies", Lists.newArrayList(defaultStrategy));
        ScheduleJobDetails jobDetails = buildJobDetails("job-3");

        scheduleJobService.insertJobList(Lists.newArrayList(jobDetails),
                EScheduleType.NORMAL_SCHEDULE.getType(),
                EScheduleJobDistributeType.TASK_TYPE_CYCTIME,
                new ScheduleJobDistributeContext());

        Assert.assertEquals("default-node", jobDetails.getScheduleJob().getNodeAddress());
        Assert.assertEquals(1, defaultStrategy.getInvokeCount());
    }

    @Test(expected = TaierDefineException.class)
    public void testInsertJobListThrowWhenStrategyDoesNotAssignNode() {
        ReflectionTestUtils.setField(scheduleJobService, "distributeStrategies",
                Lists.newArrayList(new StubDistributeStrategy(EScheduleJobDistributeType.TASK_TYPE_CYCTIME, null)));

        scheduleJobService.insertJobList(Lists.newArrayList(buildJobDetails("job-4")),
                EScheduleType.NORMAL_SCHEDULE.getType(),
                EScheduleJobDistributeType.TASK_TYPE_CYCTIME,
                new ScheduleJobDistributeContext());
    }

    @Test
    public void testInsertJobListKeepTaskTypeCycTimeBalancedAcrossMultipleCalls() {
        TaskTypeCycTimeScheduleJobDistributeStrategy realStrategy = new TaskTypeCycTimeScheduleJobDistributeStrategy();
        ZkService zkService = mock(ZkService.class);
        List<String> aliveNodes = Lists.newArrayList("nodeAddress1", "nodeAddress2", "nodeAddress3", "nodeAddress4");
        when(zkService.getAliveBrokersChildren()).thenReturn(aliveNodes);
        ReflectionTestUtils.setField(realStrategy, "zkService", zkService);
        ReflectionTestUtils.setField(scheduleJobService, "distributeStrategies", Lists.newArrayList(defaultStrategy, realStrategy));

        ScheduleJobDistributeContext distributeContext = new ScheduleJobDistributeContext();
        List<ScheduleJobDetails> allJobs = new ArrayList<>();
        long jobId = 1L;
        for (int i = 0; i < 3; i++) {
            List<ScheduleJobDetails> batchJobs = Lists.newArrayList();
            for (int j = 0; j < 5; j++) {
                batchJobs.add(buildJobDetails("job-group-a-" + i + "-" + j, jobId++, 1, "20260617000000"));
            }
            for (int j = 0; j < 3; j++) {
                batchJobs.add(buildJobDetails("job-group-b-" + i + "-" + j, jobId++, 2, "20260617010000"));
            }

            scheduleJobService.insertJobList(batchJobs,
                    EScheduleType.NORMAL_SCHEDULE.getType(),
                    EScheduleJobDistributeType.TASK_TYPE_CYCTIME,
                    distributeContext);
            allJobs.addAll(batchJobs);
        }

        assertGroupNodeCountBalanced(allJobs, aliveNodes, 1, "202606170000");
        assertGroupNodeCountBalanced(allJobs, aliveNodes, 2, "202606170100");
        Assert.assertEquals(0, defaultStrategy.getInvokeCount());
    }

    private static ScheduleJobDetails buildJobDetails(String jobId) {
        return buildJobDetails(jobId, 1L, 1, "20260617000000");
    }

    private static ScheduleJobDetails buildJobDetails(String jobId, Long id, Integer taskType, String cycTime) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setId(id);
        scheduleJob.setJobId(jobId);
        scheduleJob.setJobName(jobId);
        scheduleJob.setCycTime(cycTime);
        scheduleJob.setTaskType(taskType);

        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey(jobId);

        ScheduleJobDetails scheduleJobDetails = new ScheduleJobDetails();
        scheduleJobDetails.setScheduleJob(scheduleJob);
        scheduleJobDetails.setJobJobList(Lists.newArrayList(scheduleJobJob));
        return scheduleJobDetails;
    }

    private static void assertGroupNodeCountBalanced(List<ScheduleJobDetails> allJobs,
                                                     List<String> aliveNodes,
                                                     Integer taskType,
                                                     String cycMinute) {
        Map<String, Integer> nodeJobCount = new HashMap<>();
        for (String aliveNode : aliveNodes) {
            nodeJobCount.put(aliveNode, 0);
        }

        for (ScheduleJobDetails scheduleJobDetails : allJobs) {
            ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
            if (taskType.equals(scheduleJob.getTaskType()) && scheduleJob.getCycTime().startsWith(cycMinute)) {
                String nodeAddress = scheduleJob.getNodeAddress();
                Assert.assertTrue("unexpected node address: " + nodeAddress, nodeJobCount.containsKey(nodeAddress));
                nodeJobCount.put(nodeAddress, nodeJobCount.get(nodeAddress) + 1);
            }
        }

        int minCount = Integer.MAX_VALUE;
        int maxCount = Integer.MIN_VALUE;
        for (Integer count : nodeJobCount.values()) {
            minCount = Math.min(minCount, count);
            maxCount = Math.max(maxCount, count);
        }
        Assert.assertTrue("node count should be balanced within 1, actual: " + nodeJobCount, maxCount - minCount <= 1);
    }

    private static class StubDistributeStrategy implements ScheduleJobDistributeStrategy {

        private final EScheduleJobDistributeType distributeType;

        private final String nodeAddress;

        private int invokeCount;

        private ScheduleJobDistributeContext lastContext;

        private StubDistributeStrategy(EScheduleJobDistributeType distributeType, String nodeAddress) {
            this.distributeType = distributeType;
            this.nodeAddress = nodeAddress;
        }

        @Override
        public EScheduleJobDistributeType distributeType() {
            return distributeType;
        }

        @Override
        public Map<ScheduleJobDetails, String> distribute(List<ScheduleJobDetails> scheduleJobDetails,
                                                          Integer scheduleType,
                                                          ScheduleJobDistributeContext distributeContext) {
            invokeCount++;
            lastContext = distributeContext;
            Map<ScheduleJobDetails, String> jobNodeMap = new IdentityHashMap<>();
            for (ScheduleJobDetails scheduleJobDetail : scheduleJobDetails) {
                jobNodeMap.put(scheduleJobDetail, nodeAddress);
            }
            return jobNodeMap;
        }

        private int getInvokeCount() {
            return invokeCount;
        }

        private ScheduleJobDistributeContext getLastContext() {
            return lastContext;
        }
    }
}
