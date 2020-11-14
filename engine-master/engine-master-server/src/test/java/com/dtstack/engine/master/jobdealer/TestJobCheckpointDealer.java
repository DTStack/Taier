package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 3:19 下午 2020/11/13
 */
public class TestJobCheckpointDealer extends AbstractTest {


    @Autowired
    private JobCheckpointDealer jobCheckpointDealer;

    @Test
    public void testAfterPropertiesSet(){

        jobCheckpointDealer.afterPropertiesSet();
    }

    @Test
    public void testAddCheckpointTaskForQueue() throws ExecutionException {

        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        Integer computeType = 1;
        String taskId = checkpoint.getTaskId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId.getJobId(),jobId.getApplicationId(),taskId);
        String engineTypeName = "spark";
        jobCheckpointDealer.addCheckpointTaskForQueue(computeType,engineJobCache.getJobId(),jobIdentifier,engineTypeName);
        //2
        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        jobCheckpointDealer.addCheckpointTaskForQueue(computeType,engineJobCache2.getJobId(),jobIdentifier,engineTypeName);
    }


    @Test
    public void testUpdateCheckpointImmediately(){

        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        String taskEngineId = checkpoint.getTaskEngineId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache.getJobId(),jobId.getApplicationId(),engineJobCache.getJobId());
        JobCheckpointInfo info = new JobCheckpointInfo(jobIdentifier,engineJobCache.getEngineType());
        jobCheckpointDealer.updateCheckpointImmediately(info,taskEngineId,2);
    }




}
