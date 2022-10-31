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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * @Auther: dazhi
 * @Date: 2022/3/11 3:29 PM
 * @Email: dazhi@dtstack.com
 * @Description: 提交拦截器
 */
public interface SubmitInterceptor extends Sort {

    Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    /**
     * 提交任务前
     *
     * @param scheduleJobDetails 任务详情
     * @return 是否放行 true 放行， false 拦截
     */
    Boolean beforeSubmit(ScheduleJobDetails scheduleJobDetails);

    /**
     * 提交执行
     *
     * @param scheduleJobDetails 任务详情
     */
    void afterSubmit(ScheduleJobDetails scheduleJobDetails);
}
