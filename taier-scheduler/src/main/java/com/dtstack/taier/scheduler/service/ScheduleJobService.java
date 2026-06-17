/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.scheduler.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleJobDistributeType;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.ForceCancelFlag;
import com.dtstack.taier.common.enums.OperatorType;
import com.dtstack.taier.common.enums.Restarted;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.GenerateErrorMsgUtil;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.po.SimpleScheduleJobPO;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.dtstack.taier.scheduler.dto.scheduler.SimpleScheduleJobDTO;
import com.dtstack.taier.scheduler.enums.JobPhaseStatus;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.mapstruct.ScheduleJobMapStruct;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.server.distribute.ScheduleJobDistributeContext;
import com.dtstack.taier.scheduler.server.distribute.ScheduleJobDistributeStrategy;
import com.dtstack.taier.scheduler.server.pipeline.operator.UnnecessaryPreprocessJobPipeline;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    private ScheduleActionService actionService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;

    @Autowired
    private ScheduleTaskShadeInfoService scheduleTaskShadeInfoService;

    @Autowired
    private UnnecessaryPreprocessJobPipeline unnecessaryPreprocessJobPipeline;

    @Autowired
    private List<ScheduleJobDistributeStrategy> distributeStrategies;

    /**
     * 开始运行实例
     *
     * @param scheduleJobDetails 实例
     */
    public void startJob(ScheduleJobDetails scheduleJobDetails) throws Exception {
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        ScheduleTaskShade scheduleTaskShade = scheduleJobDetails.getScheduleTaskShade();

        // 解析任务运行信息
        JSONObject extraInfo = scheduleTaskShadeInfoService.getInfoJSON(scheduleJob.getTaskId());
        if (null == extraInfo) {
            //额外信息为空 标记任务为失败
            this.updateStatusAndLogInfoById(scheduleJob.getJobId(), TaskStatus.FAILED.getStatus(), "task run extra info is empty");
            LOGGER.error(" job  {} run fail with info is null", scheduleJob.getJobId());
        }
        ParamActionExt paramActionExt = actionService.paramActionExt(scheduleTaskShade, scheduleJob, extraInfo);
        if (paramActionExt != null && !unnecessaryPreprocessJobPipeline.isMatch(scheduleJobDetails.getScheduleJob().getTaskType())) {
            updateStatusByJobIdAndVersionId(scheduleJob.getJobId(), TaskStatus.SUBMITTING.getStatus(), scheduleTaskShade.getVersionId());
            actionService.start(paramActionExt);
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
                        if (null != v && StringUtils.isNotBlank(v.getKey())) {
                            restartJobId.add(v.getKey());
                        }
                    });

            List<List<String>> partition = Lists.partition(restartJobId, environmentContext.getBatchInsertSize());
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
                scheduleJob.setStatus(TaskStatus.UNSUBMIT.getStatus());
                scheduleJob.setPhaseStatus(JobPhaseStatus.CREATE.getCode());
                scheduleJob.setIsRestart(Restarted.RESTARTED.getStatus());
                scheduleJob.setNodeAddress(environmentContext.getLocalAddress());
                scheduleJob.setRetryNum(0);

                // 更新状态
                this.lambdaUpdate()
                        .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                        .in(ScheduleJob::getJobId, jobIds)
                        .update(scheduleJob);

                // 清除日志
                scheduleJobExpandService.clearData(jobIds);
                scheduleJobOperatorRecordService.insertBatch(records);
                LOGGER.info("reset job {}", jobIds);
            }
        }
    }

    /**
     * 批量插入周期实例 jobSize 在负载均衡时 区分 scheduleType（正常调度 和 补数据）
     *
     * @param jobBuilderBeanCollection 实例集合
     * @param scheduleType             调度类型 正常调度 和 补数据
     */
    @Transactional(rollbackFor = Exception.class)
    public Long insertJobList(Collection<ScheduleJobDetails> jobBuilderBeanCollection, Integer scheduleType) {
        return insertJobList(jobBuilderBeanCollection, scheduleType, EScheduleJobDistributeType.DEFAULT, new ScheduleJobDistributeContext());
    }

    @Transactional(rollbackFor = Exception.class)
    public Long insertJobList(Collection<ScheduleJobDetails> jobBuilderBeanCollection,
                              Integer scheduleType,
                              EScheduleJobDistributeType distributeType,
                              ScheduleJobDistributeContext distributeContext) {
        if (CollectionUtils.isEmpty(jobBuilderBeanCollection)) {
            return null;
        }

        List<ScheduleJobDetails> batchJobs = Lists.newArrayList(jobBuilderBeanCollection);
        ScheduleJobDistributeStrategy distributeStrategy = getDistributeStrategy(distributeType);
        ScheduleJobDistributeContext context = distributeContext == null ? new ScheduleJobDistributeContext() : distributeContext;
        Map<ScheduleJobDetails, String> jobNodeMap = distributeStrategy.distribute(batchJobs, scheduleType, context);

        int count = 0;
        int jobBatchSize = environmentContext.getBatchInsertSize();
        int jobJobBatchSize = environmentContext.getBatchJobJobInsertSize();
        Long minJobId = null;
        List<ScheduleJob> jobWaitForSave = Lists.newArrayList();
        List<ScheduleJobJob> jobJobWaitForSave = Lists.newArrayList();

        for (ScheduleJobDetails scheduleJobDetails : batchJobs) {
            count++;
            ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
            String nodeAddress = jobNodeMap.get(scheduleJobDetails);
            if (StringUtils.isBlank(nodeAddress)) {
                throw new TaierDefineException(String.format("No node address assigned for job: %s", scheduleJob.getJobId()));
            }
            scheduleJob.setNodeAddress(nodeAddress);

            jobWaitForSave.add(scheduleJob);
            jobJobWaitForSave.addAll(scheduleJobDetails.getJobJobList());

            LOGGER.debug("insertJobList count:{} batchJobs:{} nodeAddress:{} strategy:{}",
                    count, batchJobs.size(), nodeAddress, distributeStrategy.distributeType());
            if (count % jobBatchSize == 0 || count == batchJobs.size() || jobJobWaitForSave.size() > jobJobBatchSize) {
                minJobId = persistJobs(jobWaitForSave, jobJobWaitForSave, minJobId, jobJobBatchSize);
                LOGGER.info("insertJobList count:{} batchJobs:{} jobJobSize:{} strategy:{}",
                        count, batchJobs.size(), jobJobWaitForSave.size(), distributeStrategy.distributeType());
            }
        }
        if (CollectionUtils.isNotEmpty(jobWaitForSave) || CollectionUtils.isNotEmpty(jobJobWaitForSave)) {
            minJobId = persistJobs(jobWaitForSave, jobJobWaitForSave, minJobId, jobJobBatchSize);
        }
        return minJobId;
    }

    private ScheduleJobDistributeStrategy getDistributeStrategy(EScheduleJobDistributeType distributeType) {
        EScheduleJobDistributeType targetType = distributeType == null ? EScheduleJobDistributeType.DEFAULT : distributeType;
        for (ScheduleJobDistributeStrategy distributeStrategy : distributeStrategies) {
            if (distributeStrategy.distributeType() == targetType) {
                return distributeStrategy;
            }
        }
        for (ScheduleJobDistributeStrategy distributeStrategy : distributeStrategies) {
            if (distributeStrategy.distributeType() == EScheduleJobDistributeType.DEFAULT) {
                LOGGER.warn("Distribute strategy {} not found, fallback to DEFAULT", targetType);
                return distributeStrategy;
            }
        }
        throw new TaierDefineException(String.format("Distribute strategy %s not found", targetType));
    }

    /**
     * 插入实例
     */
    private Long persistJobs(List<ScheduleJob> jobWaitForSave, List<ScheduleJobJob> jobJobWaitForSave, Long minJobId, Integer jobJobSize) {
        try {
            return RetryUtil.executeWithRetry(() -> {
                Long curMinJobId = minJobId;
                if (jobWaitForSave.size() > 0) {
                    this.saveBatch(jobWaitForSave);
                    if (Objects.isNull(minJobId)) {
                        curMinJobId = jobWaitForSave.stream().map(ScheduleJob::getId).min(Long::compareTo).orElse(null);
                    }

                    // 插入扩展数据
                    List<ScheduleJobExpand> scheduleJobExpandList = ScheduleJobMapStruct.INSTANCE.scheduleJobTOScheduleJobExpand(jobWaitForSave);
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
            throw new TaierDefineException(e);
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
     *
     * @param jobId     实例id
     * @param status    状态
     * @param versionId 版本
     */
    private Boolean updateStatusByJobIdAndVersionId(String jobId, Integer status, Integer versionId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(status);
        scheduleJob.setVersionId(versionId);
        return this.lambdaUpdate()
                .eq(ScheduleJob::getJobId, jobId)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
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
        String errorLog = GenerateErrorMsgUtil.generateErrorMsg(logInfo);
        scheduleJobExpand.setLogInfo(errorLog);
        scheduleJobExpandService.lambdaUpdate()
                .eq(ScheduleJobExpand::getJobId, jobId)
                .eq(ScheduleJobExpand::getIsDeleted, Deleted.NORMAL.getStatus())
                .update(scheduleJobExpand);
    }

    /**
     * 更新实例状态
     *
     * @param jobId  实例 id
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
     *
     * @param jobId 实例id
     * @return 实例状态，如果查询不到，返回null
     */
    public Integer getJobStatusByJobId(String jobId) {
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
     * 更新实例队列状态，队列状态字段JobPhaseStatus，用于控制周期实例扫描时实例进队出队
     *
     * @param id       实例id
     * @param original 实例当前队列状态
     * @param update   实例需要变更的队列状态
     * @return 是否更新成功
     */
    public boolean updatePhaseStatusById(Long id, JobPhaseStatus original, JobPhaseStatus update) {
        if (id == null || original == null || update == null) {
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
     * @param startSort      开始id
     * @param nodeAddress    节点
     * @param type           类型
     * @param isEq           是否查询出第一个
     * @param jobPhaseStatus 队列状态
     * @return 周期实例列表
     */
    public List<ScheduleJob> listCycleJob(Long startSort, String nodeAddress, Integer type, Boolean isEq, Integer jobPhaseStatus) {
        if (startSort == null) {
            return Lists.newArrayList();
        }

        return this.baseMapper.listCycleJob(startSort, nodeAddress, type, isEq, jobPhaseStatus);
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
     *
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
     *
     * @param jobId     实例id
     * @param engineLog 引擎日志
     * @param logInfo   提交日志
     * @return 更新记录数
     */
    public Boolean updateExpandByJobId(String jobId, String engineLog, String logInfo) {
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
     *
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
     *
     * @param jobId            实例id
     * @param status           状态
     * @param generateErrorMsg 失败内容
     */
    @Transactional(rollbackFor = Exception.class)
    public void jobFail(String jobId, Integer status, String generateErrorMsg) {
        updateStatus(jobId, status);
        updateExpandByJobId(jobId, null, generateErrorMsg);
    }

    /**
     * 更新实例状态
     *
     * @param jobIds      实例id
     * @param status      实例状态
     * @param phaseStatus 入队状态
     */
    public int updateJobStatusByJobIds(List<String> jobIds, Integer status, Integer phaseStatus) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(status);
        scheduleJob.setPhaseStatus(phaseStatus);
        return this.baseMapper.update(scheduleJob, Wrappers.lambdaQuery(ScheduleJob.class)
                .in(ScheduleJob::getJobId, jobIds));
    }

    /**
     * 查询容灾的时候的实例
     *
     * @param jobStartId     开始id
     * @param unSubmitStatus 实例状态
     * @param localAddress   节点
     * @param phaseStatus    入队状态
     * @return 简单的实例封装
     */
    public List<SimpleScheduleJobPO> listJobByStatusAddressAndPhaseStatus(Long jobStartId, List<Integer> unSubmitStatus, String localAddress, Integer phaseStatus) {
        return this.baseMapper.listJobByStatusAddressAndPhaseStatus(jobStartId, unSubmitStatus, localAddress, phaseStatus);
    }

    /**
     * 更新实例 engineJobId和appId
     *
     * @param jobId       实例id
     * @param engineJobId 引擎id
     * @param appId       应用id
     */
    public void updateJobSubmitSuccess(String jobId, String engineJobId, String appId) {
        ScheduleJob scheduleJob = getByJobId(jobId);
        LambdaUpdateWrapper<ScheduleJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ScheduleJob::getApplicationId, appId);
        updateWrapper.set(ScheduleJob::getEngineJobId, engineJobId);
        updateWrapper.set(ScheduleJob::getExecStartTime, Timestamp.valueOf(LocalDateTime.now()));
        updateWrapper.set(ScheduleJob::getExecEndTime, null);
        updateWrapper.set(ScheduleJob::getGmtModified, Timestamp.valueOf(LocalDateTime.now()));
        updateWrapper.eq(ScheduleJob::getJobId, jobId);
        this.baseMapper.update(scheduleJob, updateWrapper);
    }

    /**
     * 更新状态
     *
     * @param jobId  实例id
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
     * @param jobId    实例id
     * @param retryNum 重试次数
     */
    public void updateRetryNum(String jobId, Integer retryNum) {
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setJobId(jobId);
        updateScheduleJob.setRetryNum(retryNum);
        this.baseMapper.update(updateScheduleJob, Wrappers.lambdaQuery(ScheduleJob.class)
                .eq(ScheduleJob::getJobId, jobId));
    }


    public void clearInterruptJob(Long startExecuteOrder) {
        this.baseMapper.delete(Wrappers.lambdaQuery(ScheduleJob.class).ge(ScheduleJob::getJobExecuteOrder, startExecuteOrder)
                .eq(ScheduleJob::getType, EScheduleType.NORMAL_SCHEDULE.getType())
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus()));
    }

    /**
     * 扫描实例，用于容灾
     *
     * @param startId            开始id
     * @param unfinishedStatuses 需求查询的状态
     * @param nodeAddress        地址
     * @return 包含部分字段的job集合
     */
    public List<SimpleScheduleJobDTO> listSimpleJobByStatusAddress(Long startId, List<Integer> unfinishedStatuses, String nodeAddress) {
        if (startId < 0 || StringUtils.isBlank(nodeAddress)) {
            return Lists.newArrayList();
        }
        List<ScheduleJob> simpleScheduleJobPOS = this.baseMapper.listSimpleJobByStatusAddress(startId, unfinishedStatuses, nodeAddress);
        return ScheduleJobMapStruct.INSTANCE.scheduleJobTOSimpleScheduleJobDTO(simpleScheduleJobPOS);
    }

    public void updateStatusWithExecTime(ScheduleJob job) {
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setExecStartTime(job.getExecStartTime());
        updateScheduleJob.setExecEndTime(job.getExecEndTime());
        updateScheduleJob.setExecTime(job.getExecTime());
        updateScheduleJob.setStatus(job.getStatus());
        this.baseMapper.update(updateScheduleJob, Wrappers.lambdaQuery(ScheduleJob.class)
                .eq(ScheduleJob::getJobId, job.getJobId()));
    }

    /**
     * 批量查询实例
     *
     * @param jobIds jobId
     * @return 实例
     */
    public List<ScheduleJob> getWorkFlowSubJobs(List<String> jobIds) {
        return this.baseMapper
                .selectList(Wrappers.lambdaQuery(ScheduleJob.class)
                        .in(ScheduleJob::getFlowJobId, jobIds)
                        .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus()));
    }
}
