package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.EngineVO;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import com.dtstack.engine.dao.TestComponentDao;
import com.dtstack.engine.dao.TestQueueDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author basion
 * @Classname EngineServiceTest
 * @Description unit test for EngineService
 * @Date 2020-11-25 19:04:44
 * @Created basion
 */
@PrepareForTest()
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
    public void testGetQueue() {
        Queue one = queueDao.getOne();
        List<QueueVO> getQueue = engineService.getQueue(one.getEngineId());
        Assert.assertTrue(CollectionUtils.isNotEmpty(getQueue));
    }

    private Component initYarnComponentForTest() {
        Component component = Template.getDefaultYarnComponentTemplate();
        componentDao.insert(component);
        return component;
    }

    private Component initHdfsComponentForTest() {
        Component component = Template.getDefaltHdfsComponentTemplate();
        componentDao.insert(component);
        return component;
    }

    @Test
    @Rollback
    public void testListSupportEngine() {
        initYarnComponentForTest();
        initHdfsComponentForTest();
        List<EngineSupportVO> listSupportEngine = engineService.listSupportEngine(1L);
        Assert.assertTrue(CollectionUtils.isNotEmpty(listSupportEngine));
    }

    @Test
    public void testUpdateEngineTenant() {
        engineService.updateEngineTenant(1L, 1L);
    }

    @Test
    @Rollback
    public void testUpdateResource() {
        ComponentTestResult.ClusterResourceDescription description = new ComponentTestResult.ClusterResourceDescription(3, 100000, 10, new ArrayList<>());
        engineService.updateResource(1L, description);
        Engine one = engineService.getOne(1L);
        Assert.assertEquals(one.getTotalMemory(),100000);
    }

    @Test
    public void testGetOne() {
        Engine getOne = engineService.getOne(1L);
        Assert.assertNotNull(getOne);
    }

    @Test
    @Rollback
    public void testListClusterEngines() {
        initYarnComponentForTest();
        initHdfsComponentForTest();
        List<EngineVO> listClusterEngines = engineService.listClusterEngines(1L, false);
        Assert.assertTrue(CollectionUtils.isNotEmpty(listClusterEngines));
    }
}
