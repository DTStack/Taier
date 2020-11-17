package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.enums.EDeployMode;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
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
public class StreamTaskService {

    private static final Logger logger = LoggerFactory.getLogger(StreamTaskService.class);

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private ScheduleJobService scheduleJobService;
    /**
     * 查询checkPoint
     */
    public List<EngineJobCheckpoint> getCheckPoint( String taskId,  Long triggerStart,  Long triggerEnd){
        return engineJobCheckpointDao.listByTaskIdAndRangeTime(taskId,triggerStart,triggerEnd);
    }

    public EngineJobCheckpoint getByTaskIdAndEngineTaskId( String taskId,  String engineTaskId){
        return engineJobCheckpointDao.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
    }

    /**
     * 查询stream job
     */
    public List<ScheduleJob> getEngineStreamJob( List<String> taskIds){
        return scheduleJobDao.getRdosJobByJobIds(taskIds);
    }

    /**
     * 获取某个状态的任务task_id
     */
    public List<String> getTaskIdsByStatus( Integer status){
        return scheduleJobDao.getJobIdsByStatus(status, ComputeType.STREAM.getType());
    }

    /**
     * 获取任务的状态
     */
    public Integer getTaskStatus(String taskId) {
        Integer status = null;
        if (StringUtils.isNotEmpty(taskId)) {
            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
            if (scheduleJob != null) {
                status = scheduleJob.getStatus();
                return RdosTaskStatus.getShowStatus(status);
            }
        }

        return null;
    }

    /**
     * 获取实时计算运行中任务的日志URL
     * @param taskId
     * @return
     */
    public List<String> getRunningTaskLogUrl( String taskId) {

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

            jobIdentifier = new JobIdentifier(scheduleJob.getEngineJobId(), applicationId, taskId,scheduleJob.getDtuicTenantId(),engineJobCache.getEngineType(),
                    EDeployMode.PERJOB.getType(),paramAction.getUserId(),null);
            jobClient = new JobClient(paramAction);

            List<String> rollingLogBaseInfo = workerOperator.getRollingLogBaseInfo(jobIdentifier);
            return rollingLogBaseInfo;

        }catch (Exception e){
            if (jobClient != null) {
                RdosTaskStatus jobStatus = workerOperator.getJobStatus(jobIdentifier);;
                Integer statusCode = jobStatus.getStatus();
                if (RdosTaskStatus.getStoppedStatus().contains(statusCode)) {
                    throw new RdosDefineException(String.format("job:%s had stop ", taskId), ErrorCode.INVALID_TASK_STATUS, e);
                }
            }
            throw new RdosDefineException(String.format("get job:%s ref application url error..", taskId), ErrorCode.UNKNOWN_ERROR, e);
        }

    }
}
