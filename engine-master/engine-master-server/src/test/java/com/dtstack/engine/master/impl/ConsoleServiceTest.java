package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.vo.console.ConsoleJobVO;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.TestClusterDao;
import com.dtstack.engine.dao.TestComponentDao;
import com.dtstack.engine.dao.TestEngineJobCacheDao;
import com.dtstack.engine.dao.TestScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.vo.TaskTypeResourceTemplateVO;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
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
@PrepareForTest({ZkService.class, WorkerOperator.class})
public class ConsoleServiceTest extends AbstractTest {

    @Mock
    private WorkerOperator workerOperator;

    @Autowired
    @InjectMocks
    private ConsoleService consoleService;

    @Mock
    private ZkService zkService;

    @Autowired
    private TestScheduleJobDao scheduleJobDao;

    @Autowired
    TestEngineJobCacheDao engineJobCacheDao;

    @Autowired
    TestClusterDao clusterDao;

    @Autowired
    TestComponentDao componentDao;

    @Before
    public void setup() throws Exception {
        initMock();
    }

    private void initMock() throws Exception {
        MockitoAnnotations.initMocks(this);
        initMockZkService();
        initMockWorkOperator();
    }

    private void initMockWorkOperator() throws Exception {
        PowerMockito.mockStatic(WorkerOperator.class);
        PowerMockito.mockStatic(WorkerOperator.class);
        ClusterResource clusterResource = new ClusterResource();
        when(workerOperator.clusterResource(any(),any())).thenReturn(clusterResource);
    }

    private void initMockZkService() {
        List<String> aliveNodes = new ArrayList<>();
        aliveNodes.add("127.0.0.1:9099");
        Mockito.when(zkService.getAliveBrokersChildren()).thenReturn(aliveNodes);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testOverview() {
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
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
        Assert.assertEquals(finishJob,true);
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
        ScheduleJob one = scheduleJobDao.getOne();
        ConsoleJobVO searchJob = consoleService.searchJob(one.getJobName());
        Assert.assertNotNull(searchJob);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testListNames() {
        ScheduleJob one = scheduleJobDao.getOne();
        List<String> listNames = consoleService.listNames(one.getJobName());
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
    public void testGroupDetail() {
        EngineJobCache one = engineJobCacheDao.getOne();
        PageResult groupDetail = consoleService.groupDetail(one.getJobResource(), one.getNodeAddress(), one.getStage(), 10, 1, "");
        Assert.assertNotNull(groupDetail);
        Assert.assertTrue(groupDetail.getTotalCount()>0);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testJobStick() {
        EngineJobCache one = engineJobCacheDao.getOne();
        Boolean jobStick = consoleService.jobStick(one.getJobId());
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
    public void testStopJobList() throws Exception {
        EngineJobCache one = engineJobCacheDao.getOne();
        consoleService.stopJobList(one.getJobResource(), one.getNodeAddress(), one.getStage(), Lists.newArrayList(one.getJobId()));
    }

    @Test
    @Rollback
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void testClusterResources() {
        Cluster one = clusterDao.getOne();
        ClusterResource clusterResources = consoleService.clusterResources(one.getClusterName());
        Assert.assertNotNull(clusterResources);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetResources() {
        Component hdfsComponent = Template.getDefaultHdfsComponentTemplate();
        Cluster cluster = clusterDao.getOne();
        ClusterResource getResources = consoleService.getResources(hdfsComponent, cluster);
        Assert.assertNotNull(getResources);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetYarnComponent() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        Component getYarnComponent = consoleService.getYarnComponent(defaultCluster.getId());
        Assert.assertNotNull(getYarnComponent);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetTaskResourceTemplate() {
        List<TaskTypeResourceTemplateVO> getTaskResourceTemplate = consoleService.getTaskResourceTemplate();
        Assert.assertTrue(org.apache.commons.collections.CollectionUtils.isNotEmpty(getTaskResourceTemplate));
    }
}
