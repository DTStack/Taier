package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.util.ApplicationWSParser;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.UrlUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reason: 查询实时任务数据
 * Date: 2018/10/11
 * Company: www.dtstack.com
 * @author jiangbo
 */
@Service
public class StreamTaskService implements com.dtstack.engine.api.service.StreamTaskService {

    private static final Logger logger = LoggerFactory.getLogger(StreamTaskService.class);

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private WorkerOperator workerOperator;

    private static final String APPLICATION_REST_API_TMP = "%s/ws/v1/cluster/apps/%s";


    /**
     * 查询checkPoint
     */
    public List<EngineJobCheckpoint> getCheckPoint(@Param("taskId") String taskId, @Param("triggerStart") Long triggerStart, @Param("triggerEnd") Long triggerEnd){
        return engineJobCheckpointDao.listByTaskIdAndRangeTime(taskId,triggerStart,triggerEnd);
    }

    public EngineJobCheckpoint getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("engineTaskId") String engineTaskId){
        return engineJobCheckpointDao.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
    }

    /**
     * 查询stream job
     */
    public List<ScheduleJob> getEngineStreamJob(@Param("taskIds") List<String> taskIds){
        return scheduleJobDao.getRdosJobByJobIds(taskIds);
    }

    /**
     * 获取某个状态的任务task_id
     */
    public List<String> getTaskIdsByStatus(@Param("status") Integer status){
        return scheduleJobDao.getJobIdsByStatus(status, ComputeType.STREAM.getType());
    }

    /**
     * 获取任务的状态
     */
    public Integer getTaskStatus(@Param("taskId") String taskId){
        Integer status = null;
        if (StringUtils.isNotEmpty(taskId)){
        	ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
            if (scheduleJob != null){
                status = scheduleJob.getStatus();
            }
        }

        return status;
    }

    /**
     * 获取实时计算运行中任务的日志URL
     * @param taskId
     * @return
     */
    public Pair<String, String> getRunningTaskLogUrl(@Param("taskId") String taskId) {

        Preconditions.checkState(StringUtils.isNotEmpty(taskId), "taskId can't be empty");

        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
        Preconditions.checkNotNull(scheduleJob, "can't find record by taskId" + taskId);

        //只获取运行中的任务的log—url
        Integer status = scheduleJob.getStatus();
        if (!RdosTaskStatus.RUNNING.getStatus().equals(status.intValue())) {
            throw new RdosDefineException(String.format("job:%s not running status ", taskId), ErrorCode.INVALID_TASK_STATUS);
        }

        String applicationId = scheduleJob.getApplicationId();

        if (StringUtils.isEmpty(applicationId)) {
            throw new RdosDefineException(String.format("job %s not running in perjob", taskId), ErrorCode.INVALID_TASK_RUN_MODE);
        }

        Preconditions.checkState(applicationId.contains("application"), String.format("current task %s don't have application id.", taskId));

        JobClient jobClient = null;
        JobIdentifier jobIdentifier = null;

        //如何获取url前缀
        try{
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(taskId);
            if (engineJobCache == null) {
                throw new RdosDefineException(String.format("job:%s not exist in job cache table ", taskId),ErrorCode.JOB_CACHE_NOT_EXIST);
            }
            String jobInfo = engineJobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);

            jobIdentifier = JobIdentifier.createInstance(scheduleJob.getEngineJobId(), applicationId, taskId);
            jobClient = new JobClient(paramAction);
            String jobMaster = workerOperator.getJobMaster(jobClient.getEngineType(), jobClient.getPluginInfo(), jobIdentifier);
            String rootUrl = UrlUtil.getHttpRootUrl(jobMaster);
            String requestUrl = String.format(APPLICATION_REST_API_TMP, rootUrl, applicationId);

            String response = PoolHttpClient.get(requestUrl);
            String amContainerLogsUrl = ApplicationWSParser.getAmContainerLogsUrl(response);

            String logPreUrl = UrlUtil.getHttpRootUrl(amContainerLogsUrl);
            String amContainerPreViewHttp = PoolHttpClient.get(amContainerLogsUrl);
            return ApplicationWSParser.parserAmContainerPreViewHttp(amContainerPreViewHttp, logPreUrl);

        }catch (Exception e){
            if (jobClient != null && jobIdentifier != null) {
                RdosTaskStatus jobStatus = workerOperator.getJobStatus(jobClient.getEngineType(), jobClient.getPluginInfo(), jobIdentifier);;
                Integer statusCode = jobStatus.getStatus();
                if (RdosTaskStatus.getStoppedStatus().contains(statusCode)) {
                    throw new RdosDefineException(String.format("job:%s had stop ", taskId), ErrorCode.INVALID_TASK_STATUS, e);
                }
            }
            throw new RdosDefineException(String.format("get job:%s ref application url error..", taskId), ErrorCode.UNKNOWN_ERROR, e);
        }

    }
}
