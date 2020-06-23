package com.dtstack.engine.master.impl;

import com.dtstack.engine.master.BaseTest;
import com.dtstack.engine.master.scheduler.ScheduleJobBack;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yuebai
 * @date 2020-06-23
 */
public class ScheduleJobBackTest extends BaseTest {

    @Autowired
    private ScheduleJobBack scheduleJobBack;

    @Test
    public void testScheduleJobBack(){
        scheduleJobBack.setIsMaster(true);
    }
}
