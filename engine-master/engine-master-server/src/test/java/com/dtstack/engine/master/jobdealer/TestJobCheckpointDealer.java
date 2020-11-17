package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.impl.ClusterService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 3:19 下午 2020/11/13
 */
public class TestJobCheckpointDealer extends AbstractTest {


    @Spy
    private JobCheckpointDealer jobCheckpointDealer;

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Mock
    private WorkerOperator workerOperator;

    @Autowired
    private ClusterService clusterService;


    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(jobCheckpointDealer,"workerOperator", workerOperator);
        ReflectionTestUtils.setField(jobCheckpointDealer,"engineJobCheckpointDao", engineJobCheckpointDao);
        ReflectionTestUtils.setField(jobCheckpointDealer,"engineJobCacheDao", engineJobCacheDao);
        ReflectionTestUtils.setField(jobCheckpointDealer,"scheduleJobDao", scheduleJobDao);
        ReflectionTestUtils.setField(jobCheckpointDealer,"workerOperator", workerOperator);
        ReflectionTestUtils.setField(jobCheckpointDealer,"clusterService", clusterService);
        when(workerOperator.getCheckpoints(any())).thenReturn("{\"restored\":0,\"total\":13,\"in_progress\":0,\"completed\":11,\"failed\":2}");
    }

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
        jobIdentifier.setEngineType(EngineType.Dummy.name());
        String engineTypeName = "spark";
//        jobCheckpointDealer.addCheckpointTaskForQueue(computeType,engineJobCache.getJobId(),jobIdentifier,engineTypeName);
        //2
        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        jobCheckpointDealer.addCheckpointTaskForQueue(computeType,engineJobCache2.getJobId(),jobIdentifier,engineTypeName);
    }


    @Test
    public void testUpdateCheckpointImmediately(){

        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        String taskEngineId = checkpoint.getTaskEngineId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache2.getJobId(),jobId.getApplicationId(),engineJobCache2.getJobId());
        JobCheckpointInfo info = new JobCheckpointInfo(jobIdentifier,engineJobCache2.getEngineType());
        when(workerOperator.getCheckpoints(any())).thenReturn("{\"restored\":0,\"total\":13,\"in_progress\":0,\"completed\":11," +
                "\"failed\":2,\"history\":[{\"id\":1,\"trigger_timestamp\":101313,\"external_path\":\"Users\",\"status\":2}]}");
        jobCheckpointDealer.updateCheckpointImmediately(info,taskEngineId,2);
    }

    @Test
    public void testUpdateCheckpointImmediately2(){

        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        String taskEngineId = checkpoint.getTaskEngineId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache2.getJobId(),jobId.getApplicationId(),engineJobCache2.getJobId());
        JobCheckpointInfo info = new JobCheckpointInfo(jobIdentifier,engineJobCache2.getEngineType());
        when(workerOperator.getCheckpoints(any())).thenReturn("{\"restored\":0,\"total\":13,\"in_progress\":0,\"completed\":11," +
                "\"failed\":2,\"history\":[{\"id\":1,\"trigger_timestamp\":101313,\"external_path\":\"Users\",\"status\":2}]}");
        jobCheckpointDealer.updateCheckpointImmediately(info,taskEngineId,12);
    }

    @Test
    public void testUpdateCheckpointImmediately3(){

        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        String taskEngineId = checkpoint.getTaskEngineId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache2.getJobId(),jobId.getApplicationId(),engineJobCache2.getJobId());
        JobCheckpointInfo info = new JobCheckpointInfo(jobIdentifier,engineJobCache2.getEngineType());
        when(workerOperator.getCheckpoints(any())).thenReturn("{\"restored\":0,\"total\":13,\"in_progress\":0,\"completed\":11," +
                "\"failed\":2,\"history\":[{\"id\":1,\"trigger_timestamp\":101313,\"external_path\":\"Users\",\"status\":2}]}");
        jobCheckpointDealer.updateCheckpointImmediately(info,taskEngineId,4);
    }

    @Test
    public void testUpdateJobCheckpoints(){

        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache2.getJobId(),jobId.getApplicationId(),engineJobCache2.getJobId());
        jobIdentifier.setPluginInfo("");
        when(workerOperator.getCheckpoints(any())).thenReturn("{\"restored\":0,\"total\":13,\"in_progress\":0,\"completed\":11," +
                "\"failed\":2,\"history\":[{\"id\":1,\"trigger_timestamp\":101313,\"external_path\":\"Users\",\"status\":2}]}");

        jobCheckpointDealer.updateJobCheckpoints(jobIdentifier);
    }




}
