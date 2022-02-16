package com.dtstack.taier.scheduler.server.scheduler.exec;

import com.dtstack.taier.common.enums.JobCheckStatus;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/1/12 7:15 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobStatusJudgeJobExecOperator implements JudgeJobExecOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobStatusJudgeJobExecOperator.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public JobCheckRunInfo isExec(ScheduleJobDetails scheduleJobDetails) {
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        JobCheckRunInfo checkRunInfo = new JobCheckRunInfo();
        Integer status = scheduleJobService.getJobStatusByJobId(scheduleJob.getJobId());

        // 判断实例状态是不是等待提交
        if (!TaskStatus.UNSUBMIT.getStatus().equals(status)) {
            checkRunInfo.setPass(Boolean.FALSE);
            checkRunInfo.setStatus(JobCheckStatus.NOT_UNSUBMIT);
            checkRunInfo.setLogInfo(JobCheckStatus.NOT_UNSUBMIT.getMsg());
            return checkRunInfo;
        }

        // 判断实例是否到达运行时间
        long cycTime = Long.parseLong(scheduleJob.getCycTime());
        long currTime = Long.parseLong(new DateTime().toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));

        if (currTime < cycTime) {
            checkRunInfo.setPass(Boolean.FALSE);
            checkRunInfo.setStatus(JobCheckStatus.TIME_NOT_REACH);
            checkRunInfo.setLogInfo(JobCheckStatus.TIME_NOT_REACH.getMsg());
            return checkRunInfo;
        }

        checkRunInfo.setPass(Boolean.TRUE);
        return checkRunInfo;
    }
}
