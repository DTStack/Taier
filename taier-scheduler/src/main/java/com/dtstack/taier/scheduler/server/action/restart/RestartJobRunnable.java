package com.dtstack.taier.scheduler.server.action.restart;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.enums.RestartType;
import com.dtstack.taier.scheduler.server.action.restart.impl.RestartCurrentAndDownStreamNodeRestartJob;
import com.dtstack.taier.scheduler.server.action.restart.impl.RestartCurrentNodeRestartJob;
import com.dtstack.taier.scheduler.server.action.restart.impl.SetSuccessAndResumeSchedulingRestartJob;
import com.dtstack.taier.scheduler.service.ScheduleTaskShadeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 11:25 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RestartJobRunnable extends AbstractRestart implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestartJobRunnable.class);

    private final List<String> jobIds;
    private final RestartType restartType;
    private final ScheduleTaskShadeService scheduleTaskShadeService;
    private final AbstractRestartJob abstractRestartJob;
    private final ApplicationContext applicationContext;

    public RestartJobRunnable(List<String> jobIds, RestartType restartType, EnvironmentContext environmentContext, ApplicationContext applicationContext) {
        super(environmentContext, applicationContext);
        this.jobIds = jobIds;
        this.restartType = restartType;
        this.applicationContext = applicationContext;
        this.abstractRestartJob = getAbstractRestartJob();
        this.scheduleTaskShadeService = applicationContext.getBean(ScheduleTaskShadeService.class);
    }

    @Override
    public void run() {
        try {
            LOGGER.info("reset start jobIds:{},restartType:{}", jobIds, restartType);
            List<ScheduleJob> jobList = scheduleJobService.lambdaQuery()
                    .in(ScheduleJob::getJobId, jobIds)
                    .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();

            if (CollectionUtils.isEmpty(jobList)) {
                LOGGER.error("cat not find job by id:{} ", jobIds.toString());
                return;
            }
            //工作流补充所有子任务
            List<String> workFlowJobIds = jobList.stream()
                    .filter(job -> EScheduleJobType.WORK_FLOW.getType().equals(job.getTaskType()))
                    .map(ScheduleJob::getJobId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(workFlowJobIds)) {
                List<ScheduleJob> workFlowSubJobs = scheduleJobService.getWorkFlowSubJobs(workFlowJobIds);
                jobList.addAll(workFlowSubJobs);
            }

            Set<Long> taskIds = jobList.stream().map(ScheduleJob::getTaskId).collect(Collectors.toSet());
            List<ScheduleTaskShade> taskShadeList = scheduleTaskShadeService.lambdaQuery()
                    .in(ScheduleTaskShade::getTaskId, taskIds)
                    .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
            Map<Long, ScheduleTaskShade> taskShadeMap = taskShadeList.stream().collect((Collectors.toMap(ScheduleTaskShade::getTaskId, g -> (g))));

            Map<String, String> resumeBatchJobs = Maps.newHashMap();
            // 查询出能重跑的任务
            jobList = findCanResetJob(jobList, taskShadeMap, resumeBatchJobs);

            if (CollectionUtils.isNotEmpty(jobList)) {
                Map<String, String> computeResumeBatchJobs = abstractRestartJob.computeResumeBatchJobs(jobList);
                if (MapUtils.isNotEmpty(computeResumeBatchJobs)) {
                    resumeBatchJobs.putAll(computeResumeBatchJobs);
                }
                scheduleJobService.restartScheduleJob(resumeBatchJobs);
            }
        } catch (Exception e) {
            LOGGER.error("restart job {} error", jobIds, e);
        }
    }

    /**
     * @param jobs
     * @param taskShadeMap
     * @param resumeBatchJobs
     * @return
     */
    private List<ScheduleJob> findCanResetJob(List<ScheduleJob> jobs, Map<Long, ScheduleTaskShade> taskShadeMap, Map<String, String> resumeBatchJobs) {
        Map<String, ScheduleJob> canResetList = new HashMap<>();
        for (ScheduleJob job : jobs) {
            Integer jobStatus = job.getStatus();

            if (!TaskStatus.canReset(jobStatus)) {
                LOGGER.error("job {} status {}  can not restart ", job.getJobId(), job.getStatus());
                continue;
            }

            ScheduleTaskShade scheduleTaskShade = taskShadeMap.get(job.getTaskId());
            if (scheduleTaskShade == null || Deleted.DELETED.getStatus().equals(scheduleTaskShade.getIsDeleted())) {
                LOGGER.error("job {} cat not find taskShade by taskId:{}", job.getJobId(), job.getTaskId());
                continue;
            }

            canResetList.putIfAbsent(job.getJobId(), job);

            // 判断这个任务是否是工作流子任务，如果是需要带上工作流任务
            if (!StringUtils.equals("0", job.getFlowJobId())) {
                ScheduleJob flowJob = scheduleJobService.lambdaQuery()
                        .eq(ScheduleJob::getJobId, job.getFlowJobId())
                        .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                        .one();
                if (flowJob != null) {
                    resumeBatchJobs.put(flowJob.getJobId(), flowJob.getCycTime());
                }
            }
        }

        return new ArrayList<>(canResetList.values());
    }

    public AbstractRestartJob getAbstractRestartJob() {
        switch (restartType) {
            case SET_SUCCESSFULLY_AND_RESUME_SCHEDULING:
                return new SetSuccessAndResumeSchedulingRestartJob(environmentContext, applicationContext);
            case RESTART_CURRENT_NODE:
                return new RestartCurrentNodeRestartJob(environmentContext, applicationContext);
            case RESTART_CURRENT_AND_DOWNSTREAM_NODE:
                return new RestartCurrentAndDownStreamNodeRestartJob(environmentContext, applicationContext);
            default:
                return null;
        }
    }
}
