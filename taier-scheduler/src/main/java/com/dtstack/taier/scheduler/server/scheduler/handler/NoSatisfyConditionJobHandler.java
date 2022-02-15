package com.dtstack.taier.scheduler.server.scheduler.handler;

import com.dtstack.taier.common.enums.JobCheckStatus;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 4:11 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class NoSatisfyConditionJobHandler implements JudgeNoPassJobHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoSatisfyConditionJobHandler.class);

    @Override
    public Boolean handlerJob(ScheduleJobDetails scheduleJobDetails, JobCheckStatus status) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean isSupportJobCheckStatus(JobCheckStatus status) {
        return JobCheckStatus.TIME_NOT_REACH.equals(status)
                || JobCheckStatus.NOT_UNSUBMIT.equals(status)
                || JobCheckStatus.FATHER_JOB_NOT_FINISHED.equals(status);
    }
}
