package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.Engine;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
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
 * @Classname QueueServiceTest
 * @Description unit test for QueueService
 * @Date 2020-11-25 19:39:34
 * @Created basion
 */
public class QueueServiceTest extends AbstractTest {

    @Autowired
    private QueueService queueService;

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
    public void testUpdateQueue() {
        List<ComponentTestResult.QueueDescription> descriptions = new ArrayList<>();
        ComponentTestResult.QueueDescription queueDescription = new ComponentTestResult.QueueDescription();
        queueDescription.setCapacity("1.0");
        queueDescription.setChildQueues(null);
        queueDescription.setMaximumCapacity("1.0");
        queueDescription.setQueueName("root");
        queueDescription.setQueuePath("root");
        queueDescription.setQueueState("RUNNING");
        descriptions.add(queueDescription);
        ComponentTestResult.ClusterResourceDescription description = new ComponentTestResult.ClusterResourceDescription(3,100000,10,descriptions);
        Engine defaultHadoopEngine = DataCollection.getData().getDefaultHadoopEngine();
        queueService.updateQueue(defaultHadoopEngine.getId(), description);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateQueue2() {
        //测试newAddQueue
        List<ComponentTestResult.QueueDescription> descriptions = new ArrayList<>();
        ComponentTestResult.QueueDescription queueDescription = new ComponentTestResult.QueueDescription();
        queueDescription.setCapacity("1.0");
        queueDescription.setChildQueues(null);
        queueDescription.setMaximumCapacity("1.0");
        queueDescription.setQueueName("root");
        queueDescription.setQueuePath("root");
        queueDescription.setQueueState("RUNNING");
        descriptions.add(queueDescription);
        ComponentTestResult.ClusterResourceDescription description = new ComponentTestResult.ClusterResourceDescription(3,100000,10,descriptions);
        queueService.updateQueue(2L, description);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAddNamespaces() {
        Long addNamespaces = queueService.addNamespaces(1L, "test");
        Assert.assertNotNull(addNamespaces);
    }
}
