package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 6:02 下午 2020/11/13
 */
public class TestJobRestartDealer extends AbstractTest {

    @Autowired
    private JobRestartDealer jobRestartDealer;

    @Autowired
    private ClusterDao clusterDao;


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestartForSubmitResult() throws Exception {

        JobClient jobClient = CommonUtils.getJobClient();
        addDefaultCluster();
        jobRestartDealer.checkAndRestartForSubmitResult(jobClient);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestart1() {
        addDefaultCluster();
        EngineJobCache jobCache = DataCollection.getData().getEngineJobCache();
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobId();
        boolean flag = jobRestartDealer.checkAndRestart(2, scheduleJob, jobCache);
        Assert.assertFalse(flag);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestart2() {
        addDefaultCluster();
        EngineJobCache jobCache = DataCollection.getData().getEngineJobCache();
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobId();
        boolean flag2 = jobRestartDealer.checkAndRestart(8, scheduleJob,jobCache );
        Assert.assertFalse(flag2);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestart3() {
        addDefaultCluster();
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobId();
        EngineJobCache jobCache4 = DataCollection.getData().getEngineJobCache4();
        boolean flag3 = jobRestartDealer.checkAndRestart(8, scheduleJob, jobCache4);
        Assert.assertTrue(flag3);

    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestart4() {

        //engineType为kylin
        addDefaultCluster();
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobId();
        EngineJobCache jobCache4 = DataCollection.getData().getEngineJobCache5();
        boolean flag3 = jobRestartDealer.checkAndRestart(8, scheduleJob, jobCache4);
        Assert.assertFalse(flag3);

    }



    private Cluster addDefaultCluster(){

        Cluster cluster = new Cluster();
        cluster.setId(-1L);
        cluster.setClusterName("defalut");
        cluster.setHadoopVersion("");
        Integer insert = clusterDao.insertWithId(cluster);
        return cluster;
    }
}
