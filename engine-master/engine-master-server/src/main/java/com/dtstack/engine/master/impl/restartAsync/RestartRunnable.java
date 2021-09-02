package com.dtstack.engine.master.impl.restartAsync;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobOperatorRecord;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.dtstack.schedule.common.enums.Restarted;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RestartRunnable implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(RestartRunnable.class);

    private Long id;
    private boolean justRunChild;
    private boolean setSuccess;
    private List<Long> subJobIds;
    private ScheduleJobDao scheduleJobDao;
    private ScheduleTaskShadeDao scheduleTaskShadeDao;
    private ScheduleJobJobDao scheduleJobJobDao;
    private ScheduleJobService scheduleJobService;
    private EnvironmentContext environmentContext;
    private String redisKey;
    private StringRedisTemplate redisTemplate;
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    public RestartRunnable(Long id, Boolean justRunChild, Boolean setSuccess, List<Long> subJobIds,
                           ScheduleJobDao scheduleJobDao, ScheduleTaskShadeDao scheduleTaskShadeDao,
                           ScheduleJobJobDao scheduleJobJobDao, EnvironmentContext environmentContext,
                           String redisKey, StringRedisTemplate redisTemplate,ScheduleJobService scheduleJobService,
                           ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao) {
        this.id = id;
        this.justRunChild = BooleanUtils.toBoolean(justRunChild);
        this.setSuccess = BooleanUtils.toBoolean(setSuccess);
        this.subJobIds = subJobIds;
        this.scheduleJobDao = scheduleJobDao;
        this.scheduleTaskShadeDao = scheduleTaskShadeDao;
        this.environmentContext = environmentContext;
        this.scheduleJobJobDao = scheduleJobJobDao;
        this.redisKey = redisKey;
        this.redisTemplate =  redisTemplate;
        this.scheduleJobService = scheduleJobService;
        this.scheduleJobOperatorRecordDao = scheduleJobOperatorRecordDao;
    }

    @Override
    public void run() {
        try {
            ScheduleJob batchJob = scheduleJobDao.getOne(id);
            if (batchJob == null) {
                logger.error("cat not find job by id:{} ", id);
                return;
            }

            ScheduleTaskShade task = scheduleTaskShadeDao.getOne(batchJob.getTaskId(), batchJob.getAppType());
            if (task == null || Deleted.DELETED.getStatus().equals(task.getIsDeleted())) {
                logger.error("cat not find taskShade by taskId:{} appType {}", batchJob.getTaskId(), batchJob.getAppType());
                return;
            }

            Integer jobStatus = batchJob.getStatus();
            if (!RdosTaskStatus.canReset(jobStatus)) {
                logger.error("job {} status {}  can not restart ", batchJob.getJobId(), batchJob.getStatus());
                return;
            }
            Map<String,String> resumeBatchJobs = new HashMap<>();
            //置成功并恢复调度
            if (setSuccess && justRunChild) {
                List<String> jobIds = getSubFlowJob(batchJob);
                // 设置强规则任务
                List<String> ruleJobs = getRuleTask(batchJob);
                jobIds.add(batchJob.getJobId());
                jobIds.addAll(ruleJobs);
                scheduleJobDao.updateJobStatusByIds(RdosTaskStatus.MANUALSUCCESS.getStatus(), jobIds);
                logger.info("ids  {} manual success", jobIds);
                return;
            }

            //重跑并恢复调度
            if (!justRunChild) {
                resumeBatchJobs.put(batchJob.getJobId(),batchJob.getCycTime());
            }

            //重跑工作流中的子任务时，加入工作流任务，用于更新状态
            if (!StringUtils.equals("0", batchJob.getFlowJobId())) {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(batchJob.getFlowJobId(), Deleted.NORMAL.getStatus());
                if (flowJob != null) {
                    resumeBatchJobs.put(flowJob.getJobId(),flowJob.getCycTime());
                }
            }

            // 子任务不为空 重跑当前任务和自身
            if (CollectionUtils.isNotEmpty(subJobIds)) {
                List<ScheduleJob> jobs = scheduleJobDao.listByJobIds(subJobIds);
                resumeBatchJobs.putAll(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId,ScheduleJob::getCycTime)));

                // 判断该节点是否被强弱规则任务所依赖
                List<ScheduleJob> taskRuleSonJob = scheduleJobService.getTaskRuleSonJob(batchJob);
                if (CollectionUtils.isNotEmpty(taskRuleSonJob)) {
                    resumeBatchJobs.putAll(taskRuleSonJob.stream().collect(Collectors.toMap(ScheduleJob::getJobId,ScheduleJob::getCycTime)));

                    // 判断所依赖的任务是否是工作流任务
                    for (ScheduleJob scheduleJob : taskRuleSonJob) {
                        setSubFlowJob(scheduleJob, resumeBatchJobs);
                    }
                }

                // 如果是工作流根节点 添加子节点
                setSubFlowJob(batchJob, resumeBatchJobs);

            } else {
                Map<String,String> allChildJobWithSameDayByForkJoin = getAllChildJobWithSameDayByForkJoin(batchJob.getJobId(), false);
                if (MapUtils.isNotEmpty(allChildJobWithSameDayByForkJoin)) {
                    resumeBatchJobs.putAll(allChildJobWithSameDayByForkJoin);
                }
            }

            batchRestartScheduleJob(resumeBatchJobs);
        } catch (Exception e) {
            logger.error("restart job {} error", id, e);
        } finally {
            redisTemplate.delete(redisKey);
            logger.info("release job {} redis key {} ", id, redisKey);
        }
    }

    private List<String> getRuleTask(ScheduleJob batchJob) {
        List<String> ruleJobs = Lists.newArrayList();
        List<ScheduleJob> taskRuleSonJob = scheduleJobService.getTaskRuleSonJob(batchJob);

        for (ScheduleJob scheduleJob : taskRuleSonJob) {
            List<String> subFlowJob = getSubFlowJob(scheduleJob);
            ruleJobs.add(scheduleJob.getJobId());
            ruleJobs.addAll(subFlowJob);
        }

        return ruleJobs;
    }

    private void setSubFlowJob(ScheduleJob batchJob, Map<String,String> resumeBatchJobs) {
        List<String> subFlowJob = getSubFlowJob(batchJob);
        if (CollectionUtils.isNotEmpty(subFlowJob)) {
            List<ScheduleJob> jobs = scheduleJobDao.getRdosJobByJobIds(subFlowJob);
            if(CollectionUtils.isNotEmpty(jobs)){
                resumeBatchJobs.putAll(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId,ScheduleJob::getCycTime)));
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchRestartScheduleJob(Map<String,String> resumeBatchJobs) {
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
            List<List<String>> partition = Lists.partition(restartJobId, 20);
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
                scheduleJobDao.updateJobStatusAndPhaseStatus(Lists.newArrayList(jobIds), RdosTaskStatus.UNSUBMIT.getStatus(), JobPhaseStatus.CREATE.getCode(), Restarted.RESTARTED.getStatus(),environmentContext.getLocalAddress());
                scheduleJobOperatorRecordDao.insertBatch(records);
                logger.info("reset job {}", jobIds);
            }
        }
    }

    /**
     * 查询出当前任务的子节点
     *
     * @param batchJob
     * @return
     */
    private List<String> getSubFlowJob(ScheduleJob batchJob) {
        List<String> subJobIds = new ArrayList<>();
        if (EScheduleJobType.WORK_FLOW.getType().equals(batchJob.getTaskType()) || EScheduleJobType.ALGORITHM_LAB.getType().equals(batchJob.getTaskType())) {
            //如果任务为工作流类型 需要补充自己的子节点
            List<ScheduleJob> subJobsByFlowIds = scheduleJobDao.getSubJobsByFlowIds(Collections.singletonList(batchJob.getJobId()));
            if (CollectionUtils.isNotEmpty(subJobsByFlowIds)) {
                subJobIds.addAll(subJobsByFlowIds.stream()
                        .map(ScheduleJob::getJobId)
                        .collect(Collectors.toSet()));
            }
        }
        return subJobIds;
    }


    /**
     * 递归查找当前jobId下的子任务
     *
     * @param jobId
     * @param isOnlyNextChild
     * @return
     */
    private Map<String,String> getAllChildJobWithSameDayByForkJoin(String jobId, boolean isOnlyNextChild) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String,String> results = new ConcurrentHashMap<>();
        ForkJoinJobTask forkJoinJobTask = new ForkJoinJobTask(jobId, results, scheduleJobDao, scheduleJobJobDao, isOnlyNextChild);
        ForkJoinTask<Map<String,String>> submit = forkJoinPool.submit(forkJoinJobTask);
        try {
            return submit.get(environmentContext.getForkJoinResultTimeOut(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("get all child job {} error ", jobId, e);
        }
        return null;
    }
}