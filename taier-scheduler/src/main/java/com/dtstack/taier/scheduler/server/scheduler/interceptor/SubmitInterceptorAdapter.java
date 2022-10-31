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
