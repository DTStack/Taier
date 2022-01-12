package com.dtstack.engine.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.engine.common.enums.ForceCancelFlag;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleJobExpand;
import com.dtstack.engine.domain.ScheduleJobJob;
import com.dtstack.engine.domain.ScheduleJobOperatorRecord;
import com.dtstack.engine.mapper.ScheduleJobMapper;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.mapstruct.ScheduleJobMapStruct;
import com.dtstack.engine.master.server.builder.ScheduleJobDetails;
import com.dtstack.engine.master.server.scheduler.JobPartitioner;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
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

import java.util.*;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:14 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service("job")
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

    /**
     * 统计周期实例数 条件 计划时间=cycTime,实例名称=jobName
     *
     * @param cycTime 计划时间
     * @param jobName 实例名称
     * @param type 实例类型
     */
    public int countByCycTimeAndJobName(String cycTime, String jobName, Integer type) {
        return this.baseMapper.countByCycTimeAndJobName(cycTime,jobName,type);
    }

    /**
     * 获得实例数
     *
     * @param startId 开始id
     * @param cycTime 计划时间
     * @param jobName 实例名称
     * @param type 实例类型
     * @param jobSize 获取实例数
     * @return 实例集合
     */
    public List<ScheduleJob> listByCycTimeAndJobName(Long startId, String cycTime, String jobName, Integer type, Integer jobSize) {
        return this.baseMapper.listByCycTimeAndJobName(startId,cycTime,jobName,type,jobSize);
    }

    public Integer updateJobStatusAndExecTime(String jobId, Integer status) {
        if (StringUtils.isNotBlank(jobId) && status != null) {
            return this.baseMapper.updateJobStatusAndExecTime(jobId, status);
        }
        return 0;
    }


}
