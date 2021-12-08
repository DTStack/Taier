package com.dtstack.engine.master.action.restart.impl;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.mapper.ScheduleJobDao;
import com.dtstack.engine.mapper.ScheduleJobJobDao;
import com.dtstack.engine.master.action.restart.AbstractRestartJob;
import com.dtstack.engine.master.impl.ScheduleJobService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午7:29
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RestartCurrentNodeRestartJob extends AbstractRestartJob {


    public RestartCurrentNodeRestartJob(ScheduleJobDao scheduleJobDao, EnvironmentContext environmentContext, ScheduleJobJobDao scheduleJobJobDao, ScheduleJobService scheduleJobService) {
        super(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
    }

    @Override
    public Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs) {
        Map<String, String> resumeBatchJobs = new HashMap<>(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId, ScheduleJob::getCycTime)));
        // 判断该节点是否被强弱规则任务所依赖
        for (ScheduleJob job : jobs) {
            setSubFlowJob(job, resumeBatchJobs);
        }
        return resumeBatchJobs;
    }
}
