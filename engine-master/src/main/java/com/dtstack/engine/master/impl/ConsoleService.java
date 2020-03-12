package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.DateUtil;
import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobStopRecordDao;
import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.Engine;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.domain.EngineJobStopRecord;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.cache.ShardCache;
import com.dtstack.engine.master.component.ComponentFactory;
import com.dtstack.engine.master.component.FlinkComponent;
import com.dtstack.engine.master.component.YARNComponent;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private EngineJobDao engineJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private WorkNode workNode;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private EngineJobStopRecordDao engineJobStopRecordDao;


    public Boolean finishJob(String jobId, Integer status) {
        if (!RdosTaskStatus.isStopped(status)) {
            logger.warn("Job status：" + status + " is not stopped status");
            return false;
        }
        shardCache.updateLocalMemTaskStatus(jobId, status);
        engineJobCacheDao.delete(jobId);
        engineJobDao.updateJobStatus(jobId, status);
        logger.info("jobId:{} update job status:{}, job is finished.", jobId, status);
        return true;
    }

    public List<String> nodeAddress() {
        try {
            return engineJobCacheDao.getAllNodeAddress();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public Map<String, Object> searchJob(@Param("jobName") String jobName) {
        Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
        String jobId = null;
        EngineJob engineJob = engineJobDao.getByName(jobName);
        if (engineJob != null) {
            jobId = engineJob.getJobId();
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
            this.fillJobInfo(theJobMap, engineJob, engineJobCache);

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
    public Collection<Map<String, Object>> overview(@Param("nodeAddress") String nodeAddress) {
        if (StringUtils.isBlank(nodeAddress)) {
            nodeAddress = null;
        }

        Map<String, Map<String, Object>> overview = new HashMap<>();
        List<Map<String, Object>> groupResult = engineJobCacheDao.groupByJobResource(nodeAddress);
        if (CollectionUtils.isNotEmpty(groupResult)) {
            for (Map<String, Object> record : groupResult) {
                long generateTime = MapUtils.getLong(record, "generateTime");
                String waitTime = DateUtil.getTimeDifference(System.currentTimeMillis() - (generateTime * 1000));
                record.put("waitTime", waitTime);
            }

            for (Map<String, Object> record : groupResult) {
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
                overviewRecord.put(eJobCacheStage.name().toLowerCase(), stage);
                overviewRecord.put(eJobCacheStage.name().toLowerCase() + "JobSize", jobSize);
                overviewRecord.put(eJobCacheStage.name().toLowerCase() + "WaitTime", waitTime);
            }
        }
        return overview.values();
    }

    public Map<String, Object> groupDetail(@Param("jobResource") String jobResource,
                                           @Param("nodeAddress") String nodeAddress,
                                           @Param("stage") Integer stage,
                                           @Param("pageSize") Integer pageSize,
                                           @Param("currentPage") Integer currentPage) {
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        Preconditions.checkNotNull(stage, "parameters of stage is required");
        Preconditions.checkArgument(currentPage != null && currentPage > 0, "parameters of currentPage is required");
        Preconditions.checkArgument(currentPage != null && currentPage > 0, "parameters of pageSize is required");

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
                for (EngineJobCache engineJobCache : engineJobCaches) {
                    Map<String, Object> theJobMap = PublicUtil.objectToMap(engineJobCache);
                    EngineJob engineJob = engineJobDao.getRdosJobByJobId(engineJobCache.getJobId());
                    if (engineJob != null) {
                        this.fillJobInfo(theJobMap, engineJob, engineJobCache);
                    }
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

    private void fillJobInfo(Map<String, Object> theJobMap, EngineJob engineJob, EngineJobCache engineJobCache) {
        theJobMap.put("status", engineJob.getStatus());
        theJobMap.put("execStartTime", engineJob.getExecStartTime());
        theJobMap.put("generateTime", engineJobCache.getGmtCreate());
        long currentTime = System.currentTimeMillis();
        String waitTime = DateUtil.getTimeDifference(currentTime - engineJobCache.getGmtCreate().getTime());
        theJobMap.put("waitTime", waitTime);
    }

    public Boolean jobStick(@Param("jobId") String jobId,
                            @Param("jobResource") String jobResource) {
        Preconditions.checkNotNull(jobId, "parameters of jobId is required");
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");

        try {
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);
            jobClient.setCallBack((jobStatus) -> {
                workNode.updateJobStatus(jobClient.getTaskId(), jobStatus);
            });
            workNode.addGroupPriorityQueue(engineJobCache.getJobResource(), jobClient, false);
            return true;
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return false;
    }

    public void stopJob(@Param("jobId") String jobId) throws Exception {
        Preconditions.checkNotNull(jobId, "parameters of jobId is required");

        List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(Lists.newArrayList(jobId));
        if (alreadyExistJobIds.contains(jobId)) {
            logger.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
            return;
        }

        EngineJobStopRecord stopRecord = new EngineJobStopRecord();
        stopRecord.setTaskId(jobId);

        engineJobStopRecordDao.insert(stopRecord);
    }

    public void stopJobList(@Param("jobResource") String jobResource,
                            @Param("nodeAddress") String nodeAddress,
                            @Param("stage") Integer stage,
                            @Param("jobIdList") List<String> jobIdList) throws Exception {
        if (jobIdList != null && !jobIdList.isEmpty()) {
            //杀死指定jobIdList的任务

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
                List<String> jobIds = jobCaches.stream().map(EngineJobCache::getJobId).collect(Collectors.toList());
                List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(jobIds);
                for (EngineJobCache jobCache : jobCaches) {
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

        Map<String, Object> yarnConfig = JSONObject.parseObject(yarnComponent.getComponentConfig(), Map.class);

        return getResources(yarnConfig, cluster.getId());
    }

    @Forbidden
    public Map<String, Object> getResources(Map<String, Object> yarnConfig, Long clusterId) {
        YARNComponent yarnComponent = null;
        try {
            Map<String, Object> kerberosConfig = componentService.fillKerberosConfig(JSONObject.toJSONString(yarnConfig), clusterId);
            yarnComponent = (YARNComponent) ComponentFactory.getComponent(kerberosConfig, EComponentType.YARN);
            yarnComponent.initClusterResource(false);

            FlinkComponent flinkComponent = (FlinkComponent) ComponentFactory.getComponent(null, EComponentType.FLINK);
            flinkComponent.initTaskManagerResource(yarnComponent.getYarnClient());

            Map<String, Object> clusterResources = new HashMap<>(2);
            clusterResources.put("yarn", yarnComponent.getClusterNodes());
            clusterResources.put("flink", flinkComponent.getTaskManagerDescriptions());
            return clusterResources;
        } catch (Exception e) {
            logger.error(" ", e);
            throw new RdosDefineException("flink资源获取异常");
        } finally {
            if (yarnComponent != null) {
                yarnComponent.closeYarnClient();
            }
        }
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
