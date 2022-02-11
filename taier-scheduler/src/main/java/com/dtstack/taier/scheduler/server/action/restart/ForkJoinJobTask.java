package com.dtstack.taier.scheduler.server.action.restart;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.scheduler.service.ScheduleJobJobService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.utils.JobKeyUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-02-01
 * 查询出当前任务的所有下游任务（同一调度日期内）
 */
public class ForkJoinJobTask extends RecursiveTask<Map<String,String>> {

    private final static Logger logger = LoggerFactory.getLogger(ForkJoinJobTask.class);
    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal());
    private final String jobId;
    private final ConcurrentHashMap<String,String> results;
    private final ScheduleJobService scheduleJobService;
    private final ScheduleJobJobService scheduleJobJobService;
    private final boolean isOnlyNextChild;

    public ForkJoinJobTask(String jobId, ConcurrentHashMap<String,String> results,
                           ScheduleJobService scheduleJobService, ScheduleJobJobService scheduleJobJobService, boolean isOnlyNextChild) {
        this.jobId = jobId;
        this.results = results;
        this.scheduleJobService = scheduleJobService;
        this.scheduleJobJobService = scheduleJobJobService;
        this.isOnlyNextChild = isOnlyNextChild;
    }

    @Override
    protected ConcurrentHashMap<String,String> compute() {
        ScheduleJob scheduleJob = scheduleJobService.lambdaQuery()
                .eq(ScheduleJob::getJobId, jobId)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .one();
        if (null == scheduleJob) {
            return null;
        }

        String jobKey = scheduleJob.getJobKey();
        //从jobKey获取父任务的触发时间
        String parentJobDayStr = JobKeyUtils.getJobTriggerTimeFromJobKey(jobKey);
        if (Strings.isNullOrEmpty(parentJobDayStr)) {
            return null;
        }

        //查询子工作任务
        List<ScheduleJobJob> scheduleJobJobList = scheduleJobJobService.lambdaQuery().eq(ScheduleJobJob::getParentJobKey, jobKey)
                .eq(ScheduleJobJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .list();
        if (CollectionUtils.isEmpty(scheduleJobJobList)) {
            return null;
        }

        List<ScheduleJob> flowJobList = null;
        //如果工作流 和 实验任务 把子节点全部添加进来
        if (SPECIAL_TASK_TYPES.contains(scheduleJob.getTaskType())) {
            flowJobList = scheduleJobService.lambdaQuery()
                    .eq(ScheduleJob::getFlowJobId, scheduleJob.getJobId())
                    .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
        }

        Set<String> jobKeyList = this.filterJobKeyList(scheduleJob, scheduleJobJobList, parentJobDayStr, flowJobList);

        if (CollectionUtils.isEmpty(jobKeyList)) {
            return null;
        }

        List<ScheduleJob> childJobList = scheduleJobService.lambdaQuery()
                .eq(ScheduleJob::getJobKey, jobKeyList)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .list();

        List<ForkJoinJobTask> tasks = new ArrayList<>();
        for (ScheduleJob childScheduleJob : childJobList) {
            if (results.containsKey(childScheduleJob.getJobId())) {
                continue;
            }
            results.put(childScheduleJob.getJobId(),childScheduleJob.getCycTime());
            if (isOnlyNextChild) {
                continue;
            }
            ForkJoinJobTask subTask = new ForkJoinJobTask(childScheduleJob.getJobId(), results, scheduleJobService, scheduleJobJobService, isOnlyNextChild);
            logger.info("forkJoinJobTask subTask jobId {} result {} isOnlyNextChild {} ", childScheduleJob.getJobId(), results.size(), isOnlyNextChild);
            tasks.add(subTask);
        }

        Collection<ForkJoinJobTask> forkJoinJobTasks = ForkJoinTask.invokeAll(tasks);
        for (ForkJoinJobTask forkJoinJobTask : forkJoinJobTasks) {
            Map<String,String> scheduleJobs = forkJoinJobTask.join();
            if (MapUtils.isNotEmpty(scheduleJobs)) {
                results.putAll(scheduleJobs);
            }
        }
        return results;
    }


    private Set<String> filterJobKeyList(ScheduleJob scheduleJob, List<ScheduleJobJob> scheduleJobJobList, String parentJobDayStr, List<ScheduleJob> subJobsAndStatusByFlowId) {
        Long jobTaskShadeId = JobKeyUtils.getTaskShadeIdFromJobKey(scheduleJob.getJobKey());
        Set<String> jobKeyList = new HashSet<>();
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
