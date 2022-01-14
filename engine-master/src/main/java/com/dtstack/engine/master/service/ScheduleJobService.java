package com.dtstack.engine.master.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.domain.po.SimpleScheduleJobPO;
import com.dtstack.engine.dto.ScheduleJobDTO;
import com.dtstack.engine.mapper.*;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.pojo.ParamActionExt;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.mapstruct.ScheduleJobMapStruct;
import com.dtstack.engine.master.server.builder.ScheduleJobDetails;
import com.dtstack.engine.master.server.scheduler.JobPartitioner;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.pager.PageQuery;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.util.RetryUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:14 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobService extends ServiceImpl<ScheduleJobMapper, ScheduleJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobService.class);

    @Autowired
    private ZkService zkService;

    @Autowired
    private JobPartitioner jobPartitioner;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private JobGraphTriggerService jobGraphTriggerService;

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Autowired
    private EngineJobCacheService engineJobCacheService;

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private ScheduleJobExpandMapper scheduleJobExpandMapper;

    /**
     * 批量设置实例状态
     *
     * @param resumeBatchJobs 实例集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchRestartScheduleJob(Map<String, String> resumeBatchJobs) {
        if (MapUtils.isNotEmpty(resumeBatchJobs)) {
            List<String> restartJobId = new ArrayList<>(resumeBatchJobs.size());
            resumeBatchJobs.entrySet()
                    .stream()
                    .sorted(Comparator.nullsFirst(Map.Entry.comparingByValue(Comparator.nullsFirst(String::compareTo))))
                    .forEachOrdered(v -> {
                        if (null!= v && StringUtils.isNotBlank(v.getKey())) {
                            restartJobId.add(v.getKey());
                        }
                    });

            List<List<String>> partition = Lists.partition(restartJobId, environmentContext.getRestartOperatorRecordMaxSize());
            for (List<String> scheduleJobs : partition) {
                Set<String> jobIds = new HashSet<>(scheduleJobs.size());
                Set<ScheduleJobOperatorRecord> records = new HashSet<>(scheduleJobs.size());
                //更新任务为重跑任务--等待调度器获取并执行
                for (String jobId : scheduleJobs) {
                    jobIds.add(jobId);
                    ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                    record.setJobId(jobId);
                    record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                    record.setOperatorType(OperatorType.RESTART.getType());
                    record.setNodeAddress(environmentContext.getLocalAddress());
                    records.add(record);
                }

                ScheduleJob scheduleJob = new ScheduleJob();
                scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
                scheduleJob.setPhaseStatus(JobPhaseStatus.CREATE.getCode());
                scheduleJob.setIsRestart(JobPhaseStatus.CREATE.getCode());
                scheduleJob.setNodeAddress(environmentContext.getLocalAddress());

                // 更新状态
                 this.lambdaUpdate()
                        .eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                        .in(ScheduleJob::getJobId, jobIds)
                        .update(scheduleJob);

                 // 清除日志
                scheduleJobExpandService.clearData(jobIds);
                scheduleJobOperatorRecordService.saveBatch(records);
                LOGGER.info("reset job {}", jobIds);
            }
        }
    }

    /**
     * 批量插入周期实例 jobSize 在负载均衡时 区分 scheduleType（正常调度 和 补数据）
     *
     * @param jobBuilderBeanCollection 实例集合
     * @param scheduleType 调度类型 正常调度 和 补数据
     */
    @Transactional(rollbackFor = Exception.class)
    public Long insertJobList(Collection<ScheduleJobDetails> jobBuilderBeanCollection, Integer scheduleType) {
        if (CollectionUtils.isEmpty(jobBuilderBeanCollection)) {
            return null;
        }

        Iterator<ScheduleJobDetails> batchJobIterator = jobBuilderBeanCollection.iterator();

        //count%20 为一批
        //1: 批量插入BatchJob
        //2: 批量插入BatchJobJobList
        int count = 0;
        int jobBatchSize = environmentContext.getBatchJobInsertSize();
        int jobJobBatchSize = environmentContext.getBatchJobJobInsertSize();
        Long minJobId=null;
        List<ScheduleJob> jobWaitForSave = Lists.newArrayList();
        List<ScheduleJobJob> jobJobWaitForSave = Lists.newArrayList();

        Map<String, Integer> nodeJobSize = computeJobSizeForNode(jobBuilderBeanCollection.size(), scheduleType);
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            final int finalBatchNodeSize = nodeSize;
            while (nodeSize > 0 && batchJobIterator.hasNext()) {
                nodeSize--;
                count++;

                ScheduleJobDetails jobBuilderBean = batchJobIterator.next();

                ScheduleJob scheduleJob = jobBuilderBean.getScheduleJob();
                scheduleJob.setNodeAddress(nodeAddress);

                jobWaitForSave.add(scheduleJob);
                jobJobWaitForSave.addAll(jobBuilderBean.getJobJobList());

                LOGGER.debug("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{}", count, jobBuilderBeanCollection.size(), finalBatchNodeSize);
                if (count % jobBatchSize == 0 || count == (jobBuilderBeanCollection.size() - 1) || jobJobWaitForSave.size() > jobJobBatchSize) {
                    minJobId = persistJobs(jobWaitForSave, jobJobWaitForSave, minJobId,jobJobBatchSize);
                    LOGGER.info("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{} jobJobSize:{}", count, jobBuilderBeanCollection.size(), finalBatchNodeSize, jobJobWaitForSave.size());
                }
            }
            LOGGER.info("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{}",count, jobBuilderBeanCollection.size(), finalBatchNodeSize);
            //结束前persist一次，flush所有jobs
            minJobId = persistJobs(jobWaitForSave, jobJobWaitForSave, minJobId,jobJobBatchSize);

        }
        return minJobId;
    }

    /**
     * 获得调度各个节点的ip
     *
     * @param jobSize 实例数
     * @param scheduleType 调度类型 正常调度 和 补数据
     */
    private Map<String, Integer> computeJobSizeForNode(int jobSize, int scheduleType) {
        Map<String, Integer> jobSizeInfo = jobPartitioner.computeBatchJobSize(scheduleType, jobSize);
        if (jobSizeInfo == null) {
            //if empty
            List<String> aliveNodes = zkService.getAliveBrokersChildren();
            jobSizeInfo = new HashMap<>(aliveNodes.size());
            int size = jobSize / aliveNodes.size() + 1;
            for (String aliveNode : aliveNodes) {
                jobSizeInfo.put(aliveNode, size);
            }
        }
        return jobSizeInfo;
    }

    /**
     * 插入实例
     */
    private Long persistJobs(List<ScheduleJob> jobWaitForSave, List<ScheduleJobJob> jobJobWaitForSave, Long minJobId,Integer jobJobSize) {
        try {
            return RetryUtil.executeWithRetry(() -> {
                Long curMinJobId=minJobId;
                if (jobWaitForSave.size() > 0) {
                    this.saveBatch(jobWaitForSave);
                    if (Objects.isNull(minJobId)) {
                        curMinJobId = jobWaitForSave.stream().map(ScheduleJob::getId).min(Long::compareTo).orElse(null);
                    }

                    // 插入扩展数据
                   List<ScheduleJobExpand> scheduleJobExpandList =  ScheduleJobMapStruct.INSTANCE.scheduleJobTOScheduleJobExpand(jobWaitForSave);
                    scheduleJobExpandService.saveBatch(scheduleJobExpandList);
                    jobWaitForSave.clear();
                }
                if (jobJobWaitForSave.size() > 0) {
                    if (jobJobWaitForSave.size() > jobJobSize) {
                        List<List<ScheduleJobJob>> partition = Lists.partition(jobJobWaitForSave, jobJobSize);
                        for (List<ScheduleJobJob> scheduleJobJobs : partition) {
                            scheduleJobJobService.saveBatch(scheduleJobJobs);
                            jobJobWaitForSave.removeAll(scheduleJobJobs);
                        }
                    } else {
                        scheduleJobJobService.saveBatch(jobJobWaitForSave);
                    }
                    jobJobWaitForSave.clear();
                }
                return curMinJobId;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            LOGGER.error("!!!!! persistJobs job error !!!! job {} jobjob {}", jobWaitForSave, jobJobWaitForSave, e);
            throw new RdosDefineException(e);
        } finally {
            if (jobWaitForSave.size() > 0) {
                jobWaitForSave.clear();
            }
            if (jobJobWaitForSave.size() > 0) {
                jobJobWaitForSave.clear();
            }
        }
    }


    public Integer updateJobStatusAndExecTime(String jobId, Integer status) {
        if (StringUtils.isNotBlank(jobId) && status != null) {
            return this.baseMapper.updateJobStatusAndExecTime(jobId, status);
        }
        return 0;
    }


    public ScheduleJob getJobByJobKeyAndType(String jobKey, int type) {
        return scheduleJobDao.getByJobKeyAndType(jobKey, type);
    }


    public Integer updateStatusAndLogInfoById(String jobId, Integer status, String msg) {
        if (StringUtils.isNotBlank(msg) && msg.length() > 5000) {
            msg = msg.substring(0, 5000) + "...";
        }
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setStatus(status);
        scheduleJob.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        updateByJobId(scheduleJob);
        return updateExpandByJobId(jobId,null,msg);
    }

    public Integer updateStatusByJobId(String jobId, Integer status, Integer versionId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setStatus(status);
        scheduleJob.setVersionId(versionId);
        scheduleJob.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        return updateByJobId(scheduleJob);
    }

    public Long startJob(ScheduleJob scheduleJob) throws Exception {
        sendTaskStartTrigger(scheduleJob);
        return scheduleJob.getId();
    }


    public Integer updateStatusWithExecTime(ScheduleJob updateJob) {
        if(null == updateJob || null == updateJob.getJobId() ){
            return 0;
        }
        ScheduleJob job = scheduleJobDao.getByJobId(updateJob.getJobId(), Deleted.NORMAL.getStatus());
        if (null != job.getExecStartTime() && null != updateJob.getExecEndTime()){
//            updateJob.setExecTime((updateJob.getExecEndTime().getTime()-job.getExecStartTime().getTime())/1000);
        }
        return scheduleJobDao.updateStatusWithExecTime(updateJob);
    }


    /**
     * 触发 engine 执行指定task
     */
    public void sendTaskStartTrigger(ScheduleJob scheduleJob) throws Exception {
        ScheduleTaskShade batchTask = batchTaskShadeService.getByTaskId(scheduleJob.getTaskId());
        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        if(EScheduleJobType.WORK_FLOW.getType().equals(batchTask.getTaskType()) || EScheduleJobType.VIRTUAL.getType().equals(batchTask.getTaskType())){
            runVirtualTask(scheduleJob,batchTask);
            return;
        }
//        String extInfoByTaskId = scheduleTaskShadeDao.getExtInfoByTaskId(scheduleJob.getTaskId(), 1);
        String extInfoByTaskId = null;
        if (StringUtils.isNotBlank(extInfoByTaskId)) {
            JSONObject extObject = JSONObject.parseObject(extInfoByTaskId);
            if (null != extObject ) {
                JSONObject info = extObject.getJSONObject(TaskConstant.INFO);
                if (null != info ) {
                    ParamActionExt paramActionExt = actionService.paramActionExt(batchTask, scheduleJob, info);
                    if (paramActionExt != null) {
                        updateStatusByJobId(scheduleJob.getJobId(), RdosTaskStatus.SUBMITTING.getStatus(),batchTask.getVersionId());
                        actionService.start(paramActionExt);
                        return;
                    }
                }
            }
        }
        //额外信息为空 标记任务为失败
        this.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.FAILED.getStatus(), "任务运行信息为空");
        LOGGER.error(" job  {} run fail with info is null",scheduleJob.getJobId());
    }



    private void runVirtualTask(ScheduleJob scheduleJob, ScheduleTaskShade batchTask) {
        ScheduleJob updateJob = new ScheduleJob();
        updateJob.setJobId(scheduleJob.getJobId());
        updateJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
        updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        if (EScheduleJobType.VIRTUAL.getType().equals(batchTask.getTaskType())) {
            //虚节点直接完成虚节点写入开始时间和结束时间
            updateJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
            updateJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
            updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
            updateJob.setExecTime(0L);
            scheduleJobDao.updateStatusWithExecTime(updateJob);
        }

        //工作流节点保持提交中状态
        if (EScheduleJobType.WORK_FLOW.getVal().equals(batchTask.getTaskType())) {
            updateJob.setStatus(RdosTaskStatus.SUBMITTING.getStatus());
        }
        scheduleJobDao.updateStatusWithExecTime(updateJob);
    }

    public void stopJob( long jobId, Integer appType) {

        ScheduleJob scheduleJob = scheduleJobDao.getOne(jobId);
        stopJobByScheduleJob(appType, scheduleJob);
        // 杀死工作流任务，已经强规则任务
        List<ScheduleJob> jobs = Lists.newArrayList(scheduleJob);
        jobStopDealer.addStopJobs(jobs);
    }

    private void stopJobByScheduleJob(  Integer appType, ScheduleJob scheduleJob) {

        if (scheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        ScheduleTaskShade task = batchTaskShadeService.getByTaskId(scheduleJob.getTaskId());
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        Integer status = scheduleJob.getStatus();
        if (!checkJobCanStop(status)) {
            throw new RdosDefineException(ErrorCode.JOB_CAN_NOT_STOP);
        }

        jobStopDealer.addStopJobs(Lists.newArrayList(scheduleJob));
    }

    private boolean checkJobCanStop(Integer status) {
        if (status == null) {
            return true;
        }
        return RdosTaskStatus.getCanStopStatus().contains(status);
    }





    /**
     * 根据工作流id获取子任务信息与任务状态
     *
     * @param jobId
     * @return
     */
    public List<ScheduleJob> getSubJobsAndStatusByFlowId(String jobId) {
        return scheduleJobDao.getSubJobsAndStatusByFlowId(jobId);
    }



    public List<String> listJobIdByTaskNameAndStatusList( String taskName,  List<Integer> statusList,  Long projectId, Integer appType) {
        ScheduleTaskShade task = batchTaskShadeService.getByName(taskName);
        if (task != null) {
            return scheduleJobDao.listJobIdByTaskIdAndStatus(task.getTaskId(), null ,statusList);
        }
        return new ArrayList<>();
    }


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param tenantId
     * @return
     */
    public Map<String, ScheduleJob> getLabTaskRelationMap( List<String> jobIdList,  Long tenantId) {

        if(CollectionUtils.isEmpty(jobIdList)){
            return Collections.EMPTY_MAP;
        }
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listByJobIdList(jobIdList, tenantId);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            Map<String, ScheduleJob> jobMap = new HashMap<>();
            for (ScheduleJob scheduleJob : scheduleJobs) {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(scheduleJob.getFlowJobId(), Deleted.NORMAL.getStatus());
                jobMap.put(scheduleJob.getJobId(), flowJob);
            }
            return jobMap;
        }
        return new HashMap<>();
    }

    /**
     * 获取任务执行信息
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    public List<Map<String, Object>> statisticsTaskRecentInfo( Long taskId,  Integer appType,  Long projectId,  Integer count) {

        return scheduleJobDao.listTaskExeInfo(taskId, projectId, count, appType);

    }

    public ScheduleJob getById( Long id) {

        return scheduleJobDao.getOne(id);
    }

    public ScheduleJob getByJobId( String jobId,  Integer isDeleted) {
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, isDeleted);

        return scheduleJob;
    }

    public Integer getJobStatus(String jobId){
        Integer status = scheduleJobDao.getStatusByJobId(jobId);
        if (Objects.isNull(status)) {
            throw new RdosDefineException("job not exist");
        }
        return status;
    }

    public Integer generalCount(ScheduleJobDTO query) {
        query.setPageQuery(false);
        return scheduleJobDao.generalCount(query);
    }


    public List<ScheduleJob> generalQuery(PageQuery query) {
        return scheduleJobDao.generalQuery(query);
    }


    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    public void updateJobStatusAndLogInfo( String jobId,  Integer status,  String logInfo) {

        scheduleJobDao.updateStatusByJobId(jobId, status, logInfo,null,null,null);
    }

    public boolean updatePhaseStatusById(Long id, JobPhaseStatus original, JobPhaseStatus update) {
        if (id==null|| original==null|| update==null) {
            return Boolean.FALSE;
        }

        Integer integer = scheduleJobDao.updatePhaseStatusById(id, original.getCode(), update.getCode());

        if (integer != null && !integer.equals(0)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Long getListMinId(String left, String right) {
        // 如果没有时间限制, 默认返回0
        if (StringUtils.isAnyBlank(left,right)){
            return 0L;
        }
        // 如果当前时间范围没有数据, 返回NULL
        String minJobId = jobGraphTriggerService.getMinJobIdByTriggerTime(left, right);
        if (StringUtils.isBlank(minJobId)){
            return 0L;
        }
        return Long.parseLong(minJobId);
    }



    /**
     * 移除满足条件的job 操作记录
     * @param jobIds
     * @param records
     */
    public void removeOperatorRecord(Collection<String> jobIds, Collection<ScheduleJobOperatorRecord> records) {
        Map<String, ScheduleJobOperatorRecord> recordMap = records.stream().collect(Collectors.toMap(ScheduleJobOperatorRecord::getJobId, k -> k));
        for (String jobId : jobIds) {
            ScheduleJobOperatorRecord record = recordMap.get(jobId);
            if (null == record) {
                continue;
            }
            EngineJobCache cache = engineJobCacheService.getByJobId(jobId);
            if (cache != null && cache.getGmtCreate().after(record.getGmtCreate())) {
                //has submit to cache
                scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
                LOGGER.info("remove schedule:[{}] operator record:[{}] time: [{}] stage:[{}] type:[{}]", record.getJobId(), record.getId(), cache.getGmtCreate(), cache.getStage(), record.getOperatorType());
            }
            ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, null);
            if (null == scheduleJob) {
                LOGGER.info("schedule job is null ,remove schedule:[{}] operator record:[{}] type:[{}] ", record.getJobId(), record.getId(), record.getOperatorType());
                scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
            } else if (scheduleJob.getGmtModified().after(record.getGmtCreate())) {
                if (RdosTaskStatus.STOPPED_STATUS.contains(scheduleJob.getStatus()) || RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus())) {
                    //has running or finish
                    scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
                    LOGGER.info("remove schedule:[{}] operator record:[{}] time: [{}] status:[{}] type:[{}]", record.getJobId(), record.getId(), scheduleJob.getGmtModified(), scheduleJob.getStatus(), record.getOperatorType());
                }
            }
        }
    }



    public ScheduleJob getByJobId(String jobId) {
        return scheduleJobMapper
                .selectOne(Wrappers.lambdaQuery(ScheduleJob.class).eq(ScheduleJob::getJobId, jobId));
    }

    public List<ScheduleJob> getByJobIds(List<String> jobId) {
        return scheduleJobMapper
                .selectList(Wrappers.lambdaQuery(ScheduleJob.class).in(ScheduleJob::getJobId, jobId));
    }

    public int updateByJobId(ScheduleJob scheduleJob) {
        if (null == scheduleJob || StringUtils.isBlank(scheduleJob.getJobId())) {
            return 0;
        }
        return scheduleJobMapper.update(scheduleJob,
                Wrappers.lambdaQuery(ScheduleJob.class)
                        .eq(ScheduleJob::getJobId, scheduleJob.getJobId()));
    }

    public int updateExpandByJobId(String jobId,String engineLog,String logInfo) {
        if (StringUtils.isBlank(jobId)) {
            return 0;
        }
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setJobId(jobId);
        scheduleJobExpand.setEngineLog(engineLog);
        scheduleJobExpand.setLogInfo(logInfo);
        return scheduleJobExpandMapper.update(scheduleJobExpand,
                Wrappers.lambdaQuery(ScheduleJobExpand.class)
                        .eq(ScheduleJobExpand::getJobId, scheduleJobExpand.getJobId()));
    }

    public int insert(ScheduleJob scheduleJob) {
        int insert = scheduleJobMapper.insert(scheduleJob);
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setJobId(scheduleJob.getJobId());
        scheduleJobExpandService.save(scheduleJobExpand);
        return insert;
    }

    @Transactional(rollbackFor = Exception.class)
    public void jobFail(String jobId, Integer status, String generateErrorMsg) {
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setJobId(jobId);
        updateScheduleJob.setStatus(status);
        scheduleJobMapper.updateById(updateScheduleJob);
        updateExpandByJobId(jobId,null,generateErrorMsg);
    }

    public int updateJobStatusByJobIds(List<String> jobIds, Integer status,Integer phaseStatus) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(status);
        scheduleJob.setPhaseStatus(phaseStatus);
        return scheduleJobMapper.update(scheduleJob,Wrappers.lambdaQuery(ScheduleJob.class)
                .in(ScheduleJob::getJobId, jobIds));
    }

    public List<SimpleScheduleJobPO> listJobByStatusAddressAndPhaseStatus(long jobStartId, List<Integer> unSubmitStatus, String localAddress, Integer phaseStatus) {
        return scheduleJobMapper.listJobByStatusAddressAndPhaseStatus(jobStartId,unSubmitStatus,localAddress,phaseStatus);
    }

    public void updateJobSubmitSuccess(String jobId, String engineJobId, String appId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setApplicationId(appId);
        scheduleJob.setEngineJobId(engineJobId);
        scheduleJob.setExecStartTime(Timestamp.valueOf(LocalDateTime.now()));
        scheduleJob.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        updateByJobId(scheduleJob);
    }

    public void updateStatus(String jobId, Integer status) {
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setJobId(jobId);
        updateScheduleJob.setStatus(status);
        scheduleJobMapper.update(updateScheduleJob, Wrappers.lambdaQuery(ScheduleJob.class)
                .eq(ScheduleJob::getJobId, jobId));
    }

    public void updateRetryNum(String jobId, Integer retryNum) {
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setJobId(jobId);
        updateScheduleJob.setRetryNum(retryNum);
        scheduleJobMapper.update(updateScheduleJob, Wrappers.lambdaQuery(ScheduleJob.class)
                .eq(ScheduleJob::getJobId, jobId));
    }
}
