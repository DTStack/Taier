package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: dazhi
 * @Date: 2020/11/14 4:30 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobRichOperatorTest extends AbstractTest {

    @Autowired
    private JobRichOperator jobRichOperator;

    @Test
    public void testJobRichOperator() throws Exception {
        // task
        ScheduleTaskShade scheduleTaskShade = DataCollection.getData().getTask();

        // son node
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobByTask(scheduleTaskShade);

        // parent node
        ScheduleJob scheduleJobParam = DataCollection.getData().getScheduleJobByTask(scheduleTaskShade);


    }








}
