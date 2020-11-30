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
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.master.utils.TaskParamsUtil;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.vo.ScheduleJobVO;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * Date: 2020/6/4
 * Company: www.dtstack.com
 * @author maqi
 */
public class ScheduleJobServiceTest extends AbstractTest {
    @Autowired
    ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @Test
    @Transactional
    @Rollback
    public void testGetStatusById() {
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
        Long id = scheduleJob.getId();
        Integer statusById = scheduleJobService.getStatusById(id);
        if (null != statusById) {
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
        PageResult result = scheduleJobService.getStatusJobList(projectId, tenantId, appType, dtuicTenantId, status, 10, 1);
        if (null != result) {
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

        ScheduleJobStatusVO statusCount = scheduleJobService.getStatusCount(projectId, tenantId, appType, dtuicTenantId);
        if (null != statusCount) {
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
        ScheduleJob job = scheduleJobService.getJobById(id);
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

        List<JobTopOrderVO> jobTopOrderVOS = scheduleJobService.runTimeTopOrder(projectId, null, null, appType, dtuicTenantId);
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

        List<JobTopErrorVO> jobTopErrorVOS = scheduleJobService.errorTopOrder(projectId, tenantId, appType, dtuicTenantId);
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

        ScheduleJobChartVO jobGraph = scheduleJobService.getJobGraph(projectId, tenantId, appType, dtuicTenantId);
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

        ChartDataVO scienceJobGraph = scheduleJobService.getScienceJobGraph(projectId, tenantId, taskType);
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

        ScheduleJobScienceJobStatusVO scheduleJobScienceJobStatusVO = scheduleJobService.countScienceJobStatus(projectId, tenantId, status, type, taskType, startTime, endTime);
        Integer total = scheduleJobScienceJobStatusVO.getTotal();
        Assert.assertTrue(total == 1);
    }


    @Test
    @Transactional
    @Rollback
    public void testDisplayPeriods() {
        ScheduleJob job = DataCollection.getData().getScheduleJobWithCycTime();
        long jobId = Long.valueOf(job.getId());

        try {
            List<SchedulePeriodInfoVO> schedulePeriodInfoVOS = scheduleJobService.displayPeriods(true, jobId, job.getProjectId(), 10);
            Assert.assertTrue(schedulePeriodInfoVOS.size() == 1 && schedulePeriodInfoVOS.get(0).getTaskId().equals(job.getTaskId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testJobDetail() {
        ScheduleTaskShade taskShade = DataCollection.getData().getScheduleTaskShadeDefiniteTaskIdSecond();
        List<ScheduleRunDetailVO> scheduleRunDetailVOS = scheduleJobService.jobDetail(taskShade.getTaskId(), taskShade.getAppType());
        ScheduleRunDetailVO scheduleRunDetailVO = scheduleRunDetailVOS.get(0);
        Assert.assertEquals(scheduleRunDetailVO.getTaskName(), taskShade.getName());
    }

    @Test
    @Transactional
    @Rollback
    public void testParseDeployTypeByTaskParams() {
        EDeployMode eDeployMode = TaskParamsUtil.parseDeployTypeByTaskParams("flinkTaskRunMode=session",0);
        Assert.assertEquals(eDeployMode, EDeployMode.SESSION);
    }

    @Test
    @Transactional
    @Rollback
    public void testStopJob() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();
        String result = scheduleJobService.stopJob(runningJob.getId(), runningJob.getCreateUserId(), runningJob.getProjectId(), runningJob.getTenantId(),
                runningJob.getDtuicTenantId(), true, runningJob.getAppType());

        Assert.assertEquals(result, "");
    }

    @Test
    @Transactional
    @Rollback
    public void testStopJobByJobId() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();
        String result = scheduleJobService.stopJobByJobId(runningJob.getJobId(), runningJob.getCreateUserId(), runningJob.getProjectId(), runningJob.getTenantId(),
                runningJob.getDtuicTenantId(), true, runningJob.getAppType());

        Assert.assertEquals(result, "");
    }

    @Test
    @Transactional
    @Rollback
    public void testStopFillDataJobs() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();

        String fillDataJobName = "Python";
        scheduleJobService.stopFillDataJobs(fillDataJobName, runningJob.getProjectId(), runningJob.getDtuicTenantId(), runningJob.getAppType());
    }


    @Test
    @Transactional
    @Rollback
    public void testBatchStopJobs() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();

        int count = scheduleJobService.batchStopJobs(Arrays.asList(runningJob.getId()), runningJob.getProjectId(), runningJob.getDtuicTenantId(), runningJob.getAppType());
        Assert.assertEquals(count, 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testListJobIdByTaskNameAndStatusList() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteTaskId();
        ScheduleTaskShade task = DataCollection.getData().getScheduleTaskShadeDefiniteTaskId();

        List<String> list = scheduleJobService.listJobIdByTaskNameAndStatusList(task.getName(), Arrays.asList(job.getStatus()), task.getProjectId(), task.getAppType());
        Assert.assertEquals(list.size(), 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testStatisticsTaskRecentInfo() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteTaskId();
        List<Map<String, Object>> taskInfos = scheduleJobService.statisticsTaskRecentInfo(job.getTaskId(), job.getAppType(), job.getProjectId(), 1);

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
        Integer result = scheduleJobService.BatchJobsBatchUpdate(jobString);

        Assert.assertEquals(result.intValue(), 1);

        Long jobId = job.getId();
        ScheduleJob scheduleJob = scheduleJobService.getJobById(jobId);
        Assert.assertEquals(scheduleJob.getJobName(), "test_batchJobsBatchUpdate");
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateTimeNull() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteJobId();
        String jobId = job.getJobId();
        Long id = job.getId();

        scheduleJobService.updateTimeNull(jobId);

        ScheduleJob scheduleJob = scheduleJobService.getJobById(id);
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

        ScheduleJob scheduleJob = scheduleJobService.getById(id);
        Assert.assertEquals(scheduleJob.getJobName(), job.getJobName());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetByJobId() {
        ScheduleJob job = DataCollection.getData().getScheduleJobDefiniteJobId();
        String jobId = job.getJobId();

        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId, 0);
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

        List<ScheduleJob> scheduleJobs = scheduleJobService.getByIds(ids, projectId);
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

        List<ScheduleJob> sameDayChildJob = scheduleJobService.getSameDayChildJob(scheduleJobJson, true, appType);
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

        List<ScheduleJob> allChildJobWithSameDay = scheduleJobService.getAllChildJobWithSameDay(scheduleJob, true, appType);
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
        ScheduleJob lastSuccessJob = scheduleJobService.getLastSuccessJob(taskId, timestamp, appType);

        Assert.assertEquals(lastSuccessJob.getJobName(), scheduleJob.getJobName());
    }




    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQuery(){
        //创建工作流任务
        buildTaskTaskData();
        long projectId = 10L;
        long userId = 10L;
        long tenant = 10L;
        Integer appType = 1;
        Long dtuicTenantId = 10L;
        long runDay = 1606406400L;
        long toDay = 1606492799L;
        long flowTaskId  = 471L;
        String fillName = "P_123_2020_11_28_17_41";
        //{"fillName":"P_123_2020_11_28_17_41","taskJson":"[{\"task\":165}]","fromDay":1606406400,"toDay":1606492799}
        try {
            //补数据
            scheduleJobService.fillTaskData("[{\"task\":471}]",fillName, runDay,toDay,null,null,projectId,userId,tenant, true,appType,dtuicTenantId);
            //查询工作流外部
            PageResult<List<ScheduleFillDataJobPreViewVO>> fillDataJobInfoPreview = scheduleJobService.getFillDataJobInfoPreview("", null, null, null, null, projectId, appType, null, 1, 20, tenant);
            Assert.assertNotNull(fillDataJobInfoPreview);
            Assert.assertNotNull(fillDataJobInfoPreview.getData());
            Assert.assertTrue(fillDataJobInfoPreview.getData().stream().anyMatch(f -> f.getFillDataJobName().contains(fillName)));
            //查询工作流详情
            PageResult<ScheduleFillDataJobDetailVO> fuzzy = scheduleJobService.getFillDataDetailInfo("{}", null, fillName
                    , null, "fuzzy", appType);
            Assert.assertNotNull(fuzzy);
            Assert.assertNotNull(fuzzy.getData());
            Assert.assertNotNull(fuzzy.getData().getRecordList());
            Optional<ScheduleFillDataJobDetailVO.FillDataRecord> fillJob = fuzzy.getData()
                    .getRecordList()
                    .stream().filter(job -> job.getTaskType().equals(EScheduleJobType.WORK_FLOW.getType())).findFirst();
            Assert.assertTrue(fillJob.isPresent());
            ScheduleFillDataJobDetailVO.FillDataRecord fillDataRecord = fillJob.get();
            //展开下游
            ScheduleFillDataJobDetailVO.FillDataRecord relatedJobsForFillData = scheduleJobService.getRelatedJobsForFillData(fillDataRecord.getJobId(), "{}", fillName);
            Assert.assertNotNull(relatedJobsForFillData);
            Assert.assertTrue(CollectionUtils.isNotEmpty(relatedJobsForFillData.getRelatedRecords()));
            QueryJobDTO queryJobDTO = new QueryJobDTO();
            queryJobDTO.setAppType(appType);
            queryJobDTO.setFillTaskName(fillName);
            queryJobDTO.setSearchType("fuzzy");
            queryJobDTO.setType(EScheduleType.FILL_DATA.getType());
            //数量统计
            Map<String, Long> countStatus = scheduleJobService.queryJobsStatusStatistics(queryJobDTO);
            Assert.assertNotNull(countStatus);
            int sum = countStatus.values().stream().mapToInt(Long::intValue).sum();
            Assert.assertTrue(sum > 0) ;
            ScheduleJobVO relatedJobs = scheduleJobService.getRelatedJobs(relatedJobsForFillData.getJobId(), "{}");
            Assert.assertNotNull(relatedJobs);
            Assert.assertNotNull(relatedJobs.getRelatedJobs());
            List<RestartJobVO> restartChildJob = scheduleJobService.getRestartChildJob(relatedJobsForFillData.getJobId(), flowTaskId, true);
            List<ScheduleJob> scheduleJobs = scheduleJobService.syncBatchJob(queryJobDTO);
            Assert.assertNotNull(scheduleJobs);
            Assert.assertNotNull(scheduleJobService.getSubJobsAndStatusByFlowId(relatedJobsForFillData.getJobId()));
            ScheduleJob flowJob = scheduleJobService.getByJobId(relatedJobsForFillData.getJobId(), null);
            Assert.assertNotNull(flowJob);
            Assert.assertNotNull(scheduleJobService.getById(flowJob.getId()));
            scheduleJobService.getAllChildJobWithSameDay(flowJob,false,appType);
            Assert.assertTrue(StringUtils.isBlank(scheduleJobService.getJobGraphJSON(flowJob.getJobId())));
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    private void buildTaskTaskData() {
        //插入工作流数据
        ScheduleTaskShade parentTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
        parentTaskShadeTemplate.setTaskId(471L);
        parentTaskShadeTemplate.setAppType(1);
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);

        //顶节点
        parentTaskShadeTemplate.setFlowId(parentTaskShadeTemplate.getTaskId());
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.VIRTUAL.getType());
        parentTaskShadeTemplate.setName("virtual");
        parentTaskShadeTemplate.setTaskId(499L);
        parentTaskShadeTemplate.setAppType(1);
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);

        //子节点
        parentTaskShadeTemplate.setFlowId(parentTaskShadeTemplate.getTaskId());
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.SHELL.getType());
        parentTaskShadeTemplate.setName("first");
        parentTaskShadeTemplate.setTaskId(525L);
        parentTaskShadeTemplate.setAppType(1);
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);

        //子节点
        parentTaskShadeTemplate.setFlowId(parentTaskShadeTemplate.getTaskId());
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.SHELL.getType());
        parentTaskShadeTemplate.setName("second");
        parentTaskShadeTemplate.setTaskId(600L);
        parentTaskShadeTemplate.setAppType(1);
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);



        //插入关系
        String taskTaskStr = "[{\"appType\":1,\"dtuicTenantId\":1,\"gmtCreate\":1606101569150,\"gmtModified\":1606101569150,\"id\":233,\"isDeleted\":0,\"parentTaskId\":499,\"projectId\":3,\"taskId\":525,\"tenantId\":1}," +
                "{\"appType\":1,\"dtuicTenantId\":1,\"gmtCreate\":1606101569150,\"gmtModified\":1606101569150,\"id\":233,\"isDeleted\":0,\"parentTaskId\":525,\"projectId\":3,\"taskId\":600,\"tenantId\":1}," +
                "]";
        scheduleTaskTaskShadeService.saveTaskTaskList(taskTaskStr);
        List<ScheduleTaskTaskShade> allParentTask = scheduleTaskTaskShadeService.getAllParentTask(525L, 1);
        Assert.assertNotNull(allParentTask);
    }


}
