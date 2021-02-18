package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.vo.console.ConsoleJobVO;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.TestEngineJobCacheDao;
import com.dtstack.engine.dao.TestScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.vo.TaskTypeResourceTemplateVO;
import com.dtstack.engine.master.zookeeper.ZkService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2020-11-05
 */
public class ConsoleServiceTest extends AbstractTest {

    @MockBean
    private WorkerOperator workerOperator;

    @Autowired
    private ConsoleService consoleService;

    @MockBean
    private ZkService zkService;

    @Autowired
    private TestScheduleJobDao scheduleJobDao;

    @Autowired
    TestEngineJobCacheDao engineJobCacheDao;


    @Before
    public void setup() throws Exception {
        initMock();
    }

    private void initMock() throws Exception {
        initMockZkService();
        initMockWorkOperator();
    }

    private void initMockWorkOperator() throws Exception {
        ClusterResource clusterResource = new ClusterResource();
        when(workerOperator.clusterResource(any(),any())).thenReturn(clusterResource);
    }

    private void initMockZkService() {
        List<String> aliveNodes = new ArrayList<>();
        aliveNodes.add("127.0.0.1:9099");
        when(zkService.getAliveBrokersChildren()).thenReturn(aliveNodes);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testOverview() {
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        engineJobCache.setJobId("testOverview");
        engineJobCacheDao.insert(engineJobCache);
        Collection<Map<String, Object>> dev = consoleService.overview(engineJobCache.getNodeAddress(), "dev");
        Assert.assertNotNull(dev);
        Collection<Map<String, Object>> defaultCluster = consoleService.overview(engineJobCache.getNodeAddress(), "default");
        Assert.assertTrue(CollectionUtils.isEmpty(defaultCluster));

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testFinishJob() {
        Boolean finishJob = consoleService.finishJob("asdf", RdosTaskStatus.RUNNING.getStatus());
        Assert.assertEquals(finishJob,false);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testNodeAddress() {
        List<String> nodeAddress = consoleService.nodeAddress();
        Assert.assertEquals(nodeAddress.size(),1);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSearchJob() {
        EngineJobCache engineJobCache = initScheduleJobForTest();
        ConsoleJobVO searchJob = consoleService.searchJob(engineJobCache.getJobName());
        Assert.assertNotNull(searchJob);
    }

    private EngineJobCache initScheduleJobForTest() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setJobName("initScheduleJobForTest");
        scheduleJobTemplate.setJobKey("initScheduleJobForTest");
        scheduleJobTemplate.setJobId("initScheduleJobForTest");
        scheduleJobDao.insert(scheduleJobTemplate);
        EngineJobCache engineJobCacheTemplate2 = Template.getEngineJobCacheTemplate2();
        engineJobCacheTemplate2.setJobName("initScheduleJobForTest");
        engineJobCacheTemplate2.setJobId("initScheduleJobForTest");
        engineJobCacheDao.insert(engineJobCacheTemplate2);
        return engineJobCacheTemplate2;
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testListNames() {
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        List<String> listNames = consoleService.listNames(engineJobCache.getJobName());
        Assert.assertTrue(org.apache.commons.collections.CollectionUtils.isNotEmpty(listNames));
    }

    @Test
    public void testJobResources() {
        List<String> jobResources = consoleService.jobResources();
        Assert.assertTrue(org.apache.commons.collections.CollectionUtils.isNotEmpty(jobResources));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testJobStick() {
        EngineJobCache engineJobCache2 = DataCollection.getData().getEngineJobCache2();
        engineJobCache2.setJobId("abcdefg");
        engineJobCacheDao.insert(engineJobCache2);
        Boolean jobStick = consoleService.jobStick(engineJobCache2.getJobId());
        Assert.assertTrue(jobStick);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testStopJob() throws Exception {
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        consoleService.stopJob(engineJobCache.getJobId());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testStopAll() throws Exception {
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        consoleService.stopAll(engineJobCache.getJobResource(), engineJobCache.getNodeAddress());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetTaskResourceTemplate() {
        List<TaskTypeResourceTemplateVO> getTaskResourceTemplate = consoleService.getTaskResourceTemplate();
        Assert.assertTrue(org.apache.commons.collections.CollectionUtils.isNotEmpty(getTaskResourceTemplate));
    }
}
