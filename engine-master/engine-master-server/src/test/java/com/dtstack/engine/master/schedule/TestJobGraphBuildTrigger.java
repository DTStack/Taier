package com.dtstack.engine.master.schedule;

import com.dtstack.engine.master.server.scheduler.JobGraphBuilderTrigger;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

/**
 * @author yuebai
 * @date 2021-08-18
 */
public class TestJobGraphBuildTrigger {

    @Test
    public void testBuild(){
        JobGraphBuilderTrigger graphBuilderTrigger = new JobGraphBuilderTrigger();
        Object getTriggerDay = ReflectionTestUtils.invokeMethod(graphBuilderTrigger, "getTriggerDay", "09:00:00");
        Assert.notNull(getTriggerDay,"");
    }
}
