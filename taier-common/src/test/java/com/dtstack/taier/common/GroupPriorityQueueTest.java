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

package com.dtstack.taier.common;

import com.dtstack.taier.common.queue.comparator.JobClientComparator;
import com.dtstack.taier.pluginapi.JobClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.PriorityBlockingQueue;

public class GroupPriorityQueueTest {

    @Test
    public void testPriorityQueueJob() {
        PriorityBlockingQueue<JobClient> queue = new PriorityBlockingQueue<JobClient>(10, new JobClientComparator());
        JobClient job3 = new JobClient();
        job3.setPriority(3);
        JobClient job4 = new JobClient();
        job4.setPriority(4);
        JobClient job1 = new JobClient();
        job1.setPriority(1);
        JobClient job2 = new JobClient();
        job2.setPriority(2);
        JobClient job5 = new JobClient();
        job5.setPriority(5);
        queue.put(job1);
        queue.put(job2);
        queue.put(job3);
        queue.put(job4);
        queue.put(job5);
        JobClient jjj = queue.poll();
        Assert.assertEquals(jjj.getPriority(), job1.getPriority());

    }
}
