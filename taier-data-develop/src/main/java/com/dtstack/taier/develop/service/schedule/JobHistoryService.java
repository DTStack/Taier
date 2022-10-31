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

package com.dtstack.taier.develop.service.schedule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleJobHistory;
import com.dtstack.taier.dao.mapper.ScheduleJobHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobHistoryService extends ServiceImpl<ScheduleJobHistoryMapper, ScheduleJobHistory> {

    public String getEngineIdByApplicationId(String applicationId){
        ScheduleJobHistory scheduleJobHistory = getBaseMapper().selectOne(Wrappers.lambdaQuery(ScheduleJobHistory.class)
                .eq(ScheduleJobHistory::getApplicationId, applicationId));
        return scheduleJobHistory == null ? ""  : scheduleJobHistory.getEngineJobId();

    }

    public List<ScheduleJobHistory> listHistory(String jobId, Integer limit) {
        return getBaseMapper().selectList(Wrappers.lambdaQuery(ScheduleJobHistory.class)
                .eq(ScheduleJobHistory::getJobId, jobId).orderBy(true, true, ScheduleJobHistory::getId).last("limit " + limit));
    }
}
