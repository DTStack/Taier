package com.dtstack.engine.master.action.restart;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.mapper.ScheduleJobDao;
import com.dtstack.engine.mapper.ScheduleJobJobDao;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午5:40
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractRestart {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractRestart.class);

    protected final ScheduleJobDao scheduleJobDao;

    protected final ScheduleJobJobDao scheduleJobJobDao;

    protected final EnvironmentContext environmentContext;

    protected final ScheduleJobService scheduleJobService;


    public AbstractRestart(ScheduleJobDao scheduleJobDao, EnvironmentContext environmentContext, ScheduleJobJobDao scheduleJobJobDao, ScheduleJobService scheduleJobService) {
        this.scheduleJobDao = scheduleJobDao;
        this.environmentContext = environmentContext;
        this.scheduleJobJobDao = scheduleJobJobDao;
        this.scheduleJobService = scheduleJobService;
    }

    /**
     * 获得工作流任务
     *
     * @param batchJob
     * @param resumeBatchJobs
     */
    protected void setSubFlowJob(ScheduleJob batchJob, Map<String,String> resumeBatchJobs) {
        List<String> subFlowJob = getSubFlowJob(batchJob);
        if (CollectionUtils.isNotEmpty(subFlowJob)) {
            List<ScheduleJob> jobs = scheduleJobDao.getRdosJobByJobIds(subFlowJob);
            if(CollectionUtils.isNotEmpty(jobs)){
                resumeBatchJobs.putAll(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId,ScheduleJob::getCycTime)));
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
    protected Map<String,String> getAllChildJobWithSameDayByForkJoin(String jobId, boolean isOnlyNextChild) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String,String> results = new ConcurrentHashMap<>();
        ForkJoinJobTask forkJoinJobTask = new ForkJoinJobTask(jobId, results, scheduleJobDao, scheduleJobJobDao, isOnlyNextChild);
        ForkJoinTask<Map<String,String>> submit = forkJoinPool.submit(forkJoinJobTask);
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
        scheduleJobDao.updateJobStatusByIds(RdosTaskStatus.MANUALSUCCESS.getStatus(), jobIds);
        LOGGER.info("ids  {} manual success", jobIds);

        // 置成功并恢复调度,要把当前置成功任务去除掉
        jobIds.forEach(jobMap::remove);
    }
}
