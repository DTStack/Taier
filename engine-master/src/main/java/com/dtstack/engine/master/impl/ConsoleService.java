package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.common.queue.GroupPriorityQueue;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private WorkNode workNode;

    @Autowired
    private ZkService zkService;

    public List<String> nodes() {
        try {
            return zkService.getAliveBrokersChildren();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public String getNodeByJobName(@Param("jobName") String jobName) {
        Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
        String jobId = null;
        EngineJob batchJob = engineJobDao.getByName(jobName);
        if (batchJob != null) {
            jobId = batchJob.getJobId();
        }
        if (jobId == null) {
            return null;
        }
        EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
        if (jobCache == null) {
            return null;
        }
        return jobCache.getNodeAddress();
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
        EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
        if (jobCache == null) {
            return null;
        }
        try {
            Map<String, Object> theJobMap = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), Map.class);
            theJobMap.put("status", engineJob.getStatus());
            theJobMap.put("execStartTime", engineJob.getExecStartTime());
            theJobMap.put("generateTime", jobCache.getGmtCreate());

            Map<String, Object> result = new HashMap<>();
            result.put("theJob", Lists.newArrayList(theJobMap));
            result.put("theJobIdx", 1);
            result.put("node", jobCache.getNodeAddress());

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
     *
     * @param jobResource 计算引擎类型
     * @return
     */
    public List<Map<String, Object>> overview() {

        List<Map<String, Object>> groupResult = engineJobCacheDao.groupByJobResource();
        if (CollectionUtils.isNotEmpty(groupResult)) {
            groupResult.forEach(record -> {
                long generateTime = MapUtils.getLong(record, "generateTime");
                long waitTime = System.currentTimeMillis() - generateTime;
                record.put("waitTime", waitTime);
            });
        }
        return Lists.newArrayList();
    }

    public Map<String, Object> groupDetail(@Param("jobResource") String jobResource,
                                           @Param("pageSize") int pageSize,
                                           @Param("currentPage") int currentPage) {
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> topN = new ArrayList<>();
        Long count = 0L;
        result.put("queueSize", count);
        result.put("topN", topN);
        try {
            count = engineJobCacheDao.countByJobResource(jobResource);
            if (count > 0) {
                List<EngineJobCache> engineJobCaches = engineJobCacheDao.listByJobResource(jobResource);
                for (EngineJobCache engineJobCache : engineJobCaches) {
                    Map<String, Object> theJobMap = PublicUtil.objectToMap(engineJobCache);
                    theJobMap.put("generateTime", engineJobCache.getGmtCreate());
                    EngineJob engineJob = engineJobDao.getRdosJobByJobId(engineJobCache.getJobId());
                    if (engineJob != null) {
                        Integer status = engineJob.getStatus();
                        theJobMap.put("status", status);
                        theJobMap.put("execStartTime", engineJob.getExecStartTime());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return result;
    }

    public Boolean jobStick(@Param("jobId") String jobId,
                            @Param("jobResource") String jobResource) {
        Preconditions.checkNotNull(jobId, "parameters of jobId is required");
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");

        try {
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);
            jobClient.setCallBack((jobStatus)-> {
                workNode.updateJobStatus(jobClient.getTaskId(), jobStatus);
            });
            return true;
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return false;
    }

}
