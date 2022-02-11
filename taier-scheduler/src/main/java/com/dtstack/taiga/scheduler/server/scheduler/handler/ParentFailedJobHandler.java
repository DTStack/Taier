package com.dtstack.taiga.scheduler.server.scheduler.handler;

import com.dtstack.taiga.common.enums.JobCheckStatus;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.pluginapi.enums.RdosTaskStatus;
import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;
import com.dtstack.taiga.scheduler.service.ScheduleJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 4:15 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ParentFailedJobHandler implements JudgeNoPassJobHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParentFailedJobHandler.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public Boolean handlerJob(ScheduleJobDetails scheduleJobDetails, JobCheckStatus status) {
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.PARENTFAILED.getStatus(), status.getMsg());
        return Boolean.FALSE;
    }

    @Override
    public Boolean isSupportJobCheckStatus(JobCheckStatus status) {
        return JobCheckStatus.FATHER_JOB_EXCEPTION.equals(status);
    }
}
