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
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.common.enums.EngineType;
import org.joda.time.DateTime;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.common.enums.EDeployMode;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    private ScheduleJobDao scheduleJobDao;

    @Test
    @Transactional
    @Rollback
    public void testGetStatusById() {
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
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
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteProjectId();
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
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteProjectId();
        Long projectId = scheduleJob.getProjectId();
        Long tenantId = scheduleJob.getTenantId();
        Integer appType = scheduleJob.getAppType();
        Long dtuicTenantId = scheduleJob.getDtuicTenantId();

        ScheduleJobStatusVO statusCount = sheduleJobService.getStatusCount(projectId, tenantId, appType, dtuicTenantId);
        if (!Objects.isNull(statusCount)) {
            Integer all = statusCount.getAll();
            Assert.assertTrue(all == 1);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testGetJobById() {
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
        Long id = scheduleJob.getId();
        ScheduleJob job = sheduleJobService.getJobById(id);
        Assert.assertEquals(job.getJobName(), "Python");
    }

    @Test
    @Transactional
    @Rollback
    public void testRunTimeTopOrder() {
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
        ScheduleJob scheduleJobSecond = DataCollection.getData().getScheduleJobSecond();

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
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
        ScheduleJob scheduleJobSecond = DataCollection.getData().getScheduleJobSecond();

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
        ScheduleJob todayJob = DataCollection.getData().getScheduleJobTodayData();
        ScheduleJob yesterdayJob = DataCollection.getData().getScheduleJobYesterdayData();

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
        ScheduleJob todayJob = DataCollection.getData().getScheduleJobTodayData();
        ScheduleTaskShade taskShade = DataCollection.getData().getScheduleTaskShadeForSheduleJob();
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
        ScheduleJob job = DataCollection.getData().getScheduleJobTodayData();
        ScheduleTaskShade scheduleTaskShadeForSheduleJob = DataCollection.getData().getScheduleTaskShadeForSheduleJob();

        List<Long> projectId = Arrays.asList(job.getProjectId());
        Long tenantId = job.getTenantId();
        Integer status = job.getStatus();
        Integer type = job.getType();
        String taskType = job.getTaskType().toString();
        String startTime = job.getCycTime();
        String endTime = job.getCycTime();

        ScheduleJobScienceJobStatusVO scheduleJobScienceJobStatusVO = sheduleJobService.countScienceJobStatus(projectId, tenantId, status, type, taskType, startTime, endTime);
        Integer total = scheduleJobScienceJobStatusVO.getTotal();
        Assert.assertTrue(total == 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testQueryJobs() {
        ScheduleJob job = DataCollection.getData().getScheduleJobTodayData();
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
        ScheduleJob job = DataCollection.getData().getScheduleJobWithCycTime();
        long jobId = Long.valueOf(job.getId());

        try {
            ScheduleJob afterJob = job;
            afterJob.setJobId("testAfter");
            afterJob.setJobKey("testAfterkey");
            afterJob.setCycTime(DateTime.now().toString("yyyy-mm-dd HH:mm:ss"));
            scheduleJobDao.insert(afterJob);
            List<SchedulePeriodInfoVO> schedulePeriodInfoVOS = sheduleJobService.displayPeriods(true, jobId, job.getProjectId(), 10);
            Assert.assertNotNull(schedulePeriodInfoVOS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testJobDetail() {
        ScheduleTaskShade taskShade = DataCollection.getData().getScheduleTaskShadeDefiniteTaskIdSecond();
        List<ScheduleRunDetailVO> scheduleRunDetailVOS = sheduleJobService.jobDetail(taskShade.getTaskId(), taskShade.getAppType());
        ScheduleRunDetailVO scheduleRunDetailVO = scheduleRunDetailVOS.get(0);
        Assert.assertEquals(scheduleRunDetailVO.getTaskName(), taskShade.getName());
    }

    @Test
    @Transactional
    @Rollback
    public void testParseDeployTypeByTaskParams() {
        EDeployMode eDeployMode = sheduleJobService.parseDeployTypeByTaskParams("flinkTaskRunMode=session",0, EngineType.Flink.name());
        Assert.assertEquals(eDeployMode, EDeployMode.SESSION);
    }

    @Test
    @Transactional
    @Rollback
    public void testStopJob() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();
        String result = sheduleJobService.stopJob(runningJob.getId(),runningJob.getAppType());

        Assert.assertEquals(result, "");
    }

    @Test
    @Transactional
    @Rollback
    public void testStopJobByJobId() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();
        String result = sheduleJobService.stopJobByJobId(runningJob.getJobId(),runningJob.getAppType());

        Assert.assertEquals(result, "");
    }

    @Test
    @Transactional
    @Rollback
    public void testStopFillDataJobs() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();

        String fillDataJobName = "Python";
        sheduleJobService.stopFillDataJobs(fillDataJobName, runningJob.getProjectId(), runningJob.getDtuicTenantId(), runningJob.getAppType());
    }


    @Test
    @Transactional
    @Rollback
    public void testBatchStopJobs() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();

        int count = sheduleJobService.batchStopJobs(Arrays.asList(runningJob.getId()));
        Assert.assertEquals(count, 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testListJobIdByTaskNameAndStatusList() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteTaskId();
        ScheduleTaskShade task = DataCollection.getData().getScheduleTaskShadeDefiniteTaskId();

        List<String> list = sheduleJobService.listJobIdByTaskNameAndStatusList(task.getName(), Arrays.asList(job.getStatus()), task.getProjectId(), task.getAppType());
        Assert.assertEquals(list.size(), 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testStatisticsTaskRecentInfo() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteTaskId();
        List<Map<String, Object>> taskInfos = sheduleJobService.statisticsTaskRecentInfo(job.getTaskId(), job.getAppType(), job.getProjectId(), 1);

        Assert.assertEquals(taskInfos.size(), 1);

        Map<String, Object> info = taskInfos.get(0);
        String jobId = info.get("jobId").toString();

        Assert.assertEquals(jobId, job.getJobId());
    }

    @Test
    @Transactional
    @Rollback
    public void testBatchJobsBatchUpdate() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteJobId();
        job.setJobName("test_batchJobsBatchUpdate");

        List<ScheduleJob> jobList = new ArrayList<>();
        jobList.add(job);

        String jobString = JSONObject.toJSONString(jobList);
        Integer result = sheduleJobService.BatchJobsBatchUpdate(jobString);

        Assert.assertEquals(result.intValue(), 1);

        Long jobId = job.getId();
        ScheduleJob scheduleJob = sheduleJobService.getJobById(jobId);
        Assert.assertEquals(scheduleJob.getJobName(), "test_batchJobsBatchUpdate");
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateTimeNull() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteJobId();
        String jobId = job.getJobId();
        Long id = job.getId();

        sheduleJobService.updateTimeNull(jobId);

        ScheduleJob scheduleJob = sheduleJobService.getJobById(id);
        Timestamp execStartTime = scheduleJob.getExecStartTime();
        Timestamp execEndTime = scheduleJob.getExecEndTime();

        Assert.assertTrue(execStartTime == null && execEndTime == null);
    }

    @Test
    @Transactional
    @Rollback
    public void testGetById() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteJobId();
        Long id = job.getId();

        ScheduleJob scheduleJob = sheduleJobService.getById(id);
        Assert.assertEquals(scheduleJob.getJobName(), job.getJobName());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetByJobId() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteJobId();
        String jobId = job.getJobId();

        ScheduleJob scheduleJob = sheduleJobService.getByJobId(jobId, 0);
        Assert.assertEquals(scheduleJob.getJobName(), job.getJobName());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetByIds() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteJobId();
        Long id = job.getId();
        Long projectId = job.getProjectId();
        List<Long> ids = Arrays.asList(id);

        List<ScheduleJob> scheduleJobs = sheduleJobService.getByIds(ids);
        Assert.assertEquals(scheduleJobs.size(), 1);

        ScheduleJob scheduleJob = scheduleJobs.get(0);
        Assert.assertEquals(scheduleJob.getJobName(), job.getJobName());
    }

    @Test
    @Transactional
    @Rollback
    public void testgetSameDayChildJob(){
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobkey();
        String scheduleJobJson=JSONObject.toJSONString(scheduleJob);;
        Integer appType = scheduleJob.getAppType();

        List<ScheduleJob> sameDayChildJob = sheduleJobService.getSameDayChildJob(scheduleJobJson, true, appType);
        Assert.assertEquals(sameDayChildJob.size(),1);

        ScheduleJob scheduleJobQuery = sameDayChildJob.get(0);
        Assert.assertEquals(scheduleJobQuery.getJobName(), scheduleJob.getJobName());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetAllChildJobWithSameDay(){
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobkey();
        Integer appType = scheduleJob.getAppType();

        List<ScheduleJob> allChildJobWithSameDay = sheduleJobService.getAllChildJobWithSameDay(scheduleJob, true, appType,10);
        Assert.assertEquals(allChildJobWithSameDay.size(),1);

        ScheduleJob scheduleJobQuery = allChildJobWithSameDay.get(0);
        Assert.assertEquals(scheduleJobQuery.getJobName(), scheduleJob.getJobName());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetLastSuccessJob(){
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobSetCycTime();
        Long taskId = scheduleJob.getTaskId();
        Integer appType = scheduleJob.getAppType();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ScheduleJob lastSuccessJob = sheduleJobService.getLastSuccessJob(taskId, timestamp, appType);

        Assert.assertEquals(lastSuccessJob.getJobName(), scheduleJob.getJobName());
    }






}
