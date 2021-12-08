package com.dtstack.engine.master.action.restart.impl;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.mapper.ScheduleJobDao;
import com.dtstack.engine.mapper.ScheduleJobJobDao;
import com.dtstack.engine.master.action.restart.AbstractRestartJob;
import com.dtstack.engine.master.impl.ScheduleJobService;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午7:23
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class SetSuccessAndResumeSchedulingRestartJob extends AbstractRestartJob {

    public SetSuccessAndResumeSchedulingRestartJob(ScheduleJobDao scheduleJobDao, EnvironmentContext environmentContext, ScheduleJobJobDao scheduleJobJobDao, ScheduleJobService scheduleJobService) {
        super(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
    }

    @Override
    public Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs) {
        Map<String, String> resumeBatchJobs = new HashMap<>();
        for (ScheduleJob job : jobs) {
            Map<String, String> allChildJobWithSameDayByForkJoin = getAllChildJobWithSameDayByForkJoin(job.getJobId(), false);
            setSuccess(job, allChildJobWithSameDayByForkJoin);
            if (MapUtils.isNotEmpty(allChildJobWithSameDayByForkJoin)) {
                resumeBatchJobs.putAll(allChildJobWithSameDayByForkJoin);
            }
            if (!"0".equalsIgnoreCase(job.getFlowJobId())) {
                ScheduleJob workFlowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), null);
                if (null != workFlowJob) {
                    resumeBatchJobs.put(workFlowJob.getJobId(), workFlowJob.getCycTime());
                }
            }
        }
        return resumeBatchJobs;
    }
}
