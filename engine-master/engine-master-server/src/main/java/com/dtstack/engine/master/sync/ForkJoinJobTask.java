package com.dtstack.engine.master.sync;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.utils.JobKeyUtils;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-02-01
 * 查询出当前任务的所有下游任务（同一调度日期内）
 */
public class ForkJoinJobTask extends RecursiveTask<List<ScheduleJob>> {

    private final static Logger logger = LoggerFactory.getLogger(ForkJoinJobTask.class);
    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal(), EScheduleJobType.ALGORITHM_LAB.getVal());
    private String jobId;
    private CopyOnWriteArrayList<ScheduleJob> results;
    private ScheduleJobDao scheduleJobDao;
    private ScheduleJobJobDao scheduleJobJobDao;
    private boolean isOnlyNextChild;

    public ForkJoinJobTask(String jobId, CopyOnWriteArrayList<ScheduleJob> results,
                           ScheduleJobDao scheduleJobDao, ScheduleJobJobDao scheduleJobJobDao, boolean isOnlyNextChild) {
        this.jobId = jobId;
        this.results = results;
        this.scheduleJobDao = scheduleJobDao;
        this.scheduleJobJobDao = scheduleJobJobDao;
        this.isOnlyNextChild = isOnlyNextChild;
    }

    @Override
    protected List<ScheduleJob> compute() {
        ScheduleJob job = scheduleJobDao.getRdosJobByJobId(jobId);
        if (null == job) {
            return null;
        }

        String jobKey = job.getJobKey();
        //从jobKey获取父任务的触发时间
        String parentJobDayStr = JobKeyUtils.getJobTriggerTimeFromJobKey(jobKey);
        if (Strings.isNullOrEmpty(parentJobDayStr)) {
            return Lists.newArrayList();
        }

        //查询子工作任务
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(jobKey);
        if (CollectionUtils.isEmpty(scheduleJobJobs)) {
            return null;
        }

        List<ScheduleJob> subJobsAndStatusByFlowId = null;
        //如果工作流 和 实验任务 把子节点全部添加进来
        if (SPECIAL_TASK_TYPES.contains(job.getTaskType())) {
            subJobsAndStatusByFlowId = scheduleJobDao.getSubJobsAndStatusByFlowId(job.getJobId());
        }

        List<String> jobKeyList = this.filterJobKeyList(job, scheduleJobJobs, parentJobDayStr, subJobsAndStatusByFlowId);

        if (CollectionUtils.isEmpty(jobKeyList)) {
            return null;
        }
        List<ScheduleJob> listJobs = scheduleJobDao.listJobByJobKeys(jobKeyList);
        List<ForkJoinJobTask> tasks = new ArrayList<>();
        for (ScheduleJob childScheduleJob : listJobs) {
            results.addIfAbsent(childScheduleJob);
            if (isOnlyNextChild) {
                continue;
            }
            ForkJoinJobTask subTask = new ForkJoinJobTask(childScheduleJob.getJobId(), results, scheduleJobDao, scheduleJobJobDao, isOnlyNextChild);
            logger.info("forkJoinJobTask subTask jobId {} result {} isOnlyNextChild {} ", childScheduleJob.getJobId(), results.size(), isOnlyNextChild);
            tasks.add(subTask);
        }

        Collection<ForkJoinJobTask> forkJoinJobTasks = ForkJoinTask.invokeAll(tasks);
        for (ForkJoinJobTask forkJoinJobTask : forkJoinJobTasks) {
            List<ScheduleJob> scheduleJobs = forkJoinJobTask.join();
            if (CollectionUtils.isNotEmpty(scheduleJobs)) {
                results.addAllAbsent(scheduleJobs);
            }
        }
        return results;
    }


    private List<String> filterJobKeyList(ScheduleJob scheduleJob, List<ScheduleJobJob> scheduleJobJobList, String parentJobDayStr, List<ScheduleJob> subJobsAndStatusByFlowId) {
        Long jobTaskShadeId = JobKeyUtils.getTaskShadeIdFromJobKey(scheduleJob.getJobKey());
        List<String> jobKeyList = new ArrayList<>();
        if (null == jobTaskShadeId) {
            return jobKeyList;
        }

        if (CollectionUtils.isNotEmpty(subJobsAndStatusByFlowId)) {
            List<String> flowJobKeys = subJobsAndStatusByFlowId.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());
            jobKeyList.addAll(flowJobKeys);
        }

        for (ScheduleJobJob scheduleJobJob : scheduleJobJobList) {
            String childJobKey = scheduleJobJob.getJobKey();
            Long childJobShadeId = JobKeyUtils.getTaskShadeIdFromJobKey(childJobKey);
            //排除自依赖
            if (null != childJobShadeId && childJobShadeId.equals(jobTaskShadeId)) {
                continue;
            }
            String childJobDayStr = JobKeyUtils.getJobTriggerTimeFromJobKey(childJobKey);
            //排除不是同一天执行的
            if (!parentJobDayStr.equals(childJobDayStr)) {
                continue;
            }
            //添加除工作流内部子任务之外的下游任务依赖
            if (CollectionUtils.isNotEmpty(subJobsAndStatusByFlowId) && subJobsAndStatusByFlowId.stream().anyMatch(s -> s.getJobKey().equalsIgnoreCase(childJobKey))) {
                continue;
            }
            jobKeyList.add(scheduleJobJob.getJobKey());
        }
        return jobKeyList;
    }
}
