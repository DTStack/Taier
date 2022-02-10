package com.dtstack.taiga.scheduler.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taiga.common.enums.ForceCancelFlag;
import com.dtstack.taiga.common.enums.Deleted;
import com.dtstack.taiga.common.enums.OperatorType;
import com.dtstack.taiga.common.env.EnvironmentContext;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.*;
import com.dtstack.taiga.dao.domain.po.SimpleScheduleJobPO;
import com.dtstack.taiga.dao.mapper.ScheduleJobMapper;
import com.dtstack.taiga.pluginapi.enums.RdosTaskStatus;
import com.dtstack.taiga.pluginapi.util.RetryUtil;
import com.dtstack.taiga.scheduler.enums.JobPhaseStatus;
import com.dtstack.taiga.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taiga.scheduler.jobdealer.JobStopDealer;
import com.dtstack.taiga.scheduler.mapstruct.ScheduleJobMapStruct;
import com.dtstack.taiga.scheduler.server.JobPartitioner;
import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;
import com.dtstack.taiga.scheduler.zookeeper.ZkService;
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

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:14 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobService extends ServiceImpl<ScheduleJobMapper, ScheduleJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobService.class);

    @Autowired
    private ZkService zkService;

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private JobPartitioner jobPartitioner;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired
    private JobGraphTriggerService jobGraphTriggerService;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;

    @Autowired
    private EngineJobCacheService engineJobCacheService;

//    @Autowired
//    private ScheduleJobExpandMapper scheduleJobExpandMapper;

    /**
     * 开始运行实例
     *
     * @param scheduleJobDetails 实例
     */
    public void startJob(ScheduleJobDetails scheduleJobDetails) throws Exception {
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        ScheduleTaskShade scheduleTaskShade = scheduleJobDetails.getScheduleTaskShade();

        // 解析任务运行信息
        String extraInfo = scheduleTaskShade.getExtraInfo();
        if (StringUtils.isNotBlank(extraInfo)) {
            JSONObject extObject = JSONObject.parseObject(extraInfo);
                ParamActionExt paramActionExt = actionService.paramActionExt(scheduleTaskShade, scheduleJob, extObject);
                if (paramActionExt != null) {
                    updateStatusByJobIdAndVersionId(scheduleJob.getJobId(), RdosTaskStatus.SUBMITTING.getStatus(),scheduleTaskShade.getVersionId());
                    actionService.start(paramActionExt);
                }
        } else {
            //额外信息为空 标记任务为失败
            this.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.FAILED.getStatus(), "任务运行信息为空");
            LOGGER.error(" job  {} run fail with info is null",scheduleJob.getJobId());
        }
    }

    /**
     * 批量设置实例状态
     *
     * @param resumeBatchJobs 实例集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void restartScheduleJob(Map<String, String> resumeBatchJobs) {
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
                        .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
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

    /**
     * 更新实例状态和版本
     * @param jobId 实例id
     * @param status 状态
     * @param versionId 版本
     */
    private Boolean updateStatusByJobIdAndVersionId(String jobId, Integer status, Integer versionId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(status);
        scheduleJob.setVersionId(versionId);
        return this.lambdaUpdate()
                .eq(ScheduleJob::getJobId,jobId)
                .eq(ScheduleJob::getIsDeleted,Deleted.NORMAL.getStatus())
                .update(scheduleJob);
    }

    /**
     * 更新状态和日志
     *
     * @param jobId   实例id
     * @param status  实例状态
     * @param logInfo 实例日志
     */
    public void updateStatusAndLogInfoById(String jobId, Integer status, String logInfo) {
        if (StringUtils.isNotBlank(logInfo) && logInfo.length() > 5000) {
            logInfo = logInfo.substring(0, 5000) + "...";
        }
        if (StringUtils.isNotBlank(jobId) && status != null) {
            updateJobStatusAndExecTime(jobId, status);
        }

        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setLogInfo(logInfo);
        scheduleJobExpandService.lambdaUpdate()
                .eq(ScheduleJobExpand::getJobId, jobId)
                .eq(ScheduleJobExpand::getIsDeleted, Deleted.NORMAL.getStatus())
                .update(scheduleJobExpand);
    }

    /**
     * 更新实例状态
     * @param jobId 实例 id
     * @param status 状态
     * @return 更新数
     */
    public Integer updateJobStatusAndExecTime(String jobId, Integer status) {
        if (StringUtils.isNotBlank(jobId) && status != null) {
            return this.baseMapper.updateJobStatusAndExecTime(jobId, status);
        }
        return 0;
    }

    /**
     * 查询实例状态
     * @param jobId 实例id
     * @return 实例状态，如果查询不到，返回null
     */
    public Integer getJobStatusByJobId(String jobId){
        if (StringUtils.isBlank(jobId)) {
            return null;
        }
        ScheduleJob scheduleJob = this.lambdaQuery().eq(ScheduleJob::getJobId, jobId).eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus()).one();

        if (scheduleJob == null) {
            return null;
        }
        return scheduleJob.getStatus();
    }

    /**
     * 更新实例队列状态
     * @param id 实例id
     * @param original 实例当前队列状态
     * @param update 实例需要变更的队列状态
     * @return 是否更新成功
     */
    public boolean updatePhaseStatusById(Long id, JobPhaseStatus original, JobPhaseStatus update) {
        if (id==null|| original==null|| update==null) {
            return Boolean.FALSE;
        }

        Integer integer = this.baseMapper.updatePhaseStatusById(id, original.getCode(), update.getCode());

        if (integer != null && !integer.equals(0)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 扫描周期实例接口
     *
     * @param startSort 开始id
     * @param nodeAddress 节点
     * @param type 类型
     * @param isEq 是否查询出第一个
     * @param jobPhaseStatus 队列状态
     * @return 周期实例列表
     */
    public List<ScheduleJob> listCycleJob(Long startSort, String nodeAddress, Integer type, Boolean isEq, Integer jobPhaseStatus) {
        if (startSort == null) {
            return Lists.newArrayList();
        }

        return this.baseMapper.listCycleJob(startSort,nodeAddress,type,isEq,jobPhaseStatus);
    }


    /**
     * 查询实例
     *
     * @param jobId 实例id
     * @return 实例
     */
    public ScheduleJob getByJobId(String jobId) {
        return this.baseMapper
                .selectOne(Wrappers.lambdaQuery(ScheduleJob.class).eq(ScheduleJob::getJobId, jobId));
    }

    /**
     * 批量查询实例
     * @param jobIds 实例id
     * @return 实例
     */
    public List<ScheduleJob> getByJobIds(List<String> jobIds) {
        return this.baseMapper
                .selectList(Wrappers.lambdaQuery(ScheduleJob.class).in(ScheduleJob::getJobId, jobIds));
    }

    /**
     * 更新周期实例
     *
     * @param scheduleJob 更新的内容
     * @return 更新的记录数
     */
    public int updateByJobId(ScheduleJob scheduleJob) {
        if (null == scheduleJob || StringUtils.isBlank(scheduleJob.getJobId())) {
            return 0;
        }
        return this.baseMapper.update(scheduleJob,
                Wrappers.lambdaQuery(ScheduleJob.class)
                        .eq(ScheduleJob::getJobId, scheduleJob.getJobId()));
    }

    /**
     * 添加日志
     * @param jobId 实例id
     * @param engineLog 引擎日志
     * @param logInfo 提交日志
     * @return 更新记录数
     */
    public Boolean updateExpandByJobId(String jobId,String engineLog,String logInfo) {
        if (StringUtils.isBlank(jobId)) {
            return Boolean.FALSE;
        }
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setJobId(jobId);
        scheduleJobExpand.setEngineLog(engineLog);
        scheduleJobExpand.setLogInfo(logInfo);
        return scheduleJobExpandService
                .lambdaUpdate()
                .eq(ScheduleJobExpand::getJobId, scheduleJobExpand.getJobId())
                .update(scheduleJobExpand);
    }

    /**
     * 插入实例
     * @param scheduleJob 实例
     * @return 插入实例数
     */
    public int insert(ScheduleJob scheduleJob) {
        int insert = this.baseMapper.insert(scheduleJob);
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setJobId(scheduleJob.getJobId());
        scheduleJobExpandService.save(scheduleJobExpand);
        return insert;
    }

    /**
     * 实例失败记录
     * @param jobId 实例id
     * @param status 状态
     * @param generateErrorMsg 失败内容
     */
    @Transactional(rollbackFor = Exception.class)
    public void jobFail(String jobId, Integer status, String generateErrorMsg) {
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setJobId(jobId);
        updateScheduleJob.setStatus(status);
        this.baseMapper.updateById(updateScheduleJob);
        updateExpandByJobId(jobId,null,generateErrorMsg);
    }

    /**
     * 更新实例状态
     *
     * @param jobIds 实例id
     * @param status 实例状态
     * @param phaseStatus 入队状态
     */
    public int updateJobStatusByJobIds(List<String> jobIds, Integer status,Integer phaseStatus) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(status);
        scheduleJob.setPhaseStatus(phaseStatus);
        return this.baseMapper.update(scheduleJob,Wrappers.lambdaQuery(ScheduleJob.class)
                .in(ScheduleJob::getJobId, jobIds));
    }

    /**
     * 查询容灾的时候的实例
     *
     * @param jobStartId 开始id
     * @param unSubmitStatus 实例状态
     * @param localAddress 节点
     * @param phaseStatus 入队状态
     * @return 简单的实例封装
     */
    public List<SimpleScheduleJobPO> listJobByStatusAddressAndPhaseStatus(long jobStartId, List<Integer> unSubmitStatus, String localAddress, Integer phaseStatus) {
        return this.baseMapper.listJobByStatusAddressAndPhaseStatus(jobStartId,unSubmitStatus,localAddress,phaseStatus);
    }

    /**
     * 更新实例 engineJobId和appId
     *
     * @param jobId 实例id
     * @param engineJobId 引擎id
     * @param appId 应用id
     */
    public void updateJobSubmitSuccess(String jobId, String engineJobId, String appId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setApplicationId(appId);
        scheduleJob.setEngineJobId(engineJobId);
        scheduleJob.setExecStartTime(Timestamp.valueOf(LocalDateTime.now()));
        scheduleJob.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        updateByJobId(scheduleJob);
    }

    /**
     * 更新状态
     * @param jobId 实例id
     * @param status 状态
     */
    public void updateStatus(String jobId, Integer status) {
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setJobId(jobId);
        updateScheduleJob.setStatus(status);
        this.baseMapper.update(updateScheduleJob, Wrappers.lambdaQuery(ScheduleJob.class)
                .eq(ScheduleJob::getJobId, jobId));
    }

    /**
     * 更新重试次数
     *
     * @param jobId 实例id
     * @param retryNum 重试次数
     */
    public void updateRetryNum(String jobId, Integer retryNum) {
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setJobId(jobId);
        updateScheduleJob.setRetryNum(retryNum);
        this.baseMapper.update(updateScheduleJob, Wrappers.lambdaQuery(ScheduleJob.class)
                .eq(ScheduleJob::getJobId, jobId));
    }


}
