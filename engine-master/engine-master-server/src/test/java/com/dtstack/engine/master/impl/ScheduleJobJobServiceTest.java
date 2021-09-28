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
import com.dtstack.engine.domain.ScheduleJobJob;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.impl.vo.ScheduleJobVO;
import com.dtstack.engine.common.enums.AppType;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.dtstack.engine.master.AbstractTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author basion
 * @Classname ScheduleJobJobServiceTest
 * @Description unit test for ScheduleJobJobService
 * @Date 2020-11-26 17:28:38
 * @Created basion
 */
public class ScheduleJobJobServiceTest extends AbstractTest {

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDisplayOffSpring() throws Exception {
        ScheduleJob defaultJobForSpring1 = DataCollection.getData().getDefaultJobForSpring1();
        ScheduleJobVO displayOffSpring = scheduleJobJobService.displayOffSpring(defaultJobForSpring1.getId(), 2);
        Assert.assertNotNull(displayOffSpring);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDisplayOffSpringWorkFlow() throws Exception {
        ScheduleJob defaultJobForFlowChild = DataCollection.getData().getDefaultJobForFlowParent();
        ScheduleJobVO displayOffSpringWorkFlow = scheduleJobJobService.displayOffSpringWorkFlow(defaultJobForFlowChild.getId(), AppType.RDOS.getType());
        Assert.assertNotNull(displayOffSpringWorkFlow);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDisplayForefathers() throws Exception {
        ScheduleJob defaultJobForFlowChild = DataCollection.getData().getDefaultJobForFlowChild();
        ScheduleJobVO displayForefathers = scheduleJobJobService.displayForefathers(defaultJobForFlowChild.getId(), 4);
        Assert.assertNotNull(displayForefathers);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetJobChild() {
        ScheduleJob defaultJobForFlowChild = DataCollection.getData().getDefaultJobForFlowParent();
        List<ScheduleJobJob> getJobChild = scheduleJobJobService.getJobChild(defaultJobForFlowChild.getJobKey());
        Assert.assertTrue(CollectionUtils.isNotEmpty(getJobChild));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testBatchInsert() {
        ScheduleJobJob jobJob = Template.getDefaultScheduleJobJobFlowTemplate();
        jobJob.setJobKey("aaaaaaa");
        jobJob.setParentJobKey("bbbbbbb");
        jobJob.setParentAppType(jobJob.getAppType());
        List<ScheduleJobJob> jobJobs = new ArrayList<>();
        jobJobs.add(jobJob);
        int batchInsert = scheduleJobJobService.batchInsert(jobJobs);
        Assert.assertEquals(batchInsert,1);
    }
}
