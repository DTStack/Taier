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

package com.dtstack.taiga.rdbs.common;

import com.dtstack.taiga.pluginapi.JobClient;
import com.dtstack.taiga.pluginapi.pojo.JudgeResult;
import com.dtstack.taiga.rdbs.common.executor.RdbsExeQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;

import static org.mockito.Mockito.when;

public class RdbsResourceInfoTest {

    @InjectMocks
    RdbsResourceInfo rdbsResourceInfo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testJudgeSlots() throws Exception {

        RdbsExeQueue rdbsExeQueue = PowerMockito.mock(RdbsExeQueue.class);
        when(rdbsExeQueue.checkCanSubmit()).thenReturn(true);
        MemberModifier.field(RdbsResourceInfo.class, "rdbsExeQueue").set(rdbsResourceInfo, rdbsExeQueue);

        JobClient jobClient = new JobClient();
        JudgeResult judgeResult = rdbsResourceInfo.judgeSlots(jobClient);
        Assert.assertTrue(judgeResult.available());
    }

}
