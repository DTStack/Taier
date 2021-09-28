/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.domain.Engine;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import com.dtstack.engine.master.vo.QueueVO;
import com.dtstack.engine.master.vo.engine.EngineSupportVO;
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
        List<EngineSupportVO> listSupportEngine = engineService.listSupportEngine(1L,false);
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

}
