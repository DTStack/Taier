package com.dtstack.taier.scheduler.server.scheduler.interceptor;

import com.dtstack.taier.scheduler.server.ScheduleJobDetails;

/**
 * @Auther: dazhi
 * @Date: 2022/3/13 12:17 AM
 * @Email: dazhi@dtstack.com
 * @Description: 提交拦截适配器
 */
public class SubmitInterceptorAdapter implements SubmitInterceptor {

    @Override
    public Integer getSort() {
        return 0;
    }

    @Override
    public Boolean beforeSubmit(ScheduleJobDetails scheduleJobDetails) {
        return Boolean.TRUE;
    }

    @Override
    public void afterSubmit(ScheduleJobDetails scheduleJobDetails) {
    }
}
