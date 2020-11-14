package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 9:22 下午 2020/11/13
 */
public class TestJobStopDealer extends AbstractTest {


    @Autowired
    private JobStopDealer jobStopDealer;


    @Test
    public void testAddStopJobs(){

        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteTaskId();
        ScheduleJob scheduleJob2 = DataCollection.getData().getScheduleJobDefiniteJobId();
        List<ScheduleJob> list = new ArrayList<>();
        list.add(scheduleJob);
        list.add(scheduleJob2);
        list.add(scheduleJob);

        int i = jobStopDealer.addStopJobs(list);
        Assert.assertNotNull(i);
    }


    @Test
    public void testDestroy() throws Exception {
        jobStopDealer.destroy();
    }


}
