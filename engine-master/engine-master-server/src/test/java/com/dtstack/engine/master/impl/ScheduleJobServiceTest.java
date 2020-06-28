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
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ChartDataVO;
import com.dtstack.engine.api.vo.ChartMetaDataVO;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.ScheduleJobChartVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.SchedulePeriodInfoVO;
import com.dtstack.engine.api.vo.ScheduleRunDetailVO;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.data.DataCollection;
import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
                String first = jobTopOrderVOS.get(i - 1).getRunTime().replaceAll("秒", "").replaceAll("小时", "").replaceAll("分钟", "").trim();
                String second = jobTopOrderVOS.get(i).getRunTime().replaceAll("秒", "").replaceAll("小时", "").replaceAll("分钟", "").trim();
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

    @Test
    @Transactional
    @Rollback
    public void testGetJobGraph() {
        ScheduleJob todayJob = dataCollection.getScheduleJobTodayData();
        ScheduleJob yesterdayJob = dataCollection.getScheduleJobYesterdayData();

        Long projectId = todayJob.getProjectId();
        Long tenantId = todayJob.getTenantId();
        Integer appType = todayJob.getAppType();
        Long dtuicTenantId = todayJob.getDtuicTenantId();

        ScheduleJobChartVO jobGraph = sheduleJobService.getJobGraph(projectId, tenantId, appType, dtuicTenantId);
        List<ChartMetaDataVO> y = jobGraph.getY();
        ChartMetaDataVO today = y.get(0);
        ChartMetaDataVO yesterday = y.get(1);

        long todaySum = today.getData().stream().mapToLong((Object i) -> (Long) i).filter(i -> i > 0).count();
        long yesSum = yesterday.getData().stream().mapToLong((Object i) -> (Long) i).filter(i -> i > 0).count();

        Assert.assertTrue(todaySum == yesSum && todaySum == 1);
    }


    @Test
    @Transactional
    @Rollback
    public void testGetScienceJobGraph() {
        ScheduleJob todayJob = dataCollection.getScheduleJobTodayData();
        ScheduleTaskShade scheduleTaskShadeForSheduleJob = dataCollection.getScheduleTaskShadeForSheduleJob();
        Long projectId = todayJob.getProjectId();
        Long tenantId = todayJob.getTenantId();
        String taskType = todayJob.getTaskType().toString();

        ChartDataVO scienceJobGraph = sheduleJobService.getScienceJobGraph(projectId, tenantId, taskType);
        ChartMetaDataVO totalCnt = scienceJobGraph.getY().get(0);
        ChartMetaDataVO successCnt = scienceJobGraph.getY().get(1);

        long totalCntNum = totalCnt.getData().stream().mapToLong((Object i) -> (Long) i).filter(i -> i > 0).count();
        long successCntNum = successCnt.getData().stream().mapToLong((Object i) -> (Long) i).filter(i -> i > 0).count();

        Assert.assertTrue(successCntNum == totalCntNum && totalCntNum == 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testCountScienceJobStatus() {
        ScheduleJob job = dataCollection.getScheduleJobTodayData();
        ScheduleTaskShade scheduleTaskShadeForSheduleJob = dataCollection.getScheduleTaskShadeForSheduleJob();

        List<Long> projectId = Arrays.asList(job.getProjectId());
        Long tenantId = job.getTenantId();
        Integer status = job.getStatus();
        Integer type = job.getType();
        String taskType = job.getTaskType().toString();
        String cycTime = job.getCycTime();

        Map<String, Object> stringObjectMap = sheduleJobService.countScienceJobStatus(projectId, tenantId, status, type, taskType, cycTime, cycTime);
        Integer total = Integer.valueOf(stringObjectMap.get("total").toString());
        Assert.assertTrue(total == 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testQueryJobs() {
        ScheduleJob job = dataCollection.getScheduleJobTodayData();
        QueryJobDTO queryJobDTO = new QueryJobDTO();
        queryJobDTO.setAppType(job.getAppType());
        queryJobDTO.setProjectId(job.getProjectId());
        queryJobDTO.setTenantId(job.getTenantId());
        queryJobDTO.setTaskId(job.getTaskId());
        queryJobDTO.setBizStartDay(job.getExecStartTime().getTime());
        queryJobDTO.setBizEndDay(job.getExecEndTime().getTime());

        try {
            PageResult<List<ScheduleJobVO>> listPageResult = sheduleJobService.queryJobs(queryJobDTO);
            int totalCount = listPageResult.getTotalCount();
            Assert.assertTrue(totalCount == 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testDisplayPeriods() {
        ScheduleJob job = dataCollection.getScheduleJobTodayData();
        long jobId = Long.valueOf(job.getId());

        try {
            List<SchedulePeriodInfoVO> schedulePeriodInfoVOS = sheduleJobService.displayPeriods(false, jobId, job.getProjectId(), 10);
            Assert.assertTrue(schedulePeriodInfoVOS.size() == 1 && schedulePeriodInfoVOS.get(0).getTaskId().equals(job.getTaskId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testJobDetail() {
        ScheduleTaskShade taskShade = dataCollection.getScheduleTaskShadeDefiniteTaskId();
        List<ScheduleRunDetailVO> scheduleRunDetailVOS = sheduleJobService.jobDetail(taskShade.getTaskId(), taskShade.getAppType());
        ScheduleRunDetailVO scheduleRunDetailVO = scheduleRunDetailVOS.get(0);
        Assert.assertEquals(scheduleRunDetailVO.getTaskName(), taskShade.getName());
    }


}
