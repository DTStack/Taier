package com.dtstack.engine.master.sync;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.Restarted;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    private EnvironmentContext environmentContext;
    private String redisKey;
    private StringRedisTemplate redisTemplate;

    public RestartRunnable(Long id, Boolean justRunChild, Boolean setSuccess, List<Long> subJobIds,
                           ScheduleJobDao scheduleJobDao, ScheduleTaskShadeDao scheduleTaskShadeDao,
                           ScheduleJobJobDao scheduleJobJobDao, EnvironmentContext environmentContext,
                           String redisKey, StringRedisTemplate redisTemplate) {
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

            List<ScheduleJob> resumeBatchJobs = Lists.newArrayList();
            //置成功并恢复调度
            if (setSuccess && justRunChild) {
                List<String> jobIds = getSubFlowJob(batchJob);
                jobIds.add(batchJob.getJobId());
                scheduleJobDao.updateJobStatusByIds(RdosTaskStatus.MANUALSUCCESS.getStatus(), jobIds);
                logger.info("ids  {} manual success", jobIds);
                return;
            }

            //重跑并恢复调度
            if (!justRunChild) {
                resumeBatchJobs.add(batchJob);
            }

            //重跑工作流中的子任务时，加入工作流任务，用于更新状态
            if (!StringUtils.equals("0", batchJob.getFlowJobId())) {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(batchJob.getFlowJobId(), Deleted.NORMAL.getStatus());
                if (flowJob != null) {
                    resumeBatchJobs.add(flowJob);
                }
            }

            // 子任务不为空 重跑当前任务和自身
            if (CollectionUtils.isNotEmpty(subJobIds)) {
                resumeBatchJobs.addAll(scheduleJobDao.listByJobIds(subJobIds));
                //如果是工作流根节点 添加子节点
                List<String> subFlowJob = getSubFlowJob(batchJob);
                if (CollectionUtils.isNotEmpty(subFlowJob)) {
                    resumeBatchJobs.addAll(scheduleJobDao.getRdosJobByJobIds(subFlowJob));
                }
            } else {
                List<ScheduleJob> allChildJobWithSameDayByForkJoin = getAllChildJobWithSameDayByForkJoin(batchJob.getJobId(), false);
                if (CollectionUtils.isNotEmpty(allChildJobWithSameDayByForkJoin)) {
                    resumeBatchJobs.addAll(allChildJobWithSameDayByForkJoin);
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

    private void batchRestartScheduleJob(List<ScheduleJob> resumeBatchJobs) {
        if (CollectionUtils.isNotEmpty(resumeBatchJobs)) {
            resumeBatchJobs = resumeBatchJobs.stream()
                    .sorted(Comparator.nullsFirst(Comparator.comparing(ScheduleJob::getCycTime,Comparator.nullsFirst(String::compareTo))))
                    .collect(Collectors.toList());
        }
        List<List<ScheduleJob>> partition = Lists.partition(resumeBatchJobs, 20);
        for (List<ScheduleJob> scheduleJobs : partition) {
            List<String> jobIds = scheduleJobs.stream()
                    .map(ScheduleJob::getJobId)
                    .collect(Collectors.toList());
            //更新任务为重跑任务--等待调度器获取并执行
            scheduleJobDao.updateJobStatusAndPhaseStatus(jobIds, RdosTaskStatus.UNSUBMIT.getStatus(), JobPhaseStatus.CREATE.getCode(), Restarted.RESTARTED.getStatus());
            logger.info("reset job {}", jobIds);
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
    private List<ScheduleJob> getAllChildJobWithSameDayByForkJoin(String jobId, boolean isOnlyNextChild) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        CopyOnWriteArrayList<ScheduleJob> results = new CopyOnWriteArrayList<>();
        ForkJoinJobTask forkJoinJobTask = new ForkJoinJobTask(jobId, results, scheduleJobDao, scheduleJobJobDao, isOnlyNextChild);
        ForkJoinTask<List<ScheduleJob>> submit = forkJoinPool.submit(forkJoinJobTask);
        try {
            return submit.get(environmentContext.getForkJoinResultTimeOut(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("get all child job {} error ", jobId, e);
        }
        return null;
    }
}