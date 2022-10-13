package com.dtstack.taier.scheduler.server.scheduler.interceptor;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleStatus;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.JobCheckStatus;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/3/13 12:15 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class TaskStatusSubmitInterceptor extends SubmitInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusSubmitInterceptor.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public Integer getSort() {
        return 0;
    }

    @Override
    public Boolean beforeSubmit(ScheduleJobDetails scheduleJobDetails) {
        ScheduleTaskShade scheduleTaskShade = scheduleJobDetails.getScheduleTaskShade();
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();

        // 任务已经删除
        if (scheduleTaskShade == null || Deleted.DELETED.getStatus().equals(scheduleTaskShade.getIsDeleted())) {
            scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), TaskStatus.FAILED.getStatus(), JobCheckStatus.TASK_DELETE.getMsg());
            LOGGER.info("jobId:{} task deleted ,update job status:{}", scheduleJob.getJobId(), TaskStatus.FAILED.getStatus());
            return Boolean.FALSE;
        }

        // 任务停止: 不管什么情况任务都是冻结状态
        if (EScheduleStatus.STOP.getVal().equals(scheduleTaskShade.getScheduleStatus())) {
            scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), TaskStatus.FROZEN.getStatus(),
                    String.format(JobCheckStatus.TASK_STATUS_STOP.getMsg(), scheduleTaskShade.getName(),
                            EScheduleStatus.getStatus(scheduleTaskShade.getScheduleStatus())));
            LOGGER.info("jobId:{} task:{} stop , update job status:{}", scheduleJob.getJobId(), scheduleTaskShade.getTaskId(), TaskStatus.FROZEN.getStatus());
            return Boolean.FALSE;
        }

        // 任务冻结:冻结任务只对正常的周期实例有效，补数据还是可以正常运行
        if (EScheduleStatus.FREEZE.getVal().equals(scheduleTaskShade.getScheduleStatus())
                && EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleJob.getType())) {
            scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), TaskStatus.FROZEN.getStatus(),
                    String.format(JobCheckStatus.TASK_STATUS_STOP.getMsg(), scheduleTaskShade.getName(),
                            EScheduleStatus.getStatus(scheduleTaskShade.getScheduleStatus())));
            LOGGER.info("jobId:{} task:{} freeze , update job status:{}", scheduleJob.getJobId(), scheduleTaskShade.getTaskId(), TaskStatus.FROZEN.getStatus());
            return Boolean.FALSE;
        }

        return super.beforeSubmit(scheduleJobDetails);
    }
}
