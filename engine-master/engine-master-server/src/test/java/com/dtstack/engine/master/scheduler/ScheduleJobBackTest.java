package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.master.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: dazhi
 * @Date: 2020/11/16 10:52 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobBackTest extends AbstractTest {

    @Autowired
    private ScheduleJobBack scheduleJobBack;

    @Test
    public void testStopGraphBuildIsMaster() throws Exception {
        System.setProperty("job.back.cron.open","true");
        scheduleJobBack.setIsMaster(Boolean.TRUE);
    }
}
