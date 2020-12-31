package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.JobGraphTrigger;
import com.dtstack.engine.master.AbstractTest;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * @author yuebai
 * @date 2020-11-17
 */
public class JobGraphTriggerServiceTest extends AbstractTest {

    @Autowired
    private JobGraphTriggerService jobGraphTriggerService;

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAdd() {
        Timestamp timestamp = new Timestamp(DateTime.now().withTime(0, 0, 0, 0).getMillis());
        jobGraphTriggerService.addJobTrigger(timestamp);
        boolean isAdd = jobGraphTriggerService.checkHasBuildJobGraph(timestamp);
        Assert.assertTrue(isAdd);
        JobGraphTrigger triggerByDate = jobGraphTriggerService.getTriggerByDate(timestamp, 0);
        Assert.assertNotNull(triggerByDate);
    }
}