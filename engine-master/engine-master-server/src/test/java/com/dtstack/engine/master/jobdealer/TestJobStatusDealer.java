package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.cache.ShardManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 5:04 下午 2020/11/14
 */
public class TestJobStatusDealer extends AbstractTest {


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private WorkerOperator workerOperator;

    @MockBean
    private ClientOperator clientOperator;





    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testRun(){

        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();

        ShardManager shardManager = new ShardManager(engineJobCache.getJobResource());
                shardManager.putJob(engineJobCache.getJobId(), RdosTaskStatus.RUNNING.getStatus());

        JobStatusDealer jobStatusDealer = new JobStatusDealer();
        jobStatusDealer.setJobResource(engineJobCache.getJobResource());
        jobStatusDealer.setShardManager(shardManager);
        jobStatusDealer.setShardCache(shardCache);
        jobStatusDealer.setApplicationContext(applicationContext);

        jobStatusDealer.run();
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testRun2(){


        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache2();
        ShardManager shardManager = new ShardManager(engineJobCache.getJobResource());
        shardManager.putJob(engineJobCache.getJobId(), RdosTaskStatus.SUBMITTING.getStatus());

        JobStatusDealer jobStatusDealer = new JobStatusDealer();
        jobStatusDealer.setJobResource(engineJobCache.getJobResource());
        jobStatusDealer.setShardManager(shardManager);
        jobStatusDealer.setShardCache(shardCache);
        ReflectionTestUtils.setField(jobStatusDealer,"workerOperator", workerOperator);
        jobStatusDealer.setApplicationContext(applicationContext);
        when(clientOperator.getJobStatus(any(),any(),any())).thenReturn(RdosTaskStatus.FINISHED);
        jobStatusDealer.run();
    }


}
