package com.dtstack.rdos.engine.service;

import com.dtstack.rdos.common.annotation.Param;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EngineType;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.base.queue.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.node.GroupPriorityQueue;
import com.dtstack.rdos.engine.service.node.WorkNode;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ConsoleServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleServiceImpl.class);

    private RdosEngineStreamJobDAO engineStreamTaskDAO = new RdosEngineStreamJobDAO();

    private RdosEngineBatchJobDAO engineBatchJobDAO = new RdosEngineBatchJobDAO();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private WorkNode workNode = WorkNode.getInstance();

    public Map<String, Object> searchJob(@Param("computeType") String computeType,
                                         @Param("jobName") String jobName,
                                         @Param("pageSize") int pageSize,
                                         @Param("currentPage") int currentPage) {
        Preconditions.checkNotNull(computeType, "parameters of computeType is required");
        ComputeType type = ComputeType.valueOf(computeType.toUpperCase());
        Preconditions.checkNotNull(type, "parameters of computeType is STREAM/BATCH");
        String jobId = null;
        if (ComputeType.STREAM == type) {
            RdosEngineStreamJob streamJob = engineStreamTaskDAO.getByName(jobName);
            if (streamJob != null) {
                jobId = streamJob.getTaskId();
            }
        } else {
            RdosEngineBatchJob batchJob = engineBatchJobDAO.getByName(jobName);
            if (batchJob != null) {
                jobId = batchJob.getJobId();
            }
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
            GroupPriorityQueue queue = workNode.getEngineTypeQueue(theJobClient.getEngineType());
            OrderLinkedBlockingQueue<JobClient> jobQueue = queue.getGroupPriorityQueueMap().get(theJobClient.getGroupName());
            int queueSize = jobQueue.size();
            JobClient theJob = jobQueue.getElement(jobId);
            if (theJob == null) {
                return null;
            }

            List<Map<String, Object>> topN = new ArrayList<>();
            Iterator<JobClient> jobIt = jobQueue.iterator();
            int startIndex = pageSize * (currentPage - 1);
            int c = 0;
            while (jobIt.hasNext()) {
                if (pageSize-- <= 0 && startIndex >= c) {
                    JobClient jobClient = jobIt.next();
                    Map<String, Object> jobMap = PublicUtil.ObjectToMap(jobClient);
                    long generateTime = jobClient.getGenerateTime();
                    Integer jobStatus = getJobStatusFromDB(type, jobId);

                    jobMap.put("generateTime", generateTime);
                    jobMap.put("status", jobStatus);
                    topN.add(jobMap);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("queueSize", queueSize);
            result.put("jobId", jobId);
            result.put("topN", topN);
            return result;
        } catch (Exception e) {
            logger.info("", e);
        }
        return null;
    }

    public List<String> listNames(@Param("computeType") String computeType,
                                  @Param("jobName") String jobName) {
        try {
            Preconditions.checkNotNull(computeType, "parameters of computeType is required");
            ComputeType type = ComputeType.valueOf(computeType.toUpperCase());
            Preconditions.checkNotNull(type, "parameters of computeType is STREAM/BATCH");
            if (ComputeType.STREAM == type) {
                return engineStreamTaskDAO.listNames(jobName);
            } else {
                return engineBatchJobDAO.listNames(jobName);
            }
        } catch (Exception e) {
            logger.info("", e);
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

    public Collection<Map<String, Object>> groups(@Param("engineType") String engineType) {
        Preconditions.checkNotNull(engineType, "parameters of engineType is required");
        GroupPriorityQueue queue = workNode.getEngineTypeQueue(engineType);
        if (queue != null) {
            Map<String, OrderLinkedBlockingQueue<JobClient>> map = queue.getGroupPriorityQueueMap();
            List<Map<String, Object>> groups = new ArrayList<>(map.size());
            for (Map.Entry<String, OrderLinkedBlockingQueue<JobClient>> entry : map.entrySet()) {
                String groupName = entry.getKey();
                int groupSize = entry.getValue().size();
                long generateTime = 0L;
                if (groupSize > 0) {
                    JobClient jobClient = entry.getValue().getTop();
                    generateTime = jobClient.getGenerateTime();
                }
                Map<String, Object> element = new HashMap<>(3);
                element.put("groupName", groupName);
                element.put("groupSize", groupSize);
                element.put("generateTime", generateTime);
                groups.add(element);
            }
            return groups;
        }
        return Collections.EMPTY_SET;
    }

    private Integer getJobStatusFromDB(ComputeType computeType, String jobId) {
        if (ComputeType.STREAM == computeType) {
            RdosEngineStreamJob engineStreamJob = engineStreamTaskDAO.getRdosTaskByTaskId(jobId);
            if (engineStreamJob != null) {
                return engineStreamJob.getStatus().intValue();
            }
        } else {
            RdosEngineBatchJob engineBatchJob = engineBatchJobDAO.getByName(jobId);
            if (engineBatchJob != null) {
                return engineBatchJob.getStatus().intValue();
            }
        }
        return null;
    }
}
