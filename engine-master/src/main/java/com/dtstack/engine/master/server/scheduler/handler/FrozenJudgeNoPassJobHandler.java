package com.dtstack.engine.master.server.scheduler.handler;

import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.server.ScheduleJobDetails;
import com.dtstack.engine.master.service.ScheduleJobService;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 4:19 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FrozenJudgeNoPassJobHandler implements JudgeNoPassJobHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrozenJudgeNoPassJobHandler.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public Boolean handlerJob(ScheduleJobDetails scheduleJobDetails, JobCheckStatus status) {
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.KILLED.getStatus(), status.getMsg());
        return Boolean.FALSE;
    }

    @Override
    public Boolean isSupportJobCheckStatus(JobCheckStatus status) {
        return RdosTaskStatus.FROZEN.equals(status);
    }
}
