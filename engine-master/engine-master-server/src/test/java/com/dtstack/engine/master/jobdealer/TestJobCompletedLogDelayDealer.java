package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.bo.JobCompletedInfo;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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
    public void testRun(){

        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache2.getJobId(),jobId.getApplicationId(),engineJobCache2.getJobId());
        JobCompletedLogDelayDealer delayDealer = getJobCompletedLogDelayDealer();

        delayDealer.addCompletedTaskInfo(new JobCompletedInfo(engineJobCache2.getJobId(),
                jobIdentifier,engineJobCache2.getEngineType(),engineJobCache2.getComputeType(),5000));
    }
}
