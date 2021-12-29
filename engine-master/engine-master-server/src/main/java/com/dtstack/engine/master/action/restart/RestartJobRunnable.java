package com.dtstack.engine.master.action.restart;

import com.dtstack.engine.common.enums.Deleted;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.mapper.ScheduleJobDao;
import com.dtstack.engine.mapper.ScheduleJobJobDao;
import com.dtstack.engine.mapper.ScheduleTaskShadeDao;
import com.dtstack.engine.master.action.restart.impl.RestartCurrentAndDownStreamNodeRestartJob;
import com.dtstack.engine.master.action.restart.impl.RestartCurrentNodeRestartJob;
import com.dtstack.engine.master.action.restart.impl.SetSuccessAndResumeSchedulingRestartJob;
import com.dtstack.engine.master.enums.RestartType;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.service.ScheduleTaskService;
import com.dtstack.engine.master.service.ScheduleTaskTaskService;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

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
    private final ScheduleTaskService scheduleTaskService;
    private final AbstractRestartJob abstractRestartJob;
    private final ApplicationContext applicationContext;

    public RestartJobRunnable(List<String> jobIds, RestartType restartType, EnvironmentContext environmentContext, ApplicationContext applicationContext) {
        super(environmentContext,applicationContext);
        this.jobIds = jobIds;
        this.restartType = restartType;
        this.applicationContext = applicationContext;
        this.abstractRestartJob = getAbstractRestartJob();
        this.scheduleTaskService = applicationContext.getBean(ScheduleTaskService.class);
    }

    @Override
    public void run() {
        try {
            LOGGER.info("reset start jobIds:{},restartType:{}",jobIds,restartType);
            List<ScheduleJob> jobList = scheduleJobService.lambdaQuery()
                    .in(ScheduleJob::getJobId,jobIds)
                    .eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                    .list();

            if (CollectionUtils.isEmpty(jobList)) {
                LOGGER.error("cat not find job by id:{} ", jobIds.toString());
                return;
            }

            List<Long> taskIds = jobList.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());
            List<ScheduleTaskShade> taskShadeList = scheduleTaskService.lambdaQuery()
                    .in(ScheduleTaskShade::getTaskId, taskIds)
                    .eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                    .list();
            Map<Long, ScheduleTaskShade> taskShadeMap = taskShadeList.stream().collect((Collectors.toMap(ScheduleTaskShade::getTaskId, g->(g))));

            Map<String, String> resumeBatchJobs = Maps.newHashMap();
            // 查询出能重跑的任务
            jobList = findCanResetJob(jobList, taskShadeMap,resumeBatchJobs);

            if (CollectionUtils.isNotEmpty(jobList)) {
                Map<String, String> computeResumeBatchJobs = abstractRestartJob.computeResumeBatchJobs(jobList);
                if(MapUtils.isNotEmpty(computeResumeBatchJobs)){
                    resumeBatchJobs.putAll(computeResumeBatchJobs);
                }
                scheduleJobService.batchRestartScheduleJob(resumeBatchJobs);
            }
        } catch (Exception e) {
            LOGGER.error("restart job {} error", jobIds, e);
        }
    }

    /**
     *
     *
     * @param jobs
     * @param taskShadeMap
     * @param resumeBatchJobs
     * @return
     */
    private List<ScheduleJob> findCanResetJob(List<ScheduleJob> jobs, Map<Long, ScheduleTaskShade> taskShadeMap, Map<String, String> resumeBatchJobs) {
        List<ScheduleJob> canResetList = Lists.newArrayList();
        for (ScheduleJob job : jobs) {
            Integer jobStatus = job.getStatus();

            if (!RdosTaskStatus.canReset(jobStatus)) {
                LOGGER.error("job {} status {}  can not restart ", job.getJobId(), job.getStatus());
                continue;
            }

            ScheduleTaskShade scheduleTaskShade = taskShadeMap.get(job.getTaskId());
            if (scheduleTaskShade == null || Deleted.DELETED.getStatus().equals(scheduleTaskShade.getIsDeleted())) {
                LOGGER.error("cat not find taskShade by taskId:{}", job.getTaskId());
                continue;
            }

            canResetList.add(job);

            // 判断这个任务是否是工作流子任务，如果是需要带上工作流任务
            if (!StringUtils.equals("0", job.getFlowJobId())) {
                ScheduleJob flowJob = scheduleJobService.lambdaQuery()
                        .eq(ScheduleJob::getFlowJobId,job.getFlowJobId())
                        .eq(ScheduleJob::getIsDeleted,IsDeletedEnum.NOT_DELETE.getType())
                        .one();
                if (flowJob != null) {
                    resumeBatchJobs.put(flowJob.getJobId(),flowJob.getCycTime());
                }
            }
        }
        return canResetList;
    }

    public AbstractRestartJob getAbstractRestartJob(){
        switch (restartType){
            case SET_SUCCESSFULLY_AND_RESUME_SCHEDULING:
                return new SetSuccessAndResumeSchedulingRestartJob(environmentContext,applicationContext);
            case RESTART_CURRENT_NODE:
                return new RestartCurrentNodeRestartJob(environmentContext,applicationContext);
            case RESTART_CURRENT_AND_DOWNSTREAM_NODE:
                return new RestartCurrentAndDownStreamNodeRestartJob(environmentContext,applicationContext);
            default: return null;
        }
    }
}
