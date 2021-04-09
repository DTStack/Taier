package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.schedule.common.enums.AppType;
import org.junit.Assert;
import org.junit.Before;
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

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }


//    @Test
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
//    @Rollback
//    public void testCheckRemoveAndUpdateFlowJobStatus() {
//        ScheduleJob scheduleJobFirst = DataCollection.getData().getScheduleJobFirst();
//        boolean checkRemoveAndUpdateFlowJobStatus = batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleJobFirst.getId(), scheduleJobFirst.getFlowJobId(), scheduleJobFirst.getAppType());
//        Assert.assertTrue(!checkRemoveAndUpdateFlowJobStatus);
//        ScheduleJob scheduleJobSubmitted = DataCollection.getData().getScheduleJobSubmitted();
//        boolean status = batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleJobSubmitted.getId(), scheduleJobSubmitted.getFlowJobId(), scheduleJobSubmitted.getAppType());
//        Assert.assertTrue(!status);
//    }

}
