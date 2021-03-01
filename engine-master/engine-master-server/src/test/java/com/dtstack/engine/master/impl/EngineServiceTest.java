package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.EngineVO;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import com.dtstack.engine.dao.TestComponentDao;
import com.dtstack.engine.dao.TestQueueDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author basion
 * @Classname EngineServiceTest
 * @Description unit test for EngineService
 * @Date 2020-11-25 19:04:44
 * @Created basion
 */
public class EngineServiceTest extends AbstractTest {

    @Autowired
    private EngineService engineService;

    @Autowired
    private TestQueueDao queueDao;

    @Autowired
    TestComponentDao componentDao;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetQueue() {
        Queue one = queueDao.getOne();
        List<QueueVO> getQueue = engineService.getQueue(one.getEngineId());
        Assert.assertTrue(CollectionUtils.isNotEmpty(getQueue));
    }

    @Test
    @Rollback
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void testListSupportEngine() {
        List<EngineSupportVO> listSupportEngine = engineService.listSupportEngine(1L);
        Assert.assertTrue(CollectionUtils.isNotEmpty(listSupportEngine));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateEngineTenant() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        Engine defaultHadoopEngine = DataCollection.getData().getDefaultHadoopEngine();
        engineService.updateEngineTenant(defaultCluster.getId(), defaultHadoopEngine.getId());
    }

    @Test
    @Rollback
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void testUpdateResource() {
        Engine defaultHadoopEngine = DataCollection.getData().getDefaultHadoopEngine();
        ComponentTestResult.ClusterResourceDescription description = new ComponentTestResult.ClusterResourceDescription(3, 100000, 10, new ArrayList<>());
        engineService.updateResource(defaultHadoopEngine.getId(), description);
        Engine one = engineService.getOne(defaultHadoopEngine.getId());
        Assert.assertEquals(one.getTotalMemory(),100000);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetOne() {
        Engine defaultHadoopEngine = DataCollection.getData().getDefaultHadoopEngine();
        Engine getOne = engineService.getOne(defaultHadoopEngine.getId());
        Assert.assertNotNull(getOne);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testListClusterEngines() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        List<EngineVO> listClusterEngines = engineService.listClusterEngines(defaultCluster.getId(), true);
        Assert.assertTrue(CollectionUtils.isNotEmpty(listClusterEngines));
    }
}
