package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.ClusterResource;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.cache.ShardCache;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    private static final Logger logger = LoggerFactory.getLogger(ConsoleService.class);

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private ZkService zkService;

    @Autowired
    private EngineJobStopRecordDao engineJobStopRecordDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private WorkerOperator workerOperator;

    private static long DELAULT_TENANT  = -1L;

    @Forbidden
    public Boolean finishJob(String jobId, Integer status) {
        if (!RdosTaskStatus.isStopped(status)) {
            logger.warn("Job status：" + status + " is not stopped status");
            return false;
        }
        shardCache.updateLocalMemTaskStatus(jobId, status);
        engineJobCacheDao.delete(jobId);
        scheduleJobDao.updateJobStatus(jobId, status);
        logger.info("jobId:{} update job status:{}, job is finished.", jobId, status);
        return true;
    }

    public List<String> nodeAddress() {
        try {
            return zkService.getAliveBrokersChildren();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public Map<String, Object> searchJob(@Param("jobName") String jobName) {
        Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
        String jobId = null;
        ScheduleJob scheduleJob = scheduleJobDao.getByName(jobName);
        if (scheduleJob != null) {
            jobId = scheduleJob.getJobId();
        }
        if (jobId == null) {
            return null;
        }
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJobCache == null) {
            return null;
        }
        try {
            Map<String, Object> theJobMap = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), Map.class);
            Tenant tenant = tenantDao.getByDtUicTenantId(scheduleJob.getDtuicTenantId());
            this.fillJobInfo(theJobMap, scheduleJob, engineJobCache,tenant);

            Map<String, Object> result = new HashMap<>(3);
            result.put("theJob", Lists.newArrayList(theJobMap));
            result.put("theJobIdx", 1);
            result.put("nodeAddress", engineJobCache.getNodeAddress());

            return result;
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return null;
    }

    public List<String> listNames(@Param("jobName") String jobName) {
        try {
            Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
            return engineJobCacheDao.listNames(jobName);
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return null;
    }

    public List<String> jobResources() {
        return engineJobCacheDao.getJobResources();
    }

    /**
     * 根据计算引擎类型显示任务
     */
    public Collection<Map<String, Object>> overview(@Param("nodeAddress") String nodeAddress, @Param("clusterName") String clusterName) {
        if (StringUtils.isBlank(nodeAddress)) {
            nodeAddress = null;
        }

        Map<String, Map<String, Object>> overview = new HashMap<>();
        List<Map<String, Object>> groupResult = engineJobCacheDao.groupByJobResource(nodeAddress);
        if (CollectionUtils.isNotEmpty(groupResult)) {
            List<Map<String, Object>> finalResult = new ArrayList<>(groupResult.size());
            for (Map<String, Object> record : groupResult) {
                String jobResource = MapUtils.getString(record, "jobResource");
                if (StringUtils.isBlank(clusterName) || !jobResource.contains(clusterName)) {
                    continue;
                }
                long generateTime = MapUtils.getLong(record, "generateTime");
                String waitTime = DateUtil.getTimeDifference(System.currentTimeMillis() - (generateTime * 1000));
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
                    Map<String, Object> overviewEle = new HashMap<>();
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
        return overview.values();
    }

    public Map<String, Object> groupDetail(@Param("jobResource") String jobResource,
                                           @Param("nodeAddress") String nodeAddress,
                                           @Param("stage") Integer stage,
                                           @Param("pageSize") Integer pageSize,
                                           @Param("currentPage") Integer currentPage,@Param("dtToken") String dtToken) {
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        Preconditions.checkNotNull(stage, "parameters of stage is required");
        Preconditions.checkArgument(currentPage != null && currentPage > 0, "parameters of currentPage is required");
        Preconditions.checkArgument(pageSize != null && pageSize > 0, "parameters of pageSize is required");

        if (StringUtils.isBlank(nodeAddress)) {
            nodeAddress = null;
        }
        List<Map<String, Object>> data = new ArrayList<>();
        Long count = 0L;
        int start = (currentPage - 1) * pageSize;
        try {
            count = engineJobCacheDao.countByJobResource(jobResource, stage, nodeAddress);
            if (count > 0) {
                List<EngineJobCache> engineJobCaches = engineJobCacheDao.listByJobResource(jobResource, stage, nodeAddress, start, pageSize);
                List<String> jobIds = engineJobCaches.stream().map(EngineJobCache::getJobId).collect(Collectors.toList());
                List<ScheduleJob> rdosJobByJobIds = scheduleJobDao.getRdosJobByJobIds(jobIds);
                Map<String, ScheduleJob> scheduleJobMap = rdosJobByJobIds.stream().collect(Collectors.toMap(ScheduleJob::getJobId, u -> u));
                Set<Long> dtuicTenantIds = rdosJobByJobIds.stream().map(ScheduleJob::getDtuicTenantId).collect(Collectors.toSet());
                Map<Long, Tenant> tenantMap = tenantDao.listAllTenantByDtUicTenantIds(new ArrayList<>(dtuicTenantIds)).stream()
                        .collect(Collectors.toMap(Tenant::getDtUicTenantId, t -> t));
                for (EngineJobCache engineJobCache : engineJobCaches) {
                    Map<String, Object> theJobMap = PublicUtil.objectToMap(engineJobCache);
                    ScheduleJob scheduleJob = scheduleJobMap.getOrDefault(engineJobCache.getJobId(), new ScheduleJob());
                    //补充租户信息
                    Tenant tenant = tenantMap.get(scheduleJob.getDtuicTenantId());
                    if(Objects.isNull(tenant) && DELAULT_TENANT != scheduleJob.getDtuicTenantId()){
                        //可能临时运行 租户在tenant表没有 需要添加
                        tenant = tenantService.addTenant(scheduleJob.getDtuicTenantId(), dtToken);
                    }
                    this.fillJobInfo(theJobMap, scheduleJob, engineJobCache,tenant);
                    data.add(theJobMap);
                }

            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", count);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);
        return result;
    }

    private void fillJobInfo(Map<String, Object> theJobMap, ScheduleJob scheduleJob, EngineJobCache engineJobCache,Tenant tenant) {
        theJobMap.put("status", scheduleJob.getStatus());
        theJobMap.put("execStartTime", scheduleJob.getExecStartTime());
        theJobMap.put("generateTime", engineJobCache.getGmtCreate());
        long currentTime = System.currentTimeMillis();
        String waitTime = DateUtil.getTimeDifference(currentTime - engineJobCache.getGmtCreate().getTime());
        theJobMap.put("waitTime", waitTime);
        theJobMap.put("tenantName", Objects.isNull(tenant) ? "" : tenant.getTenantName());
    }

    public Boolean jobStick(@Param("jobId") String jobId) {
        Preconditions.checkNotNull(jobId, "parameters of jobId is required");

        try {
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
            //只支持DB、PRIORITY两种调整顺序
            if (Objects.nonNull(engineJobCache) && EJobCacheStage.DB.getStage() == engineJobCache.getStage() || EJobCacheStage.PRIORITY.getStage() == engineJobCache.getStage()) {
                ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = new JobClient(paramAction);
                jobClient.setCallBack((jobStatus) -> {
                    jobDealer.updateJobStatus(jobClient.getTaskId(), jobStatus);
                });

                Long minPriority = engineJobCacheDao.minPriorityByStage(engineJobCache.getJobResource(), Lists.newArrayList(EJobCacheStage.PRIORITY.getStage()), engineJobCache.getNodeAddress());
                minPriority = minPriority == null ? 0 : minPriority;
                jobClient.setPriority(minPriority - 1);

                if (EJobCacheStage.PRIORITY.getStage() == engineJobCache.getStage()) {
                    //先将队列中的元素移除，重复插入会被忽略
                    GroupPriorityQueue groupPriorityQueue = jobDealer.getGroupPriorityQueue(engineJobCache.getJobResource());
                    groupPriorityQueue.remove(engineJobCache.getJobId());
                }
                return jobDealer.addGroupPriorityQueue(engineJobCache.getJobResource(), jobClient, false);
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return false;
    }

    public void stopJob(@Param("jobId") String jobId) throws Exception {
        Preconditions.checkArgument(StringUtils.isNotBlank(jobId), "parameters of jobId is required");
        List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(Lists.newArrayList(jobId));
        if (alreadyExistJobIds.contains(jobId)) {
            logger.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
            return;
        }

        EngineJobStopRecord stopRecord = new EngineJobStopRecord();
        stopRecord.setTaskId(jobId);

        engineJobStopRecordDao.insert(stopRecord);
    }

    /**
     * 概览，杀死全部
     */
    public void stopAll(@Param("jobResource") String jobResource,
                        @Param("nodeAddress") String nodeAddress) throws Exception {

        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");

        for (Integer eJobCacheStage : EJobCacheStage.unSubmitted()) {
            this.stopJobList(jobResource, nodeAddress, eJobCacheStage, null);
        }
    }

    public void stopJobList(@Param("jobResource") String jobResource,
                            @Param("nodeAddress") String nodeAddress,
                            @Param("stage") Integer stage,
                            @Param("jobIdList") List<String> jobIdList) throws Exception {
        if (jobIdList != null && !jobIdList.isEmpty()) {
            //杀死指定jobIdList的任务

            if (EJobCacheStage.unSubmitted().contains(stage)) {
                Integer deleted = engineJobCacheDao.deleteByJobIds(jobIdList);
                Integer updated = scheduleJobDao.updateJobStatusByJobIds(jobIdList, RdosTaskStatus.CANCELED.getStatus());
                logger.info("delete job size:{}, update job size:{}, deal jobIds:{}", deleted, updated, jobIdList);
            } else {
                List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(jobIdList);
                for (String jobId : jobIdList) {
                    if (alreadyExistJobIds.contains(jobId)) {
                        logger.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
                        continue;
                    }

                    EngineJobStopRecord stopRecord = new EngineJobStopRecord();
                    stopRecord.setTaskId(jobId);
                    engineJobStopRecordDao.insert(stopRecord);
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
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, nodeAddress, stage, jobResource);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    //两种情况：
                    //1. 可能本身没有jobcaches的数据
                    //2. master节点已经为此节点做了容灾
                    break;
                }
                List<String> jobIds = new ArrayList<>(jobCaches.size());
                for (EngineJobCache jobCache : jobCaches) {
                    startId = jobCache.getId();
                    jobIds.add(jobCache.getJobId());
                }

                if (EJobCacheStage.unSubmitted().contains(stage)) {
                    Integer deleted = engineJobCacheDao.deleteByJobIds(jobIds);
                    Integer updated = scheduleJobDao.updateJobStatusByJobIds(jobIds, RdosTaskStatus.CANCELED.getStatus());
                    logger.info("delete job size:{}, update job size:{}, query job size:{}, jobIds:{}", deleted, updated, jobCaches.size(), jobIds);
                } else {
                    //已提交的任务需要发送请求杀死，走正常杀任务的逻辑
                    List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(jobIds);
                    for (EngineJobCache jobCache : jobCaches) {
                        startId = jobCache.getId();
                        if (alreadyExistJobIds.contains(jobCache.getJobId())) {
                            logger.info("jobId:{} ignore insert stop record, because is already exist in table.", jobCache.getJobId());
                            continue;
                        }

                        EngineJobStopRecord stopRecord = new EngineJobStopRecord();
                        stopRecord.setTaskId(jobCache.getJobId());
                        engineJobStopRecordDao.insert(stopRecord);
                    }
                }
            }
        }
    }

    public Map<String, Object> clusterResources(@Param("clusterName") String clusterName) {
        if (StringUtils.isEmpty(clusterName)) {
            return MapUtils.EMPTY_MAP;
        }

        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        Component yarnComponent = getYarnComponent(cluster.getId());
        if (yarnComponent == null) {
            return null;
        }

        return getResources(yarnComponent, cluster);
    }

    @Forbidden
    public Map<String, Object> getResources(Component yarnComponent, Cluster cluster) {
        Map<String, Object> clusterResources = new HashMap<>(2);
        try {
            JSONObject pluginInfo = new JSONObject();
            JSONObject componentConfig = JSONObject.parseObject(yarnComponent.getComponentConfig());
            pluginInfo.put(EComponentType.YARN.getConfName(), componentConfig);
            String typeName = componentConfig.getString(ComponentService.TYPE_NAME);
            if (StringUtils.isBlank(typeName)) {
                //获取对应的插件名称
                Component hdfsComponent = componentService.getComponentByClusterId(cluster.getId(), EComponentType.HDFS.getTypeCode());
                String clusterName = cluster.getClusterName();
                if (Objects.isNull(hdfsComponent)) {
                    typeName = componentService.convertComponentTypeToClient(clusterName,
                            EComponentType.HDFS.getTypeCode(), yarnComponent.getHadoopVersion());
                } else {
                    typeName = componentService.convertComponentTypeToClient(clusterName,
                            EComponentType.HDFS.getTypeCode(), hdfsComponent.getHadoopVersion());
                }
            }
            pluginInfo.put(ComponentService.TYPE_NAME,typeName);
            ClusterResource clusterResource = workerOperator.clusterResource(typeName, pluginInfo.toJSONString());
            clusterResources.put("yarn", clusterResource.getYarn());
            clusterResources.put("flink", clusterResource.getFlink());
        } catch (Exception e) {
            logger.error(" ", e);
            throw new RdosDefineException("flink资源获取异常");
        }
        return clusterResources;
    }

    private Component getYarnComponent(Long clusterId) {
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if (CollectionUtils.isEmpty(engines)) {
            return null;
        }

        Engine hadoopEngine = null;
        for (Engine e : engines) {
            if (e.getEngineType() == MultiEngineType.HADOOP.getType()) {
                hadoopEngine = e;
                break;
            }
        }

        if (hadoopEngine == null) {
            return null;
        }

        List<Component> componentList = componentService.listComponent(hadoopEngine.getId());
        if (CollectionUtils.isEmpty(componentList)) {
            return null;
        }

        for (Component component : componentList) {
            if (EComponentType.YARN.getTypeCode() == component.getComponentTypeCode()) {
                return component;
            }
        }

        return null;
    }
}
