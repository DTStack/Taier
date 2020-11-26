package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 5:02 下午 2020/11/13
 */
public class TestJobDealer extends AbstractTest {


    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private ClusterDao clusterDao;


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAddSubmitJob() throws Exception {

        JobClient jobClient = CommonUtils.getJobClient();
        addDefaultCluster();
        jobClient.setTaskId("afafafga");
        jobDealer.addSubmitJob(jobClient);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetAllNodesGroupQueueInfo() throws Exception {

        Map<String, Map<String, GroupInfo>> allNodesGroupQueueInfo = jobDealer.getAllNodesGroupQueueInfo();
        Assert.assertNotNull(allNodesGroupQueueInfo);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetAndUpdateEngineLog(){

        ScheduleTaskShade taskShade = DataCollection.getData().getScheduleTaskShade();
        EngineJobCache jobCache = DataCollection.getData().getEngineJobCache();
        EngineJobCheckpoint checkpoint = DataCollection.getData().getEngineJobCheckpoint();
        String andUpdateEngineLog = jobDealer.getAndUpdateEngineLog(jobCache.getJobId(),checkpoint.getTaskEngineId(),
                "11",taskShade.getDtuicTenantId());
        Assert.assertNotNull(andUpdateEngineLog);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAfterPropertiesSet() throws Exception {

        EngineJobCache jobCache2 = DataCollection.getData().getEngineJobCache2();
        jobDealer.afterPropertiesSet();
    }


    @Test
    public void  testAfterSubmitJobVast() throws Exception {

        List<JobClient> jobClientList = new ArrayList<>();
        JobClient jobClient = CommonUtils.getJobClient();
        jobClientList.add(jobClient);
        jobDealer.afterSubmitJobVast(jobClientList);
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
