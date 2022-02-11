package com.dtstack.taier.scheduler.server.scheduler;

import com.dtstack.taier.scheduler.server.ScheduleJobDetails;

/**
 * @Auther: dazhi
 * @Date: 2022/1/10 2:40 PM
 * @Email: dazhi@dtstack.com
 * @Description: 调度器
 */
public interface Scheduler {


    /**
     * 提交实例
     *
     * @param scheduleJobDetails 实例详情
     * @return 是否提交成功
     */
    Boolean submitJob(ScheduleJobDetails scheduleJobDetails);


    /**
     * 获取调度名称
     * @return
     */
    String getSchedulerName();
}
