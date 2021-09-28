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

import com.dtstack.engine.domain.NodeMachine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.dtstack.engine.master.AbstractTest;

import java.util.List;

/**
 * @author basion
 * @Classname NodeMachineServiceTest
 * @Description unit test for NodeMachineService
 * @Date 2020-11-26 16:04:20
 * @Created basion
 */
public class NodeMachineServiceTest extends AbstractTest {

    @Autowired
    private NodeMachineService nodeMachineService;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }

    @Test
    public void testListByAppType() {
        List<NodeMachine> listByAppType = nodeMachineService.listByAppType("");
        Assert.assertNotNull(listByAppType);
    }

    @Test
    public void testGetByAppTypeAndMachineType() {
        NodeMachine getByAppTypeAndMachineType = nodeMachineService.getByAppTypeAndMachineType("", 0);
        Assert.assertNotNull(getByAppTypeAndMachineType);
    }
}
