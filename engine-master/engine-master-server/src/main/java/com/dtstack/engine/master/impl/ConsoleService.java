package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.vo.console.ConsoleJobInfoVO;
import com.dtstack.engine.api.vo.console.ConsoleJobVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.config.TaskResourceBeanConfig;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.plugininfo.PluginWrapper;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.vo.TaskTypeResourceTemplateVO;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleService.class);

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ClusterDao clusterDao;

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

    @Autowired
    private PluginWrapper pluginWrapper;

    @Autowired
    private JobComputeResourcePlain jobComputeResourcePlain;

    @Autowired
    private KerberosDao kerberosDao;

    private static long DELAULT_TENANT  = -1L;

    public Boolean finishJob(String jobId, Integer status) {
        if (!RdosTaskStatus.isStopped(status)) {
            LOGGER.warn("Job status：" + status + " is not stopped status");
            return false;
        }
        shardCache.updateLocalMemTaskStatus(jobId, status);
        engineJobCacheDao.delete(jobId);
        scheduleJobDao.updateJobStatus(jobId, status);
        LOGGER.info("jobId:{} update job status:{}, job is finished.", jobId, status);
        return true;
    }

    public List<String> nodeAddress() {
        try {
            return zkService.getAliveBrokersChildren();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public ConsoleJobVO searchJob(String jobName) {
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
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            Tenant tenant = tenantDao.getByDtUicTenantId(scheduleJob.getDtuicTenantId());
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
            Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
            return engineJobCacheDao.listNames(jobName);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public List<String> jobResources() {
        return engineJobCacheDao.getJobResources();
    }

    /**
     * 根据计算引擎类型显示任务
     */
    public Collection<Map<String, Object>> overview( String nodeAddress,  String clusterName) {
        if (StringUtils.isBlank(nodeAddress)) {
            nodeAddress = null;
        }

        Map<String, Map<String, Object>> overview = new HashMap<>(16);
        List<Map<String, Object>> groupResult = engineJobCacheDao.groupByJobResource(nodeAddress);
        if (CollectionUtils.isNotEmpty(groupResult)) {
            List<Map<String, Object>> finalResult = new ArrayList<>(groupResult.size());
            for (Map<String, Object> record : groupResult) {
                String jobResource = MapUtils.getString(record, "jobResource");
                if(!isBelongCluster(clusterName,jobResource)){
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

    private boolean isBelongCluster(String clusterName,String jobResource){
        return clusterName.equalsIgnoreCase(jobComputeResourcePlain.parseClusterFromJobResource(jobResource));
    }


    public PageResult groupDetail(String jobResource,
                                  String nodeAddress,
                                  Integer stage,
                                  Integer pageSize,
                                  Integer currentPage, String dtToken) {
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

                Map<String,String> pluginInfoCache = new HashMap<>();
                for (EngineJobCache engineJobCache : engineJobCaches) {
                    Map<String, Object> theJobMap = PublicUtil.objectToMap(engineJobCache);
                    ScheduleJob scheduleJob = scheduleJobMap.getOrDefault(engineJobCache.getJobId(), new ScheduleJob());
                    //补充租户信息
                    Tenant tenant = tenantMap.get(scheduleJob.getDtuicTenantId());
                    if(null == tenant && ConfigConstant.DEFAULT_TENANT != scheduleJob.getDtuicTenantId() && scheduleJob.getDtuicTenantId() > 0){
                        //可能临时运行 租户在tenant表没有 需要添加
                        try {
                            tenant = tenantService.addTenant(scheduleJob.getDtuicTenantId(), dtToken);
                        } catch (Exception e) {
                            LOGGER.error(" get tenant error {}", scheduleJob.getDtuicTenantId(),e);
                        }
                    }
                    this.fillJobInfo(theJobMap, scheduleJob, engineJobCache,tenant,pluginInfoCache);
                    data.add(theJobMap);
                }
            }
        } catch (Exception e) {
            LOGGER.error("groupDetail error", e);
        }
        PageQuery pageQuery = new PageQuery<>(currentPage, pageSize);
        return new PageResult<>(data,count.intValue(),pageQuery);
    }

    private void fillJobInfo(Map<String, Object> theJobMap, ScheduleJob scheduleJob, EngineJobCache engineJobCache, Tenant tenant,Map<String,String> pluginInfoCache) {
        theJobMap.put("status", scheduleJob.getStatus());
        theJobMap.put("execStartTime", scheduleJob.getExecStartTime());
        theJobMap.put("generateTime", engineJobCache.getGmtCreate());
        long currentTime = System.currentTimeMillis();
        String waitTime = DateUtil.getTimeDifference(currentTime - engineJobCache.getGmtCreate().getTime());
        theJobMap.put("waitTime", waitTime);
        theJobMap.put("waitReason", engineJobCache.getWaitReason());
        theJobMap.put("tenantName", null == tenant ? "" : tenant.getTenantName());
        String jobInfo = (String) theJobMap.get("jobInfo");
        JSONObject jobInfoJSON = JSONObject.parseObject(jobInfo);
        if (null == jobInfoJSON) {
            jobInfoJSON = new JSONObject();
        }
        if (!jobInfoJSON.containsKey(PluginWrapper.PLUGIN_INFO)) {
            //获取插件信息
            String pluginInfo = pluginWrapper.getPluginInfo(jobInfoJSON.getString("taskParams"), engineJobCache.getComputeType(), engineJobCache.getEngineType(),
                    null == tenant ? -1L : tenant.getDtUicTenantId(), jobInfoJSON.getLong("userId"),pluginInfoCache,jobInfoJSON.getString("componentVersion"));
            jobInfoJSON.put(PluginWrapper.PLUGIN_INFO, JSONObject.parseObject(pluginInfo));
            theJobMap.put("jobInfo", jobInfoJSON);
        }
    }

    private ConsoleJobInfoVO fillJobInfo(ParamAction paramAction, ScheduleJob scheduleJob, EngineJobCache engineJobCache, Tenant tenant) {
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
        Preconditions.checkNotNull(jobId, "parameters of jobId is required");

        try {
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
            if(null == engineJobCache){
                return false;
            }
            //只支持DB、PRIORITY两种调整顺序
            if (EJobCacheStage.DB.getStage() == engineJobCache.getStage()
                    || EJobCacheStage.PRIORITY.getStage() == engineJobCache.getStage()) {
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
        List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(Lists.newArrayList(jobId));
        if (alreadyExistJobIds.contains(jobId)) {
            LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
            return;
        }

        EngineJobStopRecord stopRecord = new EngineJobStopRecord();
        stopRecord.setTaskId(jobId);
        stopRecord.setForceCancelFlag(isForce);

        engineJobStopRecordDao.insert(stopRecord);

    }

    public void stopJob( String jobId) throws Exception {
        stopJob(jobId , ForceCancelFlag.NO.getFlag());
    }

    /**
     * 概览，杀死全部
     */
    public void stopAll( String jobResource,
                         String nodeAddress) throws Exception {

        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");

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
                Integer deleted = engineJobCacheDao.deleteByJobIds(jobIdList);
                Integer updated = scheduleJobDao.updateJobStatusByJobIds(jobIdList, RdosTaskStatus.CANCELED.getStatus());
                LOGGER.info("delete job size:{}, update job size:{}, deal jobIds:{}", deleted, updated, jobIdList);
            } else {
                List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(jobIdList);
                for (String jobId : jobIdList) {
                    if (alreadyExistJobIds.contains(jobId)) {
                        LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
                        continue;
                    }

                    EngineJobStopRecord stopRecord = new EngineJobStopRecord();
                    stopRecord.setTaskId(jobId);
                    stopRecord.setForceCancelFlag(isForce);
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
                    LOGGER.info("delete job size:{}, update job size:{}, query job size:{}, jobIds:{}", deleted, updated, jobCaches.size(), jobIds);
                } else {
                    //已提交的任务需要发送请求杀死，走正常杀任务的逻辑
                    List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(jobIds);
                    for (EngineJobCache jobCache : jobCaches) {
                        startId = jobCache.getId();
                        if (alreadyExistJobIds.contains(jobCache.getJobId())) {
                            LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobCache.getJobId());
                            continue;
                        }

                        EngineJobStopRecord stopRecord = new EngineJobStopRecord();
                        stopRecord.setTaskId(jobCache.getJobId());
                        stopRecord.setForceCancelFlag(isForce);
                        engineJobStopRecordDao.insert(stopRecord);
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

    public ClusterResource clusterResources( String clusterName,Map<Integer,String> componentVersionMap) {
        if (StringUtils.isEmpty(clusterName)) {
            return new ClusterResource();
        }

        Cluster cluster = clusterDao.getByClusterName(clusterName);
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

    public ClusterResource getResources(Component yarnComponent, Cluster cluster,JSONObject componentConfig) {
        try {
            JSONObject pluginInfo = new JSONObject();
            pluginInfo.put(EComponentType.YARN.getConfName(), componentConfig);
            if (StringUtils.isNotBlank(yarnComponent.getKerberosFileName())) {
                //开启kerberos 添加信息
                KerberosConfig kerberosConfig = kerberosDao.getByComponentType(cluster.getId(), yarnComponent.getComponentTypeCode(),null);
                Map sftpMap = componentService.getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class,null);
                pluginInfo.put(EComponentType.SFTP.getConfName(), sftpMap);
                pluginInfo = JSONObject.parseObject(componentService.wrapperConfig(yarnComponent.getComponentTypeCode(),componentConfig.toJSONString(),sftpMap,kerberosConfig,cluster.getClusterName()));
            }
            String typeName = componentConfig.getString(ConfigConstant.TYPE_NAME_KEY);
            if (StringUtils.isBlank(typeName)) {
                //获取对应的插件名称
                typeName = componentService.convertComponentTypeToClient(cluster.getClusterName(),
                        EComponentType.YARN.getTypeCode(), yarnComponent.getHadoopVersion(),null,null,null);
            }
            pluginInfo.put(ConfigConstant.TYPE_NAME_KEY,typeName);
            return workerOperator.clusterResource(typeName, pluginInfo.toJSONString());
        } catch (Exception e) {
            LOGGER.error("getResources error: ", e);
            throw new RdosDefineException("acquire flink resources error.");
        }
    }

    /**
    * @author zyd
    * @Description 获取任务类型及对应的资源模板
    * @Date 8:13 下午 2020/10/14
    * @Param []
    * @retrun java.util.List<com.dtstack.engine.master.vo.TaskTypeResourceTemplate>
    **/
    public List<TaskTypeResourceTemplateVO> getTaskResourceTemplate() {

        return TaskResourceBeanConfig.templateList;
    }
}
