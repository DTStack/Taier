package com.dtstack.engine.master.server.scheduler;

import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.server.ScheduleJobDetails;

/**
 * @Auther: dazhi
 * @Date: 2022/1/10 2:40 PM
 * @Email: dazhi@dtstack.com
 * @Description: 调度器
 */
public interface Scheduler {

    /**
     * 获得调度类型
     * @return 调度类型
     */
    EScheduleType getScheduleType();

    /**
     * 提交实例
     *
     * @param scheduleJobDetails 实例详情
     * @return 是否提交成功
     */
    Boolean submitJob(ScheduleJobDetails scheduleJobDetails);
}
