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

package com.dtstack.taier.scheduler.server.scheduler;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.OperatorType;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.scheduler.enums.JobPhaseStatus;
import com.dtstack.taier.scheduler.server.scheduler.interceptor.SubmitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 4:22 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class RestartJobScheduler extends OperatorRecordJobScheduler {

    @Autowired(required = false)
    private List<SubmitInterceptor> submitInterceptorList;

    @Override
    protected Long getMinSort() {
        return 0L;
    }

    @Override
    protected List<SubmitInterceptor> getInterceptor() {
        return submitInterceptorList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.RESTART;
    }

    @Override
    protected List<ScheduleJob> getScheduleJob(Set<String> jobIds) {
        return scheduleJobService.lambdaQuery().in(ScheduleJob::getJobId, jobIds)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .apply("(status = 0 or ((status = 10 or status=4) and task_type in (10)))")
                .eq(ScheduleJob::getPhaseStatus, JobPhaseStatus.CREATE.getCode())
                .list();
    }

    public EScheduleType getScheduleType() {
       return null;
    }

    @Override
    public String getSchedulerName() {
        return getOperatorType().name();
    }
}
