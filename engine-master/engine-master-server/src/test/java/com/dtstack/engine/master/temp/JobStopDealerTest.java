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

package com.dtstack.engine.master.temp;

import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class JobStopDealerTest extends AbstractTest {

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Test
    public void testAddStopFunction() {
        jobStopDealer.addStopJobs(getResourcesFromTaskId());
    }

    private List<ScheduleJob> getResourcesFromJobId() {
        List<Long> jobIdList = new ArrayList<>();
        jobIdList.add(50045L);
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.listByJobIds(jobIdList));
        return jobs;
    }

    private List<ScheduleJob> getResourcesFromTaskId() {
        List<String> taskIdsList = new ArrayList<>();
        taskIdsList.add("0243ba9f");
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.getRdosJobByJobIds(taskIdsList));
        return jobs;
    }
}
