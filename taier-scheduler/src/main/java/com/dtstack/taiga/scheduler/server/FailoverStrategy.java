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

package com.dtstack.taiga.scheduler.server;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.enums.EJobCacheStage;
import com.dtstack.taiga.common.enums.EScheduleType;
import com.dtstack.taiga.common.env.EnvironmentContext;
import com.dtstack.taiga.common.util.GenerateErrorMsgUtil;
import com.dtstack.taiga.dao.domain.EngineJobCache;
import com.dtstack.taiga.dao.domain.po.SimpleScheduleJobPO;
import com.dtstack.taiga.dao.mapper.ScheduleJobMapper;
import com.dtstack.taiga.dao.mapper.ScheduleJobOperatorRecordMapper;
import com.dtstack.taiga.pluginapi.CustomThreadFactory;
import com.dtstack.taiga.pluginapi.enums.RdosTaskStatus;
import com.dtstack.taiga.pluginapi.exception.ExceptionUtil;
import com.dtstack.taiga.pluginapi.http.PoolHttpClient;
import com.dtstack.taiga.scheduler.enums.JobPhaseStatus;
import com.dtstack.taiga.scheduler.server.builder.CycleJobBuilder;
import com.dtstack.taiga.scheduler.service.EngineJobCacheService;
import com.dtstack.taiga.scheduler.service.NodeRecoverService;
import com.dtstack.taiga.scheduler.zookeeper.ZkService;
import com.dtstack.taiga.scheduler.zookeeper.data.BrokerHeartNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class FailoverStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailoverStrategy.class);

    private BlockingQueue<String> queue = new LinkedBlockingDeque<>();

    private static final String MASTER_TRIGGER_NODE = "/node/nodeRecover/masterTriggerNode";

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ZkService zkService;

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private NodeRecoverService nodeRecoverService;

    @Autowired
    private CycleJobBuilder cycleJobBuilder;

    @Autowired
    private JobGraphBuilderTrigger jobGraphBuilderTrigger;

    @Autowired
    private JobPartitioner jobPartitioner;

    @Autowired
    private EngineJobCacheService engineJobCacheService;

    @Autowired
    private ScheduleJobMapper rdosEngineBatchJobDao;

    @Autowired
    private ScheduleJobOperatorRecordMapper scheduleJobOperatorRecordMapper;

    private FaultTolerantDealer faultTolerantDealer = new FaultTolerantDealer();

    private ExecutorService masterNodeDealer;

    private boolean currIsMaster = false;

    private FailoverStrategy() {
        masterNodeDealer = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName()));
    }

    public void setIsMaster(boolean isMaster) {
        if (isMaster && !currIsMaster) {
            currIsMaster = true;

            jobGraphBuilderTrigger.dealMaster(true);
            LOGGER.warn("---start jobMaster change listener------");

            if (masterNodeDealer.isShutdown()) {
                masterNodeDealer = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName()));
            }
            masterNodeDealer.submit(faultTolerantDealer);
            masterNodeDealer.submit(new JobGraphChecker());
            LOGGER.warn("---start master node dealer thread------");
        } else if (!isMaster && currIsMaster) {
            currIsMaster = false;

            jobGraphBuilderTrigger.dealMaster(false);
            LOGGER.warn("---stop jobMaster change listener------");

            if (faultTolerantDealer != null) {
                faultTolerantDealer.stop();
            }
            masterNodeDealer.shutdownNow();
            LOGGER.warn("---stop master node dealer thread------");
        }
    }

    public void dataMigration(String node) {
        if (StringUtils.isBlank(node)) {
            return;
        }
        try {
            queue.put(node);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    class JobGraphChecker implements Runnable {

        @Override
        public void run() {
            try {
                //判断当天jobGraph是否已经生成
                SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
                String currDayStr = sdfDay.format(Calendar.getInstance().getTime());
                cycleJobBuilder.buildTaskJobGraph(currDayStr);
            } catch (Exception e) {
                LOGGER.error("----jobGraphChecker error:", e);
            }
        }
    }

    /**
     * 容错处理
     */
    class FaultTolerantDealer implements Runnable {

        private volatile boolean isRun = true;

        @Override
        public void run() {
            try {
                while (isRun) {
                    String node = queue.take();
                    LOGGER.warn("----- nodeAddress:{} node disaster recovery tasks begin to recover----", node);

                    faultTolerantRecoverBatchJob(node);
                    faultTolerantRecoverJobCache(node);

                    List<String> aliveNodes = zkService.getAliveBrokersChildren();
                    for (String nodeAddress : aliveNodes) {
                        LOGGER.warn("----- nodeAddress:{} masterTriggerNode -----", nodeAddress);
                        if (nodeAddress.equals(environmentContext.getLocalAddress())) {
                            nodeRecoverService.masterTriggerNode();
                            continue;
                        }

                        PoolHttpClient.post(String.format("http://%s/%s", node,MASTER_TRIGGER_NODE), null);
                    }
                    LOGGER.warn("----- nodeAddress:{} node disaster recovery task ends and resumes-----", node);
                }
            } catch (Exception e) {
                LOGGER.error("----faultTolerantRecover error:", e);
            }
        }

        public void stop() {
            isRun = false;
        }
    }

    public void faultTolerantRecoverBatchJob(String nodeAddress) {
        try {
            //再次判断broker是否alive
            BrokerHeartNode brokerHeart = zkService.getBrokerHeartNode(nodeAddress);
            if (brokerHeart.getAlive()) {
                return;
            }

            //节点容灾恢复任务
            LOGGER.warn("----- nodeAddress:{} BatchJob mission begins to resume----", nodeAddress);
            long startId = 0L;
            while (true) {
                List<SimpleScheduleJobPO> jobs = scheduleJobMapper.listSimpleJobByStatusAddress(startId, RdosTaskStatus.getUnfinishedStatuses(), nodeAddress);
                if (CollectionUtils.isEmpty(jobs)) {
                    break;
                }
                Set<String> cronJobIds = new HashSet<>();
                Set<String> fillJobIds =  new HashSet<>();
                List<String> phaseStatus = Lists.newArrayList();
                for (SimpleScheduleJobPO batchJob : jobs) {
                    if (EScheduleType.NORMAL_SCHEDULE.getType().equals(batchJob.getType())) {
                        cronJobIds.add(batchJob.getJobId());
                        LOGGER.info("----- nodeAddress:{} distributeBatchJobs {} NORMAL_SCHEDULE -----", nodeAddress, batchJob.getJobId());
                    } else {
                        fillJobIds.add(batchJob.getJobId());
                        LOGGER.info("----- nodeAddress:{} distributeBatchJobs {} FILL_DATA -----", nodeAddress, batchJob.getJobId());
                    }
                    if (JobPhaseStatus.JOIN_THE_TEAM.getCode().equals(batchJob.getPhaseStatus())) {
                        phaseStatus.add(batchJob.getJobId());
                    }
                    startId = batchJob.getId();
                }
                distributeBatchJobs(cronJobIds, EScheduleType.NORMAL_SCHEDULE.getType());
                distributeBatchJobs(fillJobIds, EScheduleType.FILL_DATA.getType());
                updatePhaseStatus(phaseStatus);
            }

            //在迁移任务的时候，可能出现要迁移的节点也宕机了，任务没有正常接收需要再次恢复（由HearBeatCheckListener监控）。
            List<SimpleScheduleJobPO> jobs = scheduleJobMapper.listSimpleJobByStatusAddress(0L, RdosTaskStatus.getUnfinishedStatuses(), nodeAddress);
            if (CollectionUtils.isNotEmpty(jobs)) {
                zkService.updateSynchronizedLocalBrokerHeartNode(nodeAddress, BrokerHeartNode.initNullBrokerHeartNode(), true);
            }

            LOGGER.warn("----- nodeAddress:{} BatchJob mission end recovery-----", nodeAddress);
        } catch (Exception e) {
            LOGGER.error("----nodeAddress:{} faultTolerantRecoverBatchJob error:", nodeAddress, e);
        }
    }


    private void updatePhaseStatus(List<String> phaseStatus) {
        if (CollectionUtils.isNotEmpty(phaseStatus)) {
            LOGGER.info("----- updatePhaseStatus {} -----", JSONObject.toJSONString(phaseStatus));
            scheduleJobMapper.updateListPhaseStatus(phaseStatus, JobPhaseStatus.CREATE.getCode());
        }
    }

    /**
     * Ps：jobIds  为 batchJob 表的 id 字段（非job_id字段）
     */
    private void distributeBatchJobs(Set<String> jobIds, Integer scheduleType) {
        if (jobIds.isEmpty()) {
            return;
        }

        Iterator<String> jobIdsIterator = jobIds.iterator();

        //任务多节点分发，每个节点要分发的任务量
        Map<String, List<String>> nodeJobs = Maps.newHashMap();

        Map<String, Integer> nodeJobSize = jobPartitioner.computeBatchJobSize(scheduleType, jobIds.size());
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            while (nodeSize > 0 && jobIdsIterator.hasNext()) {
                nodeSize--;
                List<String> nodeJobIds = nodeJobs.computeIfAbsent(nodeAddress, k -> Lists.newArrayList());
                nodeJobIds.add(jobIdsIterator.next());
            }
        }

        updateBatchJobs(nodeJobs);
    }

    private void updateBatchJobs(Map<String, List<String>> nodeJobs) {
        for (Map.Entry<String, List<String>> nodeEntry : nodeJobs.entrySet()) {
            if (nodeEntry.getValue().isEmpty()) {
                continue;
            }
            scheduleJobMapper.updateNodeAddress(nodeEntry.getKey(), nodeEntry.getValue());
            scheduleJobOperatorRecordMapper.updateNodeAddress(nodeEntry.getKey(),nodeEntry.getValue());
            LOGGER.info("jobIds:{} failover to address:{}", nodeEntry.getValue(), nodeEntry.getKey());
        }
    }

    public void faultTolerantRecoverJobCache(String nodeAddress) {
        try {
            //再次判断broker是否alive
            BrokerHeartNode brokerHeart = zkService.getBrokerHeartNode(nodeAddress);
            if (brokerHeart.getAlive()) {
                return;
            }

            //节点容灾恢复任务
            LOGGER.warn("----- nodeAddress:{} JobCache mission begins to resume----", nodeAddress);
            long startId = 0L;
            while (true) {
                List<EngineJobCache> jobCaches = engineJobCacheService.listByStage(startId, nodeAddress, null, null);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                Map<String, List<String>> jobResources = Maps.newHashMap();
                List<String> submittedJobs = Lists.newArrayList();
                for (EngineJobCache jobCache : jobCaches) {
                    try {
                        if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
                            List<String> jobIds = jobResources.computeIfAbsent(jobCache.getJobResource(), k -> Lists.newArrayList());
                            jobIds.add(jobCache.getJobId());
                        } else {
                            submittedJobs.add(jobCache.getJobId());
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        LOGGER.error("faultTolerantRecoverJobCache {} error", jobCache.getJobId(),e);
                        dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + ExceptionUtil.getErrorMessage(e));
                    }
                }
                distributeQueueJobs(jobResources);
                distributeSubmittedJobs(submittedJobs);
            }
            //在迁移任务的时候，可能出现要迁移的节点也宕机了，任务没有正常接收
            List<EngineJobCache> jobCaches = engineJobCacheService.listByStage(0L, nodeAddress, null, null);
            if (CollectionUtils.isNotEmpty(jobCaches)) {
                //如果尚有任务未迁移完成，重置 nodeAddress 继续恢复
                zkService.updateSynchronizedLocalBrokerHeartNode(nodeAddress, BrokerHeartNode.initNullBrokerHeartNode(), true);
            }
            LOGGER.warn("----- nodeAddress:{} JobCache mission end recovery-----", nodeAddress);
        } catch (Exception e) {
            LOGGER.error("----nodeAddress:{} faultTolerantRecoverJobCache error:", nodeAddress, e);
        }
    }

    private void distributeQueueJobs(Map<String, List<String>> jobResources) {
        if (jobResources.isEmpty()) {
            return;
        }
        //任务多节点分发，每个节点要分发的任务量
        Map<String, List<String>> nodeJobs = Maps.newHashMap();
        for (Map.Entry<String, List<String>> jobResourceEntry : jobResources.entrySet()) {
            String jobResource = jobResourceEntry.getKey();
            List<String> jobIds = jobResourceEntry.getValue();
            if (jobIds.isEmpty()) {
                continue;
            }
            Map<String, Integer> jobCacheSizeInfo = jobPartitioner.computeJobCacheSize(jobResource, jobIds.size());
            Iterator<String> jobIdsIterator = jobIds.iterator();
            for (Map.Entry<String, Integer> jobCacheSizeEntry : jobCacheSizeInfo.entrySet()) {
                String nodeAddress = jobCacheSizeEntry.getKey();
                int nodeSize = jobCacheSizeEntry.getValue();
                while (nodeSize > 0 && jobIdsIterator.hasNext()) {
                    nodeSize--;
                    List<String> nodeJobIds = nodeJobs.computeIfAbsent(nodeAddress, k -> Lists.newArrayList());
                    nodeJobIds.add(jobIdsIterator.next());
                }
            }
        }
        updateJobCaches(nodeJobs, EJobCacheStage.DB.getStage());
    }

    private void distributeSubmittedJobs(List<String> jobs) {
        if (jobs.isEmpty()) {
            return;
        }
        List<String> aliveNodes = zkService.getAliveBrokersChildren();
        int avg = jobs.size() / aliveNodes.size() + 1;
        //任务多节点分发，每个节点要分发的任务量
        Map<String, List<String>> nodeJobs = Maps.newHashMap();
        Iterator<String> jobsIt = jobs.iterator();
        int size = avg;
        for (String nodeAddress : aliveNodes) {
            while (size > 0 && jobsIt.hasNext()) {
                size--;
                String jobId = jobsIt.next();
                List<String> nodeJobIds = nodeJobs.computeIfAbsent(nodeAddress, k -> Lists.newArrayList());
                nodeJobIds.add(jobId);
            }
        }
        updateJobCaches(nodeJobs, EJobCacheStage.SUBMITTED.getStage());
    }

    private void updateJobCaches(Map<String, List<String>> nodeJobs, Integer stage) {
        for (Map.Entry<String, List<String>> nodeEntry : nodeJobs.entrySet()) {
            if (nodeEntry.getValue().isEmpty()) {
                continue;
            }

            engineJobCacheService.updateNodeAddressFailover(nodeEntry.getKey(), nodeEntry.getValue(), stage);
            LOGGER.info("jobIds:{} failover to address:{}, set stage={}", nodeEntry.getValue(), nodeEntry.getKey(), stage);
        }
    }

    /**
     * master 节点分发任务失败
     * @param taskId
     */
    public void dealSubmitFailJob(String jobId, String errorMsg){
        engineJobCacheService.deleteByJobId(jobId);
        rdosEngineBatchJobDao.jobFail(jobId, RdosTaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(errorMsg));
        LOGGER.info("jobId:{} update job status:{}, job is finished.", jobId, RdosTaskStatus.SUBMITFAILD.getStatus());
    }
}

