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

import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.pluginapi.JobIdentifier;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.jobdealer.bo.JobCompletedInfo;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 4:11 下午 2020/11/16
 */
public class TestJobCompletedLogDelayDealer extends AbstractTest {


    private JobCompletedLogDelayDealer jobCompletedLogDelayDealer;

    @Autowired
    private ApplicationContext applicationContext;



    private JobCompletedLogDelayDealer getJobCompletedLogDelayDealer(){

        return new JobCompletedLogDelayDealer(applicationContext);

    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testRun(){

        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache2.getJobId(),jobId.getApplicationId(),engineJobCache2.getJobId());
        JobCompletedLogDelayDealer delayDealer = getJobCompletedLogDelayDealer();

        delayDealer.addCompletedTaskInfo(new JobCompletedInfo(engineJobCache2.getJobId(),
                jobIdentifier,engineJobCache2.getEngineType(),engineJobCache2.getComputeType(),5000));
    }
}
