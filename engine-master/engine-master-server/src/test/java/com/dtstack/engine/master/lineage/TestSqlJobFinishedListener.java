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

package com.dtstack.engine.master.lineage;

import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: ZYD
 * Date: 2021/2/2 13:46
 * Description: 单测
 * @since 1.0.0
 */
public class TestSqlJobFinishedListener extends AbstractTest {


    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetLastScheduleJob() throws InterruptedException {

        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
        ScheduleJob scheduleJob2 = DataCollection.getData().getScheduleJobForth();
        ScheduleJob scheduleJob3 = DataCollection.getData().getScheduleJobFive();
        Long id ;
        if(scheduleJob3.getId() > scheduleJob2.getId()){
            id = scheduleJob3.getId();
        }else{
            id = scheduleJob2.getId();
        }
        ScheduleJob lastScheduleJob = scheduleJobDao.getLastScheduleJob(scheduleJob3.getTaskId(), id);
        Long lastId = id - 2L;
        Assert.assertEquals(lastScheduleJob.getId(),lastId);
    }
}
