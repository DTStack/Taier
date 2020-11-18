package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.JobGraphTrigger;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.master.AbstractTest;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author yuebai
 * @date 2020-11-17
 */
@PrepareForTest({AkkaConfig.class, ClientOperator.class})
public class JobGraphTriggerServiceTest extends AbstractTest {

    @Autowired
    private JobGraphTriggerService jobGraphTriggerService;

    @Test
    public void test(){
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        jobGraphTriggerService.addJobTrigger(timestamp);
        boolean isAdd = jobGraphTriggerService.checkHasBuildJobGraph(timestamp);
        Assert.assertTrue(isAdd);
        JobGraphTrigger triggerByDate = jobGraphTriggerService.getTriggerByDate(timestamp, 0);
        Assert.assertNotNull(triggerByDate);
    }
}
