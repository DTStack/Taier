package com.dtstack.taier.scheduler.server.action.restart.impl;

import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.scheduler.server.action.restart.AbstractRestartJob;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午7:33
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RestartCurrentAndDownStreamNodeRestartJob extends AbstractRestartJob {


    public RestartCurrentAndDownStreamNodeRestartJob(EnvironmentContext environmentContext, ApplicationContext applicationContext) {
        super( environmentContext,applicationContext);
    }

    @Override
    public Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs) {
        Map<String, String> resumeBatchJobs = new HashMap<>(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId, ScheduleJob::getCycTime)));

        for (ScheduleJob job : jobs) {
            Map<String, String> allChildJobWithSameDayByForkJoin = getAllChildJobWithSameDayByForkJoin(job.getJobId(), false);
            if (MapUtils.isNotEmpty(allChildJobWithSameDayByForkJoin)) {
                resumeBatchJobs.putAll(allChildJobWithSameDayByForkJoin);
            }
        }

        return resumeBatchJobs;
    }
}
