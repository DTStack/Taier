/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.data.DataCollection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Date: 2020/6/4
 * Company: www.dtstack.com
 * @author maqi
 */
public class ScheduleJobServiceTest extends AbstractTest {
    @Autowired
    ScheduleJobService sheduleJobService;

    @Autowired
    public DataCollection dataCollection;

    @Test
    @Transactional
    @Rollback
    public void testGetStatusById() {
        ScheduleJob scheduleJob = dataCollection.getScheduleJobFirst();
        Long id = scheduleJob.getId();
        Integer statusById = sheduleJobService.getStatusById(id);
        if (!Objects.isNull(statusById)) {
            Assert.assertTrue(statusById.intValue() == 5);
        }
    }


    @Test
    @Transactional
    @Rollback
    public void testGetStatusJobList() {
        ScheduleJob scheduleJob = dataCollection.getScheduleJobFirst();
        Long projectId = scheduleJob.getProjectId();
        Long tenantId = scheduleJob.getTenantId();
        Integer appType = scheduleJob.getAppType();
        Long dtuicTenantId = scheduleJob.getDtuicTenantId();
        Integer status = scheduleJob.getStatus();
        PageResult result = sheduleJobService.getStatusJobList(projectId, tenantId, appType, dtuicTenantId, status, 10, 1);
        if (!Objects.isNull(result)) {
            Assert.assertTrue(result.getTotalCount() == 1);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testGetStatusCount() {
        ScheduleJob scheduleJob = dataCollection.getScheduleJobFirst();
        Long projectId = scheduleJob.getProjectId();
        Long tenantId = scheduleJob.getTenantId();
        Integer appType = scheduleJob.getAppType();
        Long dtuicTenantId = scheduleJob.getDtuicTenantId();

        JSONObject statusCount = sheduleJobService.getStatusCount(projectId, tenantId, appType, dtuicTenantId);
        if (!Objects.isNull(statusCount)) {
            String all = statusCount.getString("ALL");
            Assert.assertTrue(Integer.valueOf(all) == 1);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testGetJobById() {
        ScheduleJob scheduleJob = dataCollection.getScheduleJobFirst();
        Long id = scheduleJob.getId();
        ScheduleJob job = sheduleJobService.getJobById(id);
        Assert.assertEquals(job.getJobName(), "Python");
    }

    @Test
    @Transactional
    @Rollback
    public void testRunTimeTopOrder() {
        ScheduleJob scheduleJob = dataCollection.getScheduleJobFirst();
        ScheduleJob scheduleJobSecond = dataCollection.getScheduleJobSecond();

        Long projectId = scheduleJob.getProjectId();
        Integer appType = scheduleJob.getAppType();
        Long dtuicTenantId = scheduleJob.getDtuicTenantId();

        List<JobTopOrderVO> jobTopOrderVOS = sheduleJobService.runTimeTopOrder(projectId, null, null, appType, dtuicTenantId);
        int size = jobTopOrderVOS.size();
        if (size > 1) {
            for (int i = 1; i < jobTopOrderVOS.size(); i++) {
                String first = jobTopOrderVOS.get(i - 1).getRunTime().replace("秒", "").trim();
                String second = jobTopOrderVOS.get(i).getRunTime().replace("秒", "").trim();
                Assert.assertTrue(Long.valueOf(first) >= Long.valueOf(second));
            }
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testErrorTopOrder() {
        ScheduleJob scheduleJob = dataCollection.getScheduleJobFirst();
        ScheduleJob scheduleJobSecond = dataCollection.getScheduleJobSecond();

        Long projectId = scheduleJob.getProjectId();
        Long tenantId = scheduleJob.getTenantId();
        Integer appType = scheduleJob.getAppType();
        Long dtuicTenantId = scheduleJob.getDtuicTenantId();

        List<JobTopErrorVO> jobTopErrorVOS = sheduleJobService.errorTopOrder(projectId, tenantId, appType, dtuicTenantId);
        int size = jobTopErrorVOS.size();
        if (size > 1) {
            for (int i = 1; i < jobTopErrorVOS.size(); i++) {
                Long pre = Long.valueOf(jobTopErrorVOS.get(i - 1).getErrorCount());
                Long last = Long.valueOf(jobTopErrorVOS.get(i).getErrorCount());
                Assert.assertTrue(pre >= last);
            }
        }
    }


}
