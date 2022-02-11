package com.dtstack.taiga.scheduler.server.scheduler.handler;

import com.dtstack.taiga.common.enums.JobCheckStatus;
import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 1:22 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface JudgeNoPassJobHandler {

    /**
     * 处理未通过校验的实例
     *
     * @param scheduleJobDetails 实例
     * @param status 状态
     * @return 处理完后是否通过
     */
    Boolean handlerJob(ScheduleJobDetails scheduleJobDetails, JobCheckStatus status);

    /**
     * 是否支持处理的状态
     *
     * @param status 状态
     * @return 是否支持
     */
    Boolean isSupportJobCheckStatus(JobCheckStatus status);
}
