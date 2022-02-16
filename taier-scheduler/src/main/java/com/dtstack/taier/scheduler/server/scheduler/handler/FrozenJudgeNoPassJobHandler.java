package com.dtstack.taier.scheduler.server.scheduler.handler;

import com.dtstack.taier.common.enums.JobCheckStatus;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 4:19 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class FrozenJudgeNoPassJobHandler implements JudgeNoPassJobHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrozenJudgeNoPassJobHandler.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public Boolean handlerJob(ScheduleJobDetails scheduleJobDetails, JobCheckStatus status) {
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), TaskStatus.KILLED.getStatus(), status.getMsg());
        return Boolean.FALSE;
    }

    @Override
    public Boolean isSupportJobCheckStatus(JobCheckStatus status) {
        return TaskStatus.FROZEN.equals(status);
    }
}
