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

package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.ScheduleFillDataJob;
import com.dtstack.engine.mapper.ScheduleFillDataJobDao;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleFillDataJobService  {

    @Autowired
    private ScheduleFillDataJobDao scheduleFillDataJobDao;

    public boolean checkExistsName(String jobName, Long projectId) {
        if (projectId == null) {
            return Boolean.TRUE;
        }
        ScheduleFillDataJob scheduleFillDataJob = scheduleFillDataJobDao.getByJobName(jobName, projectId);
        return scheduleFillDataJob != null;
    }

    public List<ScheduleFillDataJob> getFillJobList(List<String> fillJobName, long projectId){
        if(CollectionUtils.isEmpty(fillJobName)){
            return Lists.newArrayList();
        }

        return scheduleFillDataJobDao.listFillJob(fillJobName, projectId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ScheduleFillDataJob saveData(String jobName, Long tenantId, Long projectId, String runDay,
                                        String fromDay, String toDay, Long userId, Integer appType, Long dtuicTenantId) {

        Timestamp currTimeStamp = Timestamp.valueOf(LocalDateTime.now());

        ScheduleFillDataJob fillDataJob = new ScheduleFillDataJob();
        fillDataJob.setJobName(jobName);
        fillDataJob.setFromDay(fromDay);
        fillDataJob.setToDay(toDay);
        fillDataJob.setRunDay(runDay);
        fillDataJob.setTenantId(tenantId);
        fillDataJob.setCreateUserId(userId);
        fillDataJob.setGmtModified(currTimeStamp);
        fillDataJob.setGmtCreate(currTimeStamp);
        scheduleFillDataJobDao.insert(fillDataJob);
        return fillDataJob;
    }

}
