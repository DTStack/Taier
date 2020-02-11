package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.queue.OrderLinkedBlockingQueue;
import com.dtstack.engine.dao.RdosEngineJobDAO;
import com.dtstack.engine.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.domain.RdosEngineJob;
import com.dtstack.engine.domain.RdosEngineJobCache;
import com.dtstack.engine.common.queue.GroupPriorityQueue;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.zookeeper.ZkDistributed;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/18
 */
@Service
public class ConsoleService {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleService.class);

    private RdosEngineJobDAO engineBatchJobDAO = new RdosEngineJobDAO();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private WorkNode workNode = WorkNode.getInstance();

    private ZkDistributed zkDistributed = ZkDistributed.createZkDistributed(null);

    public List<String> nodes() {
        try {
            return zkDistributed.getAliveBrokersChildren();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public String getNodeByJobName(@Param("computeType") String computeType,
                                     @Param("jobName") String jobName) {
        Preconditions.checkNotNull(computeType, "parameters of computeType is required");
        ComputeType type = ComputeType.valueOf(computeType.toUpperCase());
        Preconditions.checkNotNull(type, "parameters of computeType is STREAM/BATCH");
        String jobId = null;
        RdosEngineJob batchJob = engineBatchJobDAO.getByName(jobName);
        if (batchJob != null) {
        	jobId = batchJob.getJobId();
        }
        if (jobId == null) {
            return null;
        }
        RdosEngineJobCache jobCache = engineJobCacheDao.getJobById(jobId);
        if (jobCache == null) {
            return null;
        }
        return jobCache.getNodeAddress();
    }

    public Map<String, Object> searchJob(@Param("computeType") String computeType,
                                         @Param("jobName") String jobName) {
        Preconditions.checkNotNull(computeType, "parameters of computeType is required");
        ComputeType type = ComputeType.valueOf(computeType.toUpperCase());
        Preconditions.checkNotNull(type, "parameters of computeType is STREAM/BATCH");
        String jobId = null;
        RdosEngineJob batchJob = engineBatchJobDAO.getByName(jobName);
        if (batchJob != null) {
        	jobId = batchJob.getJobId();
        }
        if (jobId == null) {
            return null;
        }
        RdosEngineJobCache jobCache = engineJobCacheDao.getJobById(jobId);
        if (jobCache == null) {
            return null;
        }
        try {
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
            JobClient theJobClient = new JobClient(paramAction);
            GroupPriorityQueue priorityQueue = workNode.getPriorityQueue(theJobClient);
            if (priorityQueue == null) {
                return null;
            }
            OrderLinkedBlockingQueue<JobClient> jobQueue = priorityQueue.getQueue();
            if (jobQueue == null) {
                return null;
            }
            OrderLinkedBlockingQueue.IndexNode<JobClient> idxNode = jobQueue.getElement(jobId);
            if (idxNode == null) {
                return null;
            }
            JobClient theJob = idxNode.getItem();
            Map<String, Object> theJobMap = PublicUtil.ObjectToMap(theJob);
            setJobFromDB(type, theJob.getTaskId(), theJobMap);
            theJobMap.put("generateTime", theJob.getGenerateTime());

            Map<String, Object> result = new HashMap<>();
            result.put("theJob", Lists.newArrayList(theJobMap));
            result.put("theJobIdx", idxNode.getIndex());
            result.put("node", jobCache.getNodeAddress());

            return result;
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return null;
    }

    public List<String> listNames(@Param("computeType") String computeType,
                                  @Param("jobName") String jobName) {
        try {
            Preconditions.checkNotNull(computeType, "parameters of computeType is required");
            ComputeType type = ComputeType.valueOf(computeType.toUpperCase());
            Preconditions.checkNotNull(type, "parameters of computeType is STREAM/BATCH");
            return engineJobCacheDao.listNames(type.getType(),jobName);
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return null;
    }

    public List<String> engineTypes() {
        List<String> types = new ArrayList<>(EngineType.values().length);
        for (EngineType engineType : EngineType.values()) {
            types.add(engineType.name().toLowerCase());
        }
        return types;
    }

    //TODO, 控制台改造
    public Collection<Map<String, Object>> groups(@Param("engineType") String engineType,
                                                  @Param("jobResource") String jobResource) {
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        GroupPriorityQueue priorityQueue = workNode.getPriorityQueue(jobResource);
        if (priorityQueue != null) {
            List<Map<String, Object>> groups = new ArrayList<>(1);

            int groupSize = priorityQueue.getQueue().size();
            long generateTime = 0L;
            long waitTime = 0L;
            if (groupSize > 0) {
                JobClient jobClient = priorityQueue.getQueue().getTop();
                generateTime = jobClient.getGenerateTime();
                waitTime = System.currentTimeMillis() - jobClient.getGenerateTime();
            }
            Map<String, Object> element = new HashMap<>(3);
            element.put("groupName", jobResource);
            element.put("groupSize", groupSize);
            element.put("generateTime", generateTime);
            element.put("waitTime", waitTime);
            groups.add(element);
            return groups;
        }
        return Collections.EMPTY_SET;
    }

    //TODO, 控制台改造
    public Map<String, Object> groupDetail(@Param("engineType") String engineType,
                                           @Param("groupName") String groupName,
                                           @Param("jobResource") String jobResource,
                                           @Param("pageSize") int pageSize,
                                           @Param("currentPage") int currentPage) {
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        try {
            GroupPriorityQueue priorityQueue = workNode.getPriorityQueue(engineType);
            OrderLinkedBlockingQueue<JobClient> jobQueue = priorityQueue.getQueue();
            if (jobQueue == null){
                return null;
            }
            int queueSize = jobQueue.size();
            List<Map<String, Object>> topN = new ArrayList<>();
            Map<String, Object> result = new HashMap<>();
            result.put("queueSize", queueSize);
            result.put("topN", topN);

            Iterator<JobClient> jobIt = jobQueue.iterator();
            int startIndex = pageSize * (currentPage - 1);
            if (startIndex > queueSize) {
                return result;
            }
            int c = 0;
            while (jobIt.hasNext()) {
                JobClient jobClient = jobIt.next();
                c++;
                if (startIndex < c && pageSize-- > 0) {
                    Map<String, Object> jobMap = PublicUtil.ObjectToMap(jobClient);
                    setJobFromDB(jobClient.getComputeType(), jobClient.getTaskId(), jobMap);
                    jobMap.put("generateTime", jobClient.getGenerateTime());
                    topN.add(jobMap);
                }
                if (pageSize <= 0) {
                    break;
                }
            }
            if (topN.size() > queueSize){
                queueSize = topN.size();
                result.put("queueSize", queueSize);
            }
            return result;
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return null;
    }

    //TODO, 控制台改造
    public Boolean jobPriority(@Param("jobId") String jobId,
                               @Param("engineType") String engineType,
                               @Param("groupName") String groupName,
                               @Param("jobResource") String jobResource,
                               @Param("jobIndex") int jobIndex) {

        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        Preconditions.checkNotNull(jobId, "parameters of jobId is required");

        try {
            GroupPriorityQueue priorityQueue = workNode.getPriorityQueue(jobResource);
            OrderLinkedBlockingQueue<JobClient> jobQueue = priorityQueue.getQueue();
            if (jobQueue == null) {
                return false;
            }
            OrderLinkedBlockingQueue.IndexNode<JobClient> jobIdxNode = jobQueue.getElement(jobId);
            if (jobIdxNode == null) {
                return false;
            }
            JobClient theJob = jobIdxNode.getItem();
            JobClient idxJob = jobQueue.getIndexOrLast(jobIndex);
            if (idxJob == null) {
                return false;
            }
            if (theJob.getPriority() == idxJob.getPriority()) {
                return true;
            }
            theJob.setPriority(idxJob.getPriority() - 1);
            jobQueue.remove(theJob.getTaskId());
            jobQueue.put(theJob);
            return true;
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return false;
    }

    private void setJobFromDB(ComputeType computeType, String jobId, Map<String, Object> jobMap) {
        RdosEngineJob engineBatchJob = engineBatchJobDAO.getRdosTaskByTaskId(jobId);
        if (engineBatchJob != null) {
        	Integer status = engineBatchJob.getStatus().intValue();
        	jobMap.put("status", status);
        	jobMap.put("execStartTime", engineBatchJob.getExecStartTime());
        }
    }
}
