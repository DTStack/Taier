package com.dtstack.taiga.scheduler.server.action.restart.impl;

import com.dtstack.taiga.common.enums.Deleted;
import com.dtstack.taiga.common.env.EnvironmentContext;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.scheduler.server.action.restart.AbstractRestartJob;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.ApplicationContext;

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

    public SetSuccessAndResumeSchedulingRestartJob(EnvironmentContext environmentContext, ApplicationContext applicationContext) {
        super(environmentContext, applicationContext);
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
                ScheduleJob workFlowJob = scheduleJobService.lambdaQuery()
                        .eq(ScheduleJob::getJobId,job.getFlowJobId())
                        .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                        .one();
                if (null != workFlowJob) {
                    resumeBatchJobs.put(workFlowJob.getJobId(), workFlowJob.getCycTime());
                }
            }
        }
        return resumeBatchJobs;
    }
}
