package com.dtstack.taier.scheduler.server.scheduler.interceptor;

import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.server.Sort;

/**
 * @Auther: dazhi
 * @Date: 2022/3/11 3:29 PM
 * @Email: dazhi@dtstack.com
 * @Description: 提交拦截器
 */
public interface SubmitInterceptor extends Sort {

    /**
     * 提交任务前
     * @param scheduleJobDetails 任务详情
     * @return 是否放行 true 放行， false 拦截
     */
    Boolean beforeSubmit(ScheduleJobDetails scheduleJobDetails);

    /**
     * 提交执行
     * @param scheduleJobDetails 任务详情
     */
    void afterSubmit(ScheduleJobDetails scheduleJobDetails);
}
