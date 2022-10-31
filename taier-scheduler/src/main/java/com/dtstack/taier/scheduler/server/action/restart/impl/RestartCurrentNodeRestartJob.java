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

package com.dtstack.taier.scheduler.server.action.restart.impl;

import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.scheduler.server.action.restart.AbstractRestartJob;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午7:29
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RestartCurrentNodeRestartJob extends AbstractRestartJob {


    public RestartCurrentNodeRestartJob(EnvironmentContext environmentContext, ApplicationContext applicationContext) {
        super(environmentContext,applicationContext);
    }

    @Override
    public Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs) {
        Map<String, String> resumeBatchJobs = new HashMap<>(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId, ScheduleJob::getCycTime)));
        // 判断该节点是否被强弱规则任务所依赖
        for (ScheduleJob job : jobs) {
            setSubFlowJob(job, resumeBatchJobs);
        }
        return resumeBatchJobs;
    }
}
