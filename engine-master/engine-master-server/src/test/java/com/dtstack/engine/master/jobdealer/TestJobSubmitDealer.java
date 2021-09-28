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

package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.server.queue.GroupPriorityQueue;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

import static junit.framework.TestCase.fail;

/**
 * @Author: newman
 * Date: 2020-11-30 19:17
 * Description: 测试JobSubmitDealer
 * @since 1.0.0
 */
public class TestJobSubmitDealer extends AbstractTest {


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScheduleJobDao scheduleJobDao;


    @Test
    public void testJobSubmitDealer1(){
        try {
            JobSubmitDealer jobSubmitDealer = new JobSubmitDealer("127.0.0.1:8099", null, applicationContext);
            jobSubmitDealer.run();
        } catch (Exception e) {
            Assert.assertEquals("priorityQueue must not null.", e.getMessage());
        }
    }

    @Test
    public void testCheckJobSubmitExpired(){
        try {
            JobSubmitDealer jobSubmitDealer = new JobSubmitDealer("127.0.0.1:8099", GroupPriorityQueue.builder(), applicationContext);
            JobClient jobClient = CommonUtils.getJobClient();
            jobClient.setSubmitExpiredTime(1L);
            Method checkJobSubmitExpired = jobSubmitDealer.getClass().getDeclaredMethod("checkJobSubmitExpired", JobClient.class);
            checkJobSubmitExpired.setAccessible(Boolean.TRUE);
            Boolean expired = (Boolean) checkJobSubmitExpired.invoke(jobSubmitDealer, jobClient);
            System.out.println(expired);
        } catch (Exception e) {
            fail();
        }

    }

}
