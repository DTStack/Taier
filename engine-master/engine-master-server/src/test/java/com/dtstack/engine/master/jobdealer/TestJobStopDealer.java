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

package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 9:22 下午 2020/11/13
 */
public class TestJobStopDealer extends AbstractTest {


    @Autowired
    private JobStopDealer jobStopDealer;


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAddStopJobs(){

        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteTaskId();
        ScheduleJob scheduleJob2 = DataCollection.getData().getScheduleJobDefiniteJobId();
        List<ScheduleJob> list = new ArrayList<>();
        list.add(scheduleJob);
        list.add(scheduleJob2);
        list.add(scheduleJob);

        int i = jobStopDealer.addStopJobs(list);
        Assert.assertNotNull(i);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDestroy() throws Exception {
        jobStopDealer.destroy();
    }


}
