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

package com.dtstack.taier.scheduler.server.builder.dependency;

import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleCorn;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/3/13 11:22 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public abstract class DecoratorJobDependency extends AbstractJobDependency {

    /**
     * 需要被增强的依赖
     */
    private final JobDependency jobDependency;

    public DecoratorJobDependency(String keyPreStr,
                                  ScheduleTaskShade currentTaskShade,
                                  ScheduleJobService scheduleJobService,
                                  List<ScheduleTaskShade> taskShadeList,
                                  JobDependency jobDependency) {
        super(keyPreStr, currentTaskShade, scheduleJobService, taskShadeList);
        this.jobDependency = jobDependency;
    }

    @Override
    public List<ScheduleJobJob> generationJobJobForTask(ScheduleCorn corn, Date currentDate, String currentJobKey) {
        if (jobDependency == null) {
            return Lists.newArrayList();
        }
        return jobDependency.generationJobJobForTask(corn, currentDate, currentJobKey);
    }
}
