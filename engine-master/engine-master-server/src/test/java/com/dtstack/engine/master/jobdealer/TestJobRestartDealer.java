package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
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
    public void testCheckAndRestart() {
        addDefaultCluster();
        EngineJobCache jobCache = DataCollection.getData().getEngineJobCache();
        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        boolean flag = jobRestartDealer.checkAndRestart(2, jobCache.getJobId(), checkpoint.getTaskEngineId(), "1");
        Assert.assertFalse(flag);
        boolean flag2 = jobRestartDealer.checkAndRestart(8, jobCache.getJobId(), checkpoint.getTaskEngineId(), "1");
        Assert.assertFalse(flag2);
        //失败重试
        EngineJobCache jobCache2 = DataCollection.getData().getEngineJobCache2();
        boolean flag3 = jobRestartDealer.checkAndRestart(8, jobCache2.getJobId(), checkpoint.getTaskEngineId(), "1");
        Assert.assertTrue(flag3);

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
