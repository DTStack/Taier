package com.dtstack.engine.master.jobDeleader;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.jobdealer.JobRestartDealer;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    public void testCheckAndRestartForSubmitResult() throws Exception {

        JobClient jobClient = CommonUtils.getJobClient();
        addDefaultCluster();
        jobRestartDealer.checkAndRestartForSubmitResult(jobClient);
    }

    @Test
    public void testCheckAndRestart() {
        EngineJobCache jobCache = DataCollection.getData().getEngineJobCache();
        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        boolean flag = jobRestartDealer.checkAndRestart(2, jobCache.getJobId(), checkpoint.getTaskEngineId(), "1");
        Assert.assertTrue(flag);
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
