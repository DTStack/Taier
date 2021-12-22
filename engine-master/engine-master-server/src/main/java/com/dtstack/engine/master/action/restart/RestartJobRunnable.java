package com.dtstack.engine.master.action.restart;

import com.dtstack.engine.common.enums.Deleted;
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
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final ScheduleTaskShadeDao scheduleTaskShadeDao;
    private final AbstractRestartJob abstractRestartJob;

    public RestartJobRunnable(List<String> jobIds, RestartType restartType, ScheduleJobDao scheduleJobDao, ScheduleTaskShadeDao scheduleTaskShadeDao, ScheduleJobJobDao scheduleJobJobDao, EnvironmentContext environmentContext, ScheduleJobService scheduleJobService) {
        super(scheduleJobDao,environmentContext,scheduleJobJobDao,scheduleJobService);
        this.jobIds = jobIds;
        this.restartType = restartType;
        this.abstractRestartJob = getAbstractRestartJob();
        this.scheduleTaskShadeDao = scheduleTaskShadeDao;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("reset start jobIds:{},restartType:{}",jobIds,restartType);
            List<ScheduleJob> jobs = scheduleJobDao.getRdosJobByJobIds(jobIds);

            if (CollectionUtils.isEmpty(jobs)) {
                LOGGER.error("cat not find job by id:{} ", jobIds.toString());
                return;
            }

            Map<String, String> resumeBatchJobs = Maps.newHashMap();

            Map<Integer, Set<Long>> appTaskListMapping = Maps.newHashMap();
            Map<Long, ScheduleTaskShade> taskShadeMap = Maps.newHashMap();

            for (Integer appType : appTaskListMapping.keySet()) {
                Set<Long> taskIds = appTaskListMapping.get(appType);
                List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.listSimpleByTaskIds(taskIds, null, appType);
                for (ScheduleTaskShade scheduleTaskShade : scheduleTaskShades) {
                    taskShadeMap.put(scheduleTaskShade.getTaskId(),scheduleTaskShade);
                }
            }


            // 查询出能重跑的任务
            jobs = findCanResetJob(jobs, taskShadeMap,resumeBatchJobs);

            if (CollectionUtils.isNotEmpty(jobs)) {
                Map<String, String> computeResumeBatchJobs = abstractRestartJob.computeResumeBatchJobs(jobs);
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
    private List<ScheduleJob> findCanResetJob(List<ScheduleJob> jobs, Map<Long,ScheduleTaskShade> taskShadeMap, Map<String, String> resumeBatchJobs) {
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
                ScheduleJob flowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
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
                return new SetSuccessAndResumeSchedulingRestartJob(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
            case RESTART_CURRENT_NODE:
                return new RestartCurrentNodeRestartJob(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
            case RESTART_CURRENT_AND_DOWNSTREAM_NODE:
                return new RestartCurrentAndDownStreamNodeRestartJob(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
            default: return null;
        }
    }
}
