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
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.util.GenerateErrorMsgUtil;
import com.dtstack.taier.common.util.SystemPropertyUtil;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.domain.po.SimpleScheduleJobPO;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.enums.JobPhaseStatus;
import com.dtstack.taier.scheduler.jobdealer.cache.ShardCache;
import com.dtstack.taier.scheduler.jobdealer.resource.JobComputeResourcePlain;
import com.dtstack.taier.scheduler.server.queue.GroupInfo;
import com.dtstack.taier.scheduler.server.queue.GroupPriorityQueue;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/29
 */
@Component
public class JobDealer implements InitializingBean, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobDealer.class);

    private ApplicationContext applicationContext;

    @Autowired
    private JobComputeResourcePlain jobComputeResourcePlain;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private ScheduleJobCacheService scheduleJobCacheService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private JobSubmittedDealer jobSubmittedDealer;

    @Autowired
    private WorkerOperator workerOperator;

    /**
     * key: jobResource, 计算引擎类型
     * value: queue
     */
    private Map<String, GroupPriorityQueue> priorityQueueMap = Maps.newConcurrentMap();

    private ExecutorService executors = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new CustomThreadFactory("taskSubmittedDealer"));

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing " + this.getClass().getName());
        SystemPropertyUtil.setHadoopUserName(environmentContext.getHadoopUserName());

        executors.execute(jobSubmittedDealer);

        ExecutorService recoverExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName()));
        recoverExecutor.submit(new RecoverDealer());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取所有节点的队列大小信息（job已经submitted的除外）
     * key1: nodeAddress,
     * key2: jobResource
     */
    public Map<String, Map<String, GroupInfo>> getAllNodesGroupQueueInfo() {
        List<String> allNodeAddress = scheduleJobCacheService.getAllNodeAddress();
        Map<String, Map<String, GroupInfo>> allNodeGroupInfo = Maps.newHashMap();
        for (String nodeAddress : allNodeAddress) {
            if (StringUtils.isBlank(nodeAddress)) {
                continue;
            }
            allNodeGroupInfo.computeIfAbsent(nodeAddress, na -> {
                Map<String, GroupInfo> nodeGroupInfo = Maps.newHashMap();
                priorityQueueMap.forEach((jobResource, priorityQueue) -> {
                    int groupSize = scheduleJobCacheService.countByStage(jobResource, EJobCacheStage.unSubmitted(), nodeAddress);
                    Long minPriority = scheduleJobCacheService.minPriorityByStage(jobResource, Lists.newArrayList(EJobCacheStage.PRIORITY.getStage(), EJobCacheStage.LACKING.getStage()), nodeAddress);
                    minPriority = minPriority == null ? 0 : minPriority;
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setSize(groupSize);
                    groupInfo.setPriority(minPriority);
                    nodeGroupInfo.put(jobResource, groupInfo);
                });
                return nodeGroupInfo;
            });
        }
        return allNodeGroupInfo;
    }

    /**
     * 提交优先级队列->最终提交到具体执行组件
     */
    public void addSubmitJob(JobClient jobClient) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
        jobClient.setCallBack((jobStatus) -> {
            updateJobStatus(jobClient.getJobId(), jobStatus);
        });

        //加入节点的优先级队列
        this.addGroupPriorityQueue(jobResource, jobClient, true, true);
    }

    /**
     * job cache 表已经存在
     * @param jobClients
     */
    private void addSubmitJobVast(List<JobClient> jobClients) {
        List<String> jobIds = jobClients.stream().map(JobClient::getJobId).collect(Collectors.toList());
        updateCacheBatch(jobIds, EJobCacheStage.DB.getStage());
        scheduleJobService.updateJobStatusByJobIds(jobIds, TaskStatus.WAITENGINE.getStatus(),null);
        LOGGER.info(" addSubmitJobBatch jobId:{} update", JSONObject.toJSONString(jobIds));
        for (JobClient jobClient : jobClients) {
            jobClient.setCallBack((jobStatus) -> {
                updateJobStatus(jobClient.getJobId(), jobStatus);
            });
            //加入节点的优先级队列
            this.addGroupPriorityQueue(jobComputeResourcePlain.getJobResource(jobClient), jobClient, true, false);
        }
    }

    /**
     * 容灾时对已经提交到执行组件的任务，进行恢复
     */
    public void afterSubmitJobVast(List<JobClient> jobClients) {
        List<String> jobIds = jobClients.stream().map(JobClient::getJobId).collect(Collectors.toList());
        updateCacheBatch(jobIds, EJobCacheStage.SUBMITTED.getStage());
        LOGGER.info(" afterSubmitJobBatch jobId:{} update", JSONObject.toJSONString(jobIds));
        for (String taskId : jobIds) {
            shardCache.updateLocalMemTaskStatus(taskId, TaskStatus.SUBMITTED.getStatus());
        }
    }

    public boolean addGroupPriorityQueue(String jobResource, JobClient jobClient, boolean judgeBlock, boolean insert) {
        try {
            GroupPriorityQueue groupPriorityQueue = getGroupPriorityQueue(jobResource);
            boolean rs = groupPriorityQueue.add(jobClient, judgeBlock, insert);
            if (!rs) {
                saveCache(jobClient, jobResource, EJobCacheStage.DB.getStage(), insert);
            }
            return rs;
        } catch (Exception e) {
            LOGGER.error("", e);
            dealSubmitFailJob(jobClient.getJobId(), e.toString());
            return false;
        }
    }

    public boolean addRestartJob(JobClient jobClient) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
        GroupPriorityQueue groupPriorityQueue = getGroupPriorityQueue(jobResource);
        return groupPriorityQueue.addRestartJob(jobClient);
    }

    public GroupPriorityQueue getGroupPriorityQueue(String jobResource) {
        GroupPriorityQueue groupPriorityQueue = priorityQueueMap.computeIfAbsent(jobResource, k -> GroupPriorityQueue.builder()
                .setApplicationContext(applicationContext)
                .setJobResource(jobResource)
                .setJobDealer(this)
                .build());
        return groupPriorityQueue;
    }

    public void updateJobStatus(String jobId, Integer status) {
        scheduleJobService.updateJobStatusByJobIds(Lists.newArrayList(jobId), status,null);
        LOGGER.info("jobId:{} update job status:{}.", jobId, status);
    }

    public void saveCache(JobClient jobClient, String jobResource, int stage, boolean insert) {
        String nodeAddress = environmentContext.getLocalAddress();
        if (insert) {
            scheduleJobCacheService.insert(jobClient.getJobId(), jobClient.getComputeType().getType(), stage, jobClient.getParamAction().toString(), nodeAddress, jobClient.getJobName(), jobClient.getPriority(), jobResource, jobClient.getTenantId());
            jobClient.doStatusCallBack(TaskStatus.WAITENGINE.getStatus());
        } else {
            scheduleJobCacheService.updateStage(jobClient.getJobId(), stage, nodeAddress, jobClient.getPriority(), null);
        }
    }

    private void updateCacheBatch(List<String> jobIds, int stage) {
        String nodeAddress = environmentContext.getLocalAddress();
        scheduleJobCacheService.updateStageBatch(jobIds, stage, nodeAddress);
    }

    public void updateCache(JobClient jobClient, int stage) {
        String nodeAddress = environmentContext.getLocalAddress();
        scheduleJobCacheService.updateStage(jobClient.getJobId(), stage, nodeAddress, jobClient.getPriority(), null);
    }

    public String getAndUpdateEngineLog(String jobId, String engineJobId, String appId, Long tenantId) {

        if(StringUtils.isBlank(engineJobId)){
            return "";
        }
        String engineLog = null;
        try {
            ScheduleEngineJobCache engineJobCache = scheduleJobCacheService.getJobCacheByJobId(jobId);
            if (null == engineJobCache) {
                return "";
            }
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            Map<String, Object> pluginInfo = paramAction.getPluginInfo();
            JobIdentifier jobIdentifier = new JobIdentifier(engineJobId, appId, jobId,tenantId,paramAction.getTaskType(),
                    TaskParamsUtils.parseDeployTypeByTaskParams(paramAction.getTaskParams(),engineJobCache.getComputeType()).getType(),
                    null, MapUtils.isEmpty(pluginInfo) ? null : JSONObject.toJSONString(pluginInfo),paramAction.getComponentVersion());
            //从engine获取log
            engineLog = workerOperator.getEngineLog(jobIdentifier);
            if (engineLog != null) {
                scheduleJobService.updateExpandByJobId(jobId,engineLog,null);
            }
        } catch (Throwable e) {
            LOGGER.error("getAndUpdateEngineLog error jobId:{} error:.", jobId, e);
        }
        return engineLog;
    }

    /**
     * master 节点分发任务失败
     *
     * @param jobId
     */
    public void dealSubmitFailJob(String jobId, String errorMsg) {
        scheduleJobCacheService.deleteByJobId(jobId);
        scheduleJobService.jobFail(jobId, TaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(errorMsg));
        LOGGER.info("jobId:{} update job status:{}, job is finished.", jobId, TaskStatus.SUBMITFAILD.getStatus());
    }

    class RecoverDealer implements Runnable {
        @Override
        public void run() {
            LOGGER.info("-----The task resumes after restart----");
            String localAddress = environmentContext.getLocalAddress();
            try {
                long startId = 0L;
                while (true) {
                    List<ScheduleEngineJobCache> jobCaches = scheduleJobCacheService.listByStage(startId, localAddress, null, null);
                    if (CollectionUtils.isEmpty(jobCaches)) {
                        //两种情况：
                        //1. 可能本身没有jobcaches的数据
                        //2. master节点已经为此节点做了容灾
                        break;
                    }
                    List<JobClient> unSubmitClients = new ArrayList<>();
                    List<JobClient> submitClients = new ArrayList<>();
                    for (ScheduleEngineJobCache jobCache : jobCaches) {
                        try {
                            ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                            JobClient jobClient = new JobClient(paramAction);
                            if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
                                unSubmitClients.add(jobClient);
                            } else {
                                submitClients.add(jobClient);
                            }
                            startId = jobCache.getId();
                        } catch (Exception e) {
                            LOGGER.error("RecoverDealer run jobId {} error", jobCache.getJobId(), e);
                            //数据转换异常--打日志
                            dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + ExceptionUtil.getErrorMessage(e));
                        }
                    }
                    if (CollectionUtils.isNotEmpty(unSubmitClients)) {
                        addSubmitJobVast(unSubmitClients);
                    }
                    if (CollectionUtils.isNotEmpty(submitClients)) {
                        afterSubmitJobVast(submitClients);
                    }
                }
                LOGGER.info("cache deal end");

                // 恢复没有被容灾，但是状态丢失的任务
                long jobStartId = 0;
                // 扫描出 status = 0 和 19  phaseStatus = 1 
                List<SimpleScheduleJobPO> jobs = scheduleJobService.listJobByStatusAddressAndPhaseStatus(jobStartId, TaskStatus.getUnSubmitStatus(), localAddress, JobPhaseStatus.JOIN_THE_TEAM.getCode());
                while (CollectionUtils.isNotEmpty(jobs)) {
                    List<String> jobIds = jobs.stream().map(SimpleScheduleJobPO::getJobId).collect(Collectors.toList());
                    LOGGER.info("update job ids {}", jobIds);
                    scheduleJobService.updateJobStatusByJobIds(jobIds, TaskStatus.UNSUBMIT.getStatus(), JobPhaseStatus.CREATE.getCode());
                    jobStartId = jobs.get(jobs.size()-1).getId();
                    jobs = scheduleJobService.listJobByStatusAddressAndPhaseStatus(jobStartId, TaskStatus.getUnSubmitStatus(), localAddress, JobPhaseStatus.JOIN_THE_TEAM.getCode());
                }
                LOGGER.info("job deal end");
            } catch (Exception e) {
                LOGGER.error("----broker:{} RecoverDealer error:", localAddress, e);
            }

            LOGGER.info("-----After the restart, the task ends and resumes-----");
        }
    }

}
