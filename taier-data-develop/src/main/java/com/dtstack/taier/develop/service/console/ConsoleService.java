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

package com.dtstack.taier.develop.service.console;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EJobCacheStage;
import com.dtstack.taier.common.enums.ForceCancelFlag;
import com.dtstack.taier.common.enums.OperatorType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.ComponentVersionUtil;
import com.dtstack.taier.common.util.DatasourceTypeUtil;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.KerberosConfig;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taier.dao.domain.Tenant;
import com.dtstack.taier.dao.mapper.ClusterMapper;
import com.dtstack.taier.dao.mapper.ConsoleKerberosMapper;
import com.dtstack.taier.dao.mapper.ScheduleEngineJobCacheMapper;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.dao.mapper.ScheduleJobOperatorRecordMapper;
import com.dtstack.taier.dao.mapper.TenantMapper;
import com.dtstack.taier.dao.pager.PageQuery;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IYarn;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.yarn.YarnResourceDTO;
import com.dtstack.taier.develop.mapstruct.vo.DatasourceMapstructTransfer;
import com.dtstack.taier.develop.vo.console.ConsoleJobInfoVO;
import com.dtstack.taier.develop.vo.console.ConsoleJobVO;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.ClusterResource;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.datasource.convert.engine.PluginInfoToSourceDTO;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.jobdealer.JobDealer;
import com.dtstack.taier.scheduler.server.queue.GroupPriorityQueue;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.zookeeper.ZkService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 对接数栈控制台
 * <p>
 * 代码engine中内存队列的类型名字
 * <p>
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/18
 */
@Service
public class ConsoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleService.class);

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private ScheduleEngineJobCacheMapper scheduleEngineJobCacheMapper;

    @Autowired
    private ClusterMapper clusterMapper;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private ZkService zkService;

    @Autowired
    private ScheduleJobOperatorRecordMapper engineJobStopRecordMapper;

    @Autowired
    private TenantMapper tenantMapper;


    @Autowired
    private ClusterTenantService clusterTenantService;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private ConsoleKerberosMapper consoleKerberosMapper;

    @Autowired
    private DatasourceMapstructTransfer datasourceMapstructTransfer;


    public List<String> nodeAddress() {
        return zkService.getAliveBrokersChildren();
    }

    public ConsoleJobVO searchJob(String jobName) {
        String jobId = null;
        ScheduleJob scheduleJob = scheduleJobMapper.getByName(jobName);
        if (scheduleJob != null) {
            jobId = scheduleJob.getJobId();
        }
        if (jobId == null) {
            return null;
        }
        ScheduleEngineJobCache engineJobCache = scheduleEngineJobCacheMapper.getOne(jobId);
        if (engineJobCache == null) {
            return null;
        }
        try {
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            Tenant tenant = tenantMapper.selectById(scheduleJob.getTenantId());
            ConsoleJobInfoVO consoleJobInfoVO = this.fillJobInfo(paramAction, scheduleJob, engineJobCache, tenant);
            ConsoleJobVO vo = new ConsoleJobVO();
            vo.setTheJob(consoleJobInfoVO);
            vo.setNodeAddress(engineJobCache.getNodeAddress());
            vo.setTheJobIdx(1);
            return vo;
        } catch (Exception e) {
            LOGGER.error("searchJob error:", e);
        }
        return null;
    }

    public List<String> listNames( String jobName) {
        try {
            return scheduleEngineJobCacheMapper.listNames(jobName);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public List<String> jobResources() {
        return scheduleEngineJobCacheMapper.getJobResources();
    }

    /**
     * 根据计算引擎类型显示任务
     */
    public Collection<Map<String, Object>> overview(String nodeAddress, String clusterName) {
        List<Map<String, Object>> groupResult = scheduleEngineJobCacheMapper.groupByJobResourceFilterByCluster(nodeAddress, clusterName);
        if (CollectionUtils.isEmpty(groupResult)) {
            Map<String, Map<String, Object>> overview = new HashMap<>(16);
            return overview.values();
        }
        return this.dealGroupResult(groupResult);
    }

    private Collection<Map<String, Object>> dealGroupResult(List<Map<String, Object>> groupResult) {
        // {jobResource, Map<String, Object> overviewRecord}
        Map<String, Map<String, Object>> overview = new HashMap<>(16);
        List<Map<String, Object>> finalResult = new ArrayList<>(groupResult.size());
        // 处理 DB 返回结果
        for (Map<String, Object> record : groupResult) {
            long generateTime = MapUtils.getLong(record, "generateTime");
            String waitTime = com.dtstack.taier.common.util.DateUtil.getTimeDifference(System.currentTimeMillis() - (generateTime * 1000));
            record.put("waitTime", waitTime);
            finalResult.add(record);
        }

        for (Map<String, Object> record : finalResult) {
            String jobResource = MapUtils.getString(record, "jobResource");
            int stage = MapUtils.getInteger(record, "stage");
            String waitTime = MapUtils.getString(record, "waitTime");
            long jobSize = MapUtils.getLong(record, "jobSize");
            EJobCacheStage eJobCacheStage = EJobCacheStage.getStage(stage);

            Map<String, Object> overviewRecord = overview.computeIfAbsent(jobResource, k -> {
                Map<String, Object> overviewEle = new HashMap<>(16);
                overviewEle.put("jobResource", jobResource);
                return overviewEle;
            });
            String stageName = eJobCacheStage.name().toLowerCase();
            overviewRecord.put(stageName, stage);
            overviewRecord.put(stageName + "JobSize", jobSize);
            overviewRecord.put(stageName + "WaitTime", waitTime);
        }

        Collection<Map<String, Object>> overviewValues = overview.values();
        for (Map<String, Object> record : overviewValues) {
            // 针对单个 jobResource，归集所有 stage
            for (EJobCacheStage checkStage : EJobCacheStage.values()) {
                String checkStageName = checkStage.name().toLowerCase();
                if (record.containsKey(checkStageName)) {
                    continue;
                }

                record.put(checkStageName, checkStage.getStage());
                record.put(checkStageName + "JobSize", 0);
                record.put(checkStageName + "WaitTime", "");
            }
        }
        return overviewValues;
    }

    public PageResult groupDetail(String jobResource,
                                  String nodeAddress,
                                  Integer stage,
                                  Integer pageSize,
                                  Integer currentPage) {
        if (StringUtils.isBlank(nodeAddress)) {
            nodeAddress = null;
        }
        List<Map<String, Object>> data = new ArrayList<>();
        Long count = 0L;
        int start = (currentPage - 1) * pageSize;
        try {
            count = scheduleEngineJobCacheMapper.countByJobResource(jobResource, stage, nodeAddress);

            if (count > 0) {
                List<ScheduleEngineJobCache> engineJobCaches = scheduleEngineJobCacheMapper.listByJobResource(jobResource, stage, nodeAddress, start, pageSize);
                List<String> jobIds = engineJobCaches.stream().map(ScheduleEngineJobCache::getJobId).collect(Collectors.toList());
                List<ScheduleJob> rdosJobByJobIds = scheduleJobMapper.getRdosJobByJobIds(jobIds);
                Map<String, ScheduleJob> scheduleJobMap = rdosJobByJobIds.stream().collect(Collectors.toMap(ScheduleJob::getJobId, u -> u));
                Set<Long> tenantIds = rdosJobByJobIds.stream().map(ScheduleJob::getTenantId).collect(Collectors.toSet());
                Map<Long, Tenant> tenantMap = tenantMapper.selectBatchIds(tenantIds).stream()
                        .collect(Collectors.toMap(Tenant::getId, t -> t));

                for (ScheduleEngineJobCache engineJobCache : engineJobCaches) {
                    Map<String, Object> theJobMap = PublicUtil.objectToMap(engineJobCache);
                    ScheduleJob scheduleJob = scheduleJobMap.getOrDefault(engineJobCache.getJobId(), new ScheduleJob());
                    //补充租户信息
                    Tenant tenant = tenantMap.get(scheduleJob.getTenantId());
                    this.fillJobInfo(theJobMap, scheduleJob, engineJobCache,tenant);
                    data.add(theJobMap);
                }
            }
        } catch (Exception e) {
            LOGGER.error("groupDetail error", e);
        }
        PageQuery pageQuery = new PageQuery<>(currentPage, pageSize);
        return new PageResult<>(data,count.intValue(),pageQuery);
    }

    private void fillJobInfo(Map<String, Object> theJobMap, ScheduleJob scheduleJob, ScheduleEngineJobCache engineJobCache, Tenant tenant) {
        theJobMap.put("status", scheduleJob.getStatus());
        theJobMap.put("execStartTime", scheduleJob.getExecStartTime());
        theJobMap.put("generateTime", engineJobCache.getGmtCreate());
        long currentTime = System.currentTimeMillis();
        String waitTime = DateUtil.getTimeDifference(currentTime - engineJobCache.getGmtCreate().getTime());
        theJobMap.put("waitTime", waitTime);
        theJobMap.put("waitReason", engineJobCache.getWaitReason());
        theJobMap.put("tenantName", null == tenant ? "" : tenant.getTenantName());
    }

    private ConsoleJobInfoVO fillJobInfo(ParamAction paramAction, ScheduleJob scheduleJob, ScheduleEngineJobCache engineJobCache, Tenant tenant) {
        ConsoleJobInfoVO infoVO = new ConsoleJobInfoVO();
        infoVO.setStatus(scheduleJob.getStatus());
        infoVO.setExecStartTime(scheduleJob.getExecStartTime());
        infoVO.setGenerateTime(engineJobCache.getGmtCreate());
        long currentTime = System.currentTimeMillis();
        String waitTime = DateUtil.getTimeDifference(currentTime - engineJobCache.getGmtCreate().getTime());
        infoVO.setWaitTime(waitTime);
        infoVO.setTenantName(null == tenant ? "" : tenant.getTenantName());
        infoVO.setParamAction(paramAction);
        return infoVO;
    }

    public Boolean jobStick( String jobId) {
        try {
            ScheduleEngineJobCache engineJobCache = scheduleEngineJobCacheMapper.getOne(jobId);
            if(null == engineJobCache){
                return false;
            }
            //只支持DB、PRIORITY两种调整顺序
            if (EJobCacheStage.DB.getStage() == engineJobCache.getStage()
                    || EJobCacheStage.PRIORITY.getStage() == engineJobCache.getStage()) {
                ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = new JobClient(paramAction);
                jobClient.setCallBack((jobStatus) -> {
                    jobDealer.updateJobStatus(jobClient.getJobId(), jobStatus);
                });

                Long minPriority = scheduleEngineJobCacheMapper.minPriorityByStage(engineJobCache.getJobResource(), Lists.newArrayList(EJobCacheStage.PRIORITY.getStage()), engineJobCache.getNodeAddress());
                minPriority = minPriority == null ? 0 : minPriority;
                jobClient.setPriority(minPriority - 1);

                if (EJobCacheStage.PRIORITY.getStage() == engineJobCache.getStage()) {
                    //先将队列中的元素移除，重复插入会被忽略
                    GroupPriorityQueue groupPriorityQueue = jobDealer.getGroupPriorityQueue(engineJobCache.getJobResource());
                    groupPriorityQueue.remove(jobClient);
                }
                return jobDealer.addGroupPriorityQueue(engineJobCache.getJobResource(), jobClient, false, false);
            }
        } catch (Exception e) {
            LOGGER.error("jobStick error:", e);
        }
        return false;
    }

    public void stopJob(String jobId, Integer isForce){
        Preconditions.checkArgument(StringUtils.isNotBlank(jobId), "parameters of jobId is required");
        List<String> alreadyExistJobIds = engineJobStopRecordMapper.listByJobIds(Lists.newArrayList(jobId));
        if (alreadyExistJobIds.contains(jobId)) {
            LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
            return;
        }

        ScheduleJobOperatorRecord stopRecord = new ScheduleJobOperatorRecord();
        stopRecord.setJobId(jobId);
        stopRecord.setForceCancelFlag(isForce);
        stopRecord.setOperatorType(OperatorType.STOP.getType());
        engineJobStopRecordMapper.insert(stopRecord);

    }

    public void stopJob( String jobId) throws Exception {
        stopJob(jobId , ForceCancelFlag.NO.getFlag());
    }

    /**
     * 概览，杀死全部
     */
    public void stopAll( String jobResource,
                         String nodeAddress) throws Exception {


        for (Integer eJobCacheStage : EJobCacheStage.unSubmitted()) {
            this.stopJobList(jobResource, nodeAddress, eJobCacheStage, null);
        }
    }

    public void stopJobList(String jobResource,
                            String nodeAddress,
                            Integer stage,
                            List<String> jobIdList,
                            Integer isForce){
        if (CollectionUtils.isNotEmpty(jobIdList)) {
            //杀死指定jobIdList的任务
            if (EJobCacheStage.unSubmitted().contains(stage)) {
                Integer deleted = scheduleEngineJobCacheMapper.deleteByJobIds(jobIdList);
                Integer updated = scheduleJobMapper.updateJobStatusByJobIds(jobIdList, TaskStatus.CANCELED.getStatus());
                LOGGER.info("delete job size:{}, update job size:{}, deal jobIds:{}", deleted, updated, jobIdList);
            } else {
                List<String> alreadyExistJobIds = engineJobStopRecordMapper.listByJobIds(jobIdList);
                for (String jobId : jobIdList) {
                    if (alreadyExistJobIds.contains(jobId)) {
                        LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
                        continue;
                    }

                    ScheduleJobOperatorRecord stopRecord = new ScheduleJobOperatorRecord();
                    stopRecord.setJobId(jobId);
                    stopRecord.setForceCancelFlag(isForce);
                    stopRecord.setOperatorType(OperatorType.STOP.getType());
                    engineJobStopRecordMapper.insert(stopRecord);
                }
            }
        } else {
            //根据条件杀死所有任务
            Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
            Preconditions.checkNotNull(stage, "parameters of stage is required");

            if (StringUtils.isBlank(nodeAddress)) {
                nodeAddress = null;
            }

            long startId = 0L;
            while (true) {
                List<ScheduleEngineJobCache> jobCaches = scheduleEngineJobCacheMapper.listByStage(startId, nodeAddress, stage, jobResource,Boolean.FALSE);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    //两种情况：
                    //1. 可能本身没有jobcaches的数据
                    //2. master节点已经为此节点做了容灾
                    break;
                }
                List<String> jobIds = new ArrayList<>(jobCaches.size());
                for (ScheduleEngineJobCache jobCache : jobCaches) {
                    startId = jobCache.getId();
                    jobIds.add(jobCache.getJobId());
                }

                if (EJobCacheStage.unSubmitted().contains(stage)) {
                    Integer deleted = scheduleEngineJobCacheMapper.deleteByJobIds(jobIds);
                    Integer updated = scheduleJobMapper.updateJobStatusByJobIds(jobIds, TaskStatus.CANCELED.getStatus());
                    LOGGER.info("delete job size:{}, update job size:{}, query job size:{}, jobIds:{}", deleted, updated, jobCaches.size(), jobIds);
                } else {
                    //已提交的任务需要发送请求杀死，走正常杀任务的逻辑
                    List<String> alreadyExistJobIds = engineJobStopRecordMapper.listByJobIds(jobIds);
                    for (ScheduleEngineJobCache jobCache : jobCaches) {
                        startId = jobCache.getId();
                        if (alreadyExistJobIds.contains(jobCache.getJobId())) {
                            LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobCache.getJobId());
                            continue;
                        }

                        ScheduleJobOperatorRecord stopRecord = new ScheduleJobOperatorRecord();
                        stopRecord.setJobId(jobCache.getJobId());
                        stopRecord.setForceCancelFlag(isForce);
                        stopRecord.setOperatorType(OperatorType.STOP.getType());
                        engineJobStopRecordMapper.insert(stopRecord);
                    }
                }
            }
        }
    }

    public void stopJobList( String jobResource,
                             String nodeAddress,
                             Integer stage,
                             List<String> jobIdList) throws Exception {
        stopJobList(jobResource, nodeAddress, stage, jobIdList, ForceCancelFlag.NO.getFlag());
    }

    public ClusterResource clusterResources(Long clusterId) {
        Cluster cluster = clusterMapper.getOne(clusterId);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        Component yarnComponent = componentService.getComponentByClusterId(cluster.getId(),EComponentType.YARN.getTypeCode(),null);
        if (yarnComponent == null) {
            return null;
        }
        JSONObject yarnConfigStr = componentService.getComponentByClusterId(cluster.getId(), EComponentType.YARN.getTypeCode(), false, JSONObject.class,null);
        return getResources(yarnComponent, cluster,yarnConfigStr);
    }


    public ClusterResource clusterResourcesByTenantId(Long tenantId) {
        Long clusterId = clusterTenantService.getClusterIdByTenantId(tenantId);
        return clusterResources(clusterId);
    }

    public ClusterResource getResources(Component yarnComponent, Cluster cluster,JSONObject componentConfig) {
        try {
            JSONObject pluginInfo = new JSONObject();
            pluginInfo.put(EComponentType.YARN.getConfName(), componentConfig);
            if (StringUtils.isNotBlank(yarnComponent.getKerberosFileName())) {
                //开启kerberos 添加信息
                KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(cluster.getId(), yarnComponent.getComponentTypeCode(), ComponentVersionUtil.formatMultiVersion(yarnComponent.getComponentTypeCode(),yarnComponent.getVersionValue()));
                Map sftpMap = componentService.getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class,null);
                pluginInfo = componentService.wrapperConfig(yarnComponent.getComponentTypeCode(),componentConfig.toJSONString(),sftpMap,kerberosConfig,cluster.getId());
            }
            Integer datasourceType = DatasourceTypeUtil.getTypeByComponentAndVersion(yarnComponent.getComponentTypeCode(), yarnComponent.getVersionName());
            pluginInfo.put(DatasourceOperator.DATA_SOURCE_TYPE, datasourceType);


            IYarn yarn = ClientCache.getYarn(datasourceType);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString());
            YarnResourceDTO yarnResource = yarn.getYarnResource(sourceDTO);
            return datasourceMapstructTransfer.yarnResourceDTOtoClusterResource(yarnResource);
        } catch (Exception e) {
            LOGGER.error("getResources error: ", e);
            throw new RdosDefineException("acquire flink resources error.");
        }
    }

}
