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

import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.dtstack.engine.master.AbstractTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author basion
 * @Classname BatchFlowWorkJobServiceTest
 * @Description unit test for BatchFlowWorkJobService
 * @Date 2020-11-26 16:44:33
 * @Created basion
 */
public class BatchFlowWorkJobServiceTest extends AbstractTest {

    @Autowired
    private BatchFlowWorkJobService batchFlowWorkJobService;


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckRemoveAndUpdateFlowJobStatus() {
        ScheduleJob scheduleJobFirst = DataCollection.getData().getScheduleJobFirst();
        ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJobFirst);
        boolean checkRemoveAndUpdateFlowJobStatus = batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleBatchJob);
        Assert.assertTrue(checkRemoveAndUpdateFlowJobStatus);
        ScheduleJob scheduleJobSubmitted = DataCollection.getData().getScheduleJobSubmitted();
        ScheduleBatchJob batchJob = new ScheduleBatchJob(scheduleJobSubmitted);
        boolean status = batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(batchJob);
        Assert.assertTrue(status);
    }

}
