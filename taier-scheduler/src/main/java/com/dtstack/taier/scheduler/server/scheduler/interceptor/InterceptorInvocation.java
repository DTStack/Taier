package com.dtstack.taier.scheduler.server.scheduler.interceptor;

import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.server.Sort;
import com.dtstack.taier.scheduler.server.scheduler.Scheduler;
import org.apache.commons.collections.CollectionUtils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/3/11 4:29 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class InterceptorInvocation {

    /**
     * 调度器
     */
    private final Scheduler scheduler;

    /**
     * 提交拦截器
     */
    private Iterator<SubmitInterceptor> iterator;

    public InterceptorInvocation(Scheduler scheduler, List<SubmitInterceptor> interceptorList) {
        // 调度器
        this.scheduler = scheduler;

        // 对拦截器进行排序
        if (CollectionUtils.isNotEmpty(interceptorList)) {
            interceptorList.sort(Comparator.comparingInt(Sort::getSort));
            iterator = interceptorList.iterator();
        }

    }

    public Boolean invoke(ScheduleJobDetails scheduleJobDetails) {
        if (iterator == null || !iterator.hasNext()) {
            scheduler.submitJob(scheduleJobDetails);
            return Boolean.TRUE;
        } else {
            SubmitInterceptor next = iterator.next();
            return intercept(next, scheduleJobDetails);
        }
    }

    public Boolean intercept(SubmitInterceptor submitInterceptor, ScheduleJobDetails scheduleJobDetailss) {
        // 返回true 说明拦截通过
        if (!submitInterceptor.beforeSubmit(scheduleJobDetailss)) {
            return Boolean.FALSE;
        }

        if (!invoke(scheduleJobDetailss)) {
            return Boolean.FALSE;
        }

        submitInterceptor.afterSubmit(scheduleJobDetailss);
        return Boolean.TRUE;
    }

}
