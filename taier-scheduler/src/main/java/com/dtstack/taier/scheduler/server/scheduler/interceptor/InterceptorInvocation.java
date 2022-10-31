/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public Boolean submit(ScheduleJobDetails scheduleJobDetails) {
        if (iterator == null || !iterator.hasNext()) {
            return scheduler.submitJob(scheduleJobDetails);
        } else {
            SubmitInterceptor next = iterator.next();
            return intercept(next, scheduleJobDetails);
        }
    }

    public Boolean intercept(SubmitInterceptor submitInterceptor, ScheduleJobDetails scheduleJobDetails) {
        // 返回true 说明拦截通过
        if (!submitInterceptor.beforeSubmit(scheduleJobDetails)) {
            return Boolean.FALSE;
        }

        if (!submit(scheduleJobDetails)) {
            return Boolean.FALSE;
        }

        submitInterceptor.afterSubmit(scheduleJobDetails);
        return Boolean.TRUE;
    }

}
