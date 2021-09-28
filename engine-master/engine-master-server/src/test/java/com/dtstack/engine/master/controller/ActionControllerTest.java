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

package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.vo.action.ActionJobStatusVO;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.TestCase.fail;


public class ActionControllerTest extends AbstractTest {

    @Autowired
    ActionController actionController;

    @Test
    public void testGetListJobStatusByJobIds() {
        List<String> jobIds = Lists.newArrayList(DataCollection.getData().getScheduleJobFirst().getJobId());
        try {
            List<ActionJobStatusVO> actionJobStatusVOS = actionController.listJobStatusByJobIds(jobIds);
            System.out.println(actionJobStatusVOS);
        } catch (Exception e) {
            fail();
        }
    }
}
