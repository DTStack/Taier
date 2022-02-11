package com.dtstack.taiga.scheduler.server.scheduler.exec;

import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;

/**
 * @Auther: dazhi
 * @Date: 2022/1/10 7:58 PM
 * @Email: dazhi@dtstack.com
 * @Description: 实例运行条件抽象
 */
public interface JudgeJobExecOperator {

    /**
     * 实例运行条件接口
     *
     * @param scheduleJobDetails 实例详情
     * @return 是否通过
     */
    JobCheckRunInfo isExec(ScheduleJobDetails scheduleJobDetails);

}
