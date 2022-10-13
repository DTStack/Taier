package com.dtstack.taier.scheduler.server.action.restart;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.service.ScheduleJobJobService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午5:40
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractRestart {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractRestart.class);

    protected final ScheduleJobService scheduleJobService;

    protected final ScheduleJobJobService scheduleJobJobService;

    protected final EnvironmentContext environmentContext;

    protected final ApplicationContext applicationContext;


    public AbstractRestart(EnvironmentContext environmentContext, ApplicationContext applicationContext) {
        this.environmentContext = environmentContext;
        this.applicationContext = applicationContext;
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobJobService = applicationContext.getBean(ScheduleJobJobService.class);
    }

    /**
     * 获得工作流任务
     *
     * @param batchJob
     * @param resumeBatchJobs
     */
    protected void setSubFlowJob(ScheduleJob batchJob, Map<String, String> resumeBatchJobs) {
        List<String> subFlowJob = getSubFlowJob(batchJob);
        if (CollectionUtils.isNotEmpty(subFlowJob)) {
            List<ScheduleJob> jobList = scheduleJobService.lambdaQuery()
                    .in(ScheduleJob::getFlowJobId, subFlowJob)
                    .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
            if (CollectionUtils.isNotEmpty(jobList)) {
                resumeBatchJobs.putAll(jobList.stream().collect(Collectors.toMap(ScheduleJob::getJobId, ScheduleJob::getCycTime)));
            }
        }
    }

    /**
     * 查询出当前任务的子节点
     *
     * @param batchJob
     * @return
     */
    protected List<String> getSubFlowJob(ScheduleJob batchJob) {
        List<String> subJobIds = new ArrayList<>();
        if (EScheduleJobType.WORK_FLOW.getType().equals(batchJob.getTaskType())) {
            //如果任务为工作流类型 需要补充自己的子节点
            List<ScheduleJob> flowJobList = scheduleJobService.lambdaQuery()
                    .eq(ScheduleJob::getFlowJobId, batchJob.getJobId())
                    .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
            if (CollectionUtils.isNotEmpty(flowJobList)) {
                subJobIds.addAll(flowJobList.stream()
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
    protected Map<String, String> getAllChildJobWithSameDayByForkJoin(String jobId, boolean isOnlyNextChild) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();
        ForkJoinJobTask forkJoinJobTask = new ForkJoinJobTask(jobId, results, scheduleJobService, scheduleJobJobService, isOnlyNextChild);
        ForkJoinTask<Map<String, String>> submit = forkJoinPool.submit(forkJoinJobTask);
        try {
            return submit.get(environmentContext.getForkJoinResultTimeOut(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("get all child job {} error ", jobId, e);
        }
        return null;
    }

    /**
     * 置成功
     *
     * @param job
     * @param jobMap
     */
    protected void setSuccess(ScheduleJob job, Map<String, String> jobMap) {
        List<String> jobIds = getSubFlowJob(job);
        // 设置强规则任务
        jobIds.add(job.getJobId());

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(TaskStatus.MANUALSUCCESS.getStatus());
        scheduleJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        scheduleJobService.lambdaUpdate().in(ScheduleJob::getJobId, jobIds)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .update(scheduleJob);

        LOGGER.info("jobIds {} manual success", jobIds);
        // 置成功并恢复调度,要把当前置成功任务去除掉
        if (MapUtils.isNotEmpty(jobMap)) {
            jobIds.forEach(jobMap::remove);
        }
    }
}
