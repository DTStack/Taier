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

package com.dtstack.taier.rdbs.common.executor;

import com.dtstack.taier.pluginapi.JobClient;
import com.google.common.collect.Queues;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.powermock.api.mockito.PowerMockito.when;


public class RdbsExeQueueTest {

    @InjectMocks
    RdbsExeQueue rdbsExeQueue;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Exception {
        BlockingQueue<JobClient> waitQueue = Queues.newLinkedBlockingQueue();
        JobClient jobClient = new JobClient();
        jobClient.setJobName("test");
        jobClient.setJobId("test");
        jobClient.setSql("select * from tableTest;");
        jobClient.setTaskParams("{\"task\":\"test\"}");
        waitQueue.add(jobClient);
        MemberModifier.field(RdbsExeQueue.class, "waitQueue").set(rdbsExeQueue, waitQueue);
        MemberModifier.field(RdbsExeQueue.class, "minSize").set(rdbsExeQueue, 1);
        MemberModifier.field(RdbsExeQueue.class, "maxSize").set(rdbsExeQueue, 1);
        rdbsExeQueue.init();
    }

    @Test
    public void testSubmit() {
        JobClient jobClient = new JobClient();
        jobClient.setJobId("test");
        String submit = rdbsExeQueue.submit(jobClient);
        Assert.assertNotNull(submit);
    }


    @Test
    public void testRdbsExeRun() throws Exception {

        RdbsExeQueue.RdbsExe rdbsExe = PowerMockito.mock(RdbsExeQueue.RdbsExe.class);
        List<String> sqlList = new ArrayList<>();
        sqlList.add("select * from testTables");
        MemberModifier.field(RdbsExeQueue.RdbsExe.class, "sqlList").set(rdbsExe, sqlList);

        rdbsExe.run();
        System.out.println("test");
    }
}
