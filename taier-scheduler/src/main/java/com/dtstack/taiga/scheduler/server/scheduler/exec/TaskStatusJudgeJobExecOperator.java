package com.dtstack.taiga.scheduler.server.scheduler.exec;

import com.dtstack.taiga.common.enums.EScheduleStatus;
import com.dtstack.taiga.common.enums.EScheduleType;
import com.dtstack.taiga.common.enums.Deleted;
import com.dtstack.taiga.common.enums.JobCheckStatus;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/1/10 8:01 PM
 * @Email: dazhi@dtstack.com
 * @Description: 判断任务状态
 */
@Component
public class TaskStatusJudgeJobExecOperator implements JudgeJobExecOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusJudgeJobExecOperator.class);

    @Override
    public JobCheckRunInfo isExec(ScheduleJobDetails scheduleJobDetails) {
        ScheduleTaskShade scheduleTaskShade = scheduleJobDetails.getScheduleTaskShade();
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        JobCheckRunInfo checkRunInfo = new JobCheckRunInfo();

        // 任务已经删除
        if (scheduleTaskShade == null || Deleted.DELETED.getStatus().equals(scheduleTaskShade.getIsDeleted())) {
            checkRunInfo.setPass(Boolean.FALSE);
            checkRunInfo.setStatus(JobCheckStatus.TASK_DELETE);
            checkRunInfo.setLogInfo(JobCheckStatus.TASK_DELETE.getMsg());
            return checkRunInfo;
        }

        // 任务停止: 不管什么情况任务都是冻结状态
        if (EScheduleStatus.STOP.getVal().equals(scheduleTaskShade.getScheduleStatus())) {
            checkRunInfo.setPass(Boolean.FALSE);
            checkRunInfo.setStatus(JobCheckStatus.TASK_STATUS_STOP);
            checkRunInfo.setLogInfo(String.format(JobCheckStatus.TASK_STATUS_STOP.getMsg(),scheduleTaskShade.getName(),scheduleTaskShade.getScheduleStatus()));
            return checkRunInfo;
        }

        // 任务冻结:冻结任务只对正常的周期实例有效，补数据还是可以正常运行
        if (EScheduleStatus.FREEZE.getVal().equals(scheduleTaskShade.getScheduleStatus())
                && EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleJob.getType())) {
            checkRunInfo.setPass(Boolean.FALSE);
            checkRunInfo.setStatus(JobCheckStatus.TASK_STATUS_STOP);
            checkRunInfo.setLogInfo(String.format(JobCheckStatus.TASK_STATUS_STOP.getMsg(),scheduleTaskShade.getName(),scheduleTaskShade.getScheduleStatus()));
            return checkRunInfo;
        }

        checkRunInfo.setPass(Boolean.TRUE);
        return checkRunInfo;
    }
}
