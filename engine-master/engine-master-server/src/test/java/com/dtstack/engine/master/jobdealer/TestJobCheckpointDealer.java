package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.worker.WorkerOperator;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.impl.ClusterService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
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
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;


    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @MockBean
    private WorkerOperator workerOperator;

    @Autowired
    private ClusterService clusterService;


    @Before
    public void setup() throws Exception{
        ReflectionTestUtils.setField(jobCheckpointDealer,"workerOperator", workerOperator);
        ReflectionTestUtils.setField(jobCheckpointDealer,"engineJobCheckpointDao", engineJobCheckpointDao);
        ReflectionTestUtils.setField(jobCheckpointDealer,"engineJobCacheDao", engineJobCacheDao);
        ReflectionTestUtils.setField(jobCheckpointDealer,"scheduleJobDao", scheduleJobDao);
        ReflectionTestUtils.setField(jobCheckpointDealer,"workerOperator", workerOperator);
        ReflectionTestUtils.setField(jobCheckpointDealer,"clusterService", clusterService);
        when(workerOperator.getCheckpoints(any())).thenReturn("{\"restored\":0,\"total\":13,\"in_progress\":0,\"completed\":11,\"failed\":2}");
    }

//    @Test
//    public void testAfterPropertiesSet(){
//        jobCheckpointDealer.afterPropertiesSet();
//    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAddCheckpointTaskForQueue() throws ExecutionException {

        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        Integer computeType = 1;
        String taskId = checkpoint.getTaskId();
        JobIdentifier jobIdentifier = new JobIdentifier(jobId.getJobId(),jobId.getApplicationId(),taskId,
                jobId.getTenantId(),engineJobCache.getEngineType(),1,1L,"",null);
        String engineTypeName = "spark";
//        jobCheckpointDealer.addCheckpointTaskForQueue(computeType,engineJobCache.getJobId(),jobIdentifier,engineTypeName);
        //2
        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        EngineJobCache one = engineJobCacheDao.getOne(engineJobCache2.getJobId());
        if(null == one){
            engineJobCacheDao.insert(engineJobCache2.getJobId(),engineJobCache2.getEngineType(),engineJobCache2.getComputeType(),
                    engineJobCache2.getStage(),engineJobCache2.getJobInfo(),engineJobCache2.getNodeAddress(),
                    engineJobCache2.getJobName(),engineJobCache2.getJobPriority(),engineJobCache2.getJobResource());
        }
        jobCheckpointDealer.addCheckpointTaskForQueue(computeType,engineJobCache2.getJobId(),jobIdentifier,engineTypeName);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
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

        jobCheckpointDealer.afterPropertiesSet();

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
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
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
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
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateJobCheckpoints(){

        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache2.getJobId(),jobId.getApplicationId(),engineJobCache2.getJobId());
        when(workerOperator.getCheckpoints(any())).thenReturn("{\"restored\":0,\"total\":13,\"in_progress\":0,\"completed\":11," +
                "\"failed\":2,\"history\":[{\"id\":1,\"trigger_timestamp\":101313,\"external_path\":\"Users\",\"status\":2}]}");

        jobCheckpointDealer.updateJobCheckpoints(jobIdentifier);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateJobCheckpointsFailed(){
        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        ScheduleJob jobId = DataCollection.getData().getScheduleJobDefiniteJobId();
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineJobCache2.getJobId(), jobId.getApplicationId(), engineJobCache2.getJobId());
        when(workerOperator.getCheckpoints(any())).thenReturn("{\"restored\":0,\"total\":13,\"in_progress\":0,\"completed\":11," +
                "\"failed\":2,\"history\":[{\"id\":1,;;32422\"trigger_timestamp\":101313,\"external_path\":\"Users\",\"status\":2}]}");

        jobCheckpointDealer.updateJobCheckpoints(jobIdentifier);

        List<EngineJobCheckpoint> engineJobCheckpoints = engineJobCheckpointDao.listFailedByTaskIdAndRangeTime(jobIdentifier.getTaskId(), null, null, 5);
        System.out.println(engineJobCheckpoints.get(0));
    }


}
