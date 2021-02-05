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
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import org.joda.time.DateTime;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.multiengine.engine.HadoopJobStartTrigger;
import com.dtstack.engine.master.utils.TaskParamsUtil;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.vo.ScheduleJobVO;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

    @MockBean
    private HadoopJobStartTrigger hadoopJobStartTrigger;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Before
    public void init() throws Exception {
        doNothing().when(hadoopJobStartTrigger).readyForTaskStartTrigger(any(),any(),any());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetStatusById() {
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
        Long id = scheduleJob.getId();
        Integer statusById = scheduleJobService.getJobStatus(scheduleJob.getJobId());
        if (null != statusById) {
            Assert.assertFalse(statusById != 5);
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
        scheduleJobService.getStatusJobList(projectId, tenantId, appType, dtuicTenantId, status, 10, 1);
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
            ScheduleJob afterJob = job;
            afterJob.setJobId("testAfter");
            afterJob.setJobKey("testAfterkey");
            afterJob.setCycTime(DateTime.now().toString("yyyy-mm-dd HH:mm:ss"));
            scheduleJobDao.insert(afterJob);
            List<SchedulePeriodInfoVO> schedulePeriodInfoVOS = scheduleJobService.displayPeriods(true, jobId, job.getProjectId(), 10);
            Assert.assertTrue(schedulePeriodInfoVOS.size() > 1 && schedulePeriodInfoVOS.get(0).getTaskId().equals(job.getTaskId()));
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
        String result = scheduleJobService.stopJob(runningJob.getId(),runningJob.getAppType());

        Assert.assertEquals(result, "");
    }

    @Test
    @Transactional
    @Rollback
    public void testStopJobByJobId() throws Exception {
        ScheduleJob runningJob = DataCollection.getData().getScheduleJobDefiniteTaskId();
        String result = scheduleJobService.stopJobByJobId(runningJob.getJobId(),runningJob.getAppType());
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

        int count = scheduleJobService.batchStopJobs(Arrays.asList(runningJob.getId()));
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

        List<ScheduleJob> scheduleJobs = scheduleJobService.getByIds(ids);
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
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setTaskId(scheduleJob.getTaskId() + 1);
        scheduleTaskShade.setName("hbase_hdfs");
        scheduleTaskShade.setTaskType(2);
        scheduleTaskShade.setEngineType(0);
        scheduleTaskShade.setComputeType(1);
        scheduleTaskShade.setTaskParams("## 任务运行方式：\n" +
                "## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步\n" +
                "## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session\n" +
                "flinkTaskRunMode=per_job\n" +
                "## per_job模式下jobManager配置的内存大小，默认1024（单位M)\n" +
                "## jobmanager.memory.mb=1024\n" +
                "## per_job模式下taskManager配置的内存大小，默认1024（单位M）\n" +
                "## taskmanager.memory.mb=1024\n" +
                "## per_job模式下每个taskManager 对应 slot的数量\n" +
                "## slots=1\n" +
                "## checkpoint保存时间间隔\n" +
                "## flink.checkpoint.interval=300000\n" +
                "## 任务优先级, 范围:1-1000\n" +
                "## job.priority=10");
        scheduleTaskShade.setScheduleConf("{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
        scheduleTaskShade.setPeriodType(2);
        scheduleTaskShade.setScheduleStatus(1);
        scheduleTaskShade.setProjectScheduleStatus(0);
        scheduleTaskShade.setAppType(scheduleJob.getAppType());
        scheduleTaskShadeDao.insert(scheduleTaskShade);

        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setJobId("testSage");
        scheduleJobTemplate.setProjectId(scheduleJob.getProjectId());
        scheduleJobTemplate.setTaskId(scheduleJob.getTaskId() +1);
        scheduleJobTemplate.setTenantId(scheduleJob.getTenantId());
        scheduleJobTemplate.setDtuicTenantId(scheduleJob.getDtuicTenantId());
        scheduleJobTemplate.setJobKey("cronTrigger_941_20200729000000");
        scheduleJobTemplate.setJobName("cronJob_hbase_hdfs_20200729000000");
        scheduleJobTemplate.setType(0);
        scheduleJobTemplate.setBusinessDate("20200729000000");
        scheduleJobTemplate.setCycTime("20200730000000");
        scheduleJobTemplate.setDependencyType(0);
        scheduleJobTemplate.setStatus(0);
        scheduleJobTemplate.setTaskType(0);
        scheduleJobTemplate.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleJobTemplate.setGmtModified(new Timestamp(System.currentTimeMillis()));
        scheduleJobTemplate.setAppType(scheduleJob.getAppType());
        scheduleJobDao.insert(scheduleJobTemplate);
        ScheduleJobJob jobJob = new ScheduleJobJob();
        jobJob.setParentJobKey(scheduleJob.getJobKey());
        jobJob.setJobKey(scheduleJobTemplate.getJobKey());
        jobJob.setAppType(scheduleJobTemplate.getAppType());
        jobJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        jobJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        jobJob.setTenantId(scheduleJob.getTenantId());
        jobJob.setDtuicTenantId(scheduleJob.getDtuicTenantId());
        jobJob.setProjectId(scheduleJob.getProjectId());
        scheduleJobJobDao.insert(jobJob);
        List<ScheduleJob> sameDayChildJob = scheduleJobService.getSameDayChildJob(scheduleJobJson, true, appType);
        Assert.assertEquals(true, sameDayChildJob.size() > 0);

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
            PageResult<List<ScheduleFillDataJobPreViewVO>> fillDataJobInfoPreview = scheduleJobService.getFillDataJobInfoPreview("", null, null, null, null, projectId, appType, 1, 20, tenant);
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
            scheduleJobService.getRestartChildJob(relatedJobsForFillData.getJobId(), flowTaskId, true);
            List<ScheduleJob> scheduleJobs = scheduleJobService.syncBatchJob(queryJobDTO);
            Assert.assertNotNull(scheduleJobs);
            Assert.assertNotNull(scheduleJobService.getSubJobsAndStatusByFlowId(relatedJobsForFillData.getJobId()));
            ScheduleJob flowJob = scheduleJobService.getByJobId(relatedJobsForFillData.getJobId(), null);
            Assert.assertNotNull(flowJob);
            Assert.assertNotNull(scheduleJobService.getById(flowJob.getId()));
            scheduleJobService.getAllChildJobWithSameDay(flowJob,false,appType, 20);
            Assert.assertTrue(StringUtils.isBlank(scheduleJobService.getJobGraphJSON(flowJob.getJobId())));
            Assert.assertNotNull(scheduleJobService.listJobsByTaskIdsAndApptype(Lists.newArrayList(flowTaskId),appType));
            scheduleJobService.getLabTaskRelationMap(Lists.newArrayList(scheduleJobs.get(0).getJobId()),projectId);
            scheduleJobService.deleteJobsByJobKey(Lists.newArrayList(scheduleJobs.get(0).getJobKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetLabTaskRelationMap(){

        ScheduleJob job = DataCollection.getData().getScheduleJobFirst();
        Map<String, ScheduleJob> relationMap = scheduleJobService.getLabTaskRelationMap(Lists.newArrayList(job.getJobId()), job.getProjectId());
        Assert.assertNotNull(relationMap);
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



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testTrigger() {
        String jobId = "9ead3d66";
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setTaskId(1391L);
        scheduleTaskShade.setName("hbase_hdfs");
        scheduleTaskShade.setTaskType(2);
        scheduleTaskShade.setEngineType(0);
        scheduleTaskShade.setComputeType(1);
        scheduleTaskShade.setTaskParams("## 任务运行方式：\n" +
                "## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步\n" +
                "## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session\n" +
                "flinkTaskRunMode=per_job\n" +
                "## per_job模式下jobManager配置的内存大小，默认1024（单位M)\n" +
                "## jobmanager.memory.mb=1024\n" +
                "## per_job模式下taskManager配置的内存大小，默认1024（单位M）\n" +
                "## taskmanager.memory.mb=1024\n" +
                "## per_job模式下每个taskManager 对应 slot的数量\n" +
                "## slots=1\n" +
                "## checkpoint保存时间间隔\n" +
                "## flink.checkpoint.interval=300000\n" +
                "## 任务优先级, 范围:1-1000\n" +
                "## job.priority=10");
        scheduleTaskShade.setScheduleConf("{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
        scheduleTaskShade.setPeriodType(2);
        scheduleTaskShade.setScheduleStatus(1);
        scheduleTaskShade.setProjectScheduleStatus(0);
        scheduleTaskShadeDao.insert(scheduleTaskShade);

        scheduleTaskShadeDao.updateTaskExtInfo(scheduleTaskShade.getTaskId(),
                scheduleTaskShade.getAppType(),
                "{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"\\\"," +
                        "\\\"computeType\\\":1,\\\"engineIdentity\\\":\\\"dev2\\\",\\\"engineType\\\":\\\"flink\\\",\\\"taskParams\\\":\\\"mr.job.parallelism = 1\\\\nflinkTaskRunMode = per_job\\\\n\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1," +
                        "\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":2,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"hbase_hdfs\\\",\\\"tenantId\\\":1,\\\"job\\\":\\\"{\\\\\\\"job\\\\\\\":{\\\\\\\"content\\\\\\\":[{\\\\\\\"reader\\\\\\\":{\\\\\\\"parameter\\\\\\\":{\\\\\\\"scanCacheSize\\\\\\\":256,\\\\\\\"scanBatchSize\\\\\\\":100," +
                        "\\\\\\\"column\\\\\\\":[{\\\\\\\"name\\\\\\\":\\\\\\\"rowkey\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"STRING\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"info1:id\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"STRING\\\\\\\"}],\\\\\\\"range\\\\\\\":{\\\\\\\"endRowkey\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"isBinaryRowkey\\\\\\\":false," +
                        "\\\\\\\"startRowkey\\\\\\\":\\\\\\\"\\\\\\\"},\\\\\\\"encoding\\\\\\\":\\\\\\\"utf-8\\\\\\\",\\\\\\\"table\\\\\\\":\\\\\\\"wangchuan_test\\\\\\\",\\\\\\\"sourceIds\\\\\\\":[211],\\\\\\\"hbaseConfig\\\\\\\":{\\\\\\\"hbase.zookeeper.quorum\\\\\\\":\\\\\\\"kudu1:2181\\\\\\\"}},\\\\\\\"name\\\\\\\":\\\\\\\"hbasereader\\\\\\\"}," +
                        "\\\\\\\"writer\\\\\\\":{\\\\\\\"parameter\\\\\\\":{\\\\\\\"fileName\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"column\\\\\\\":[{\\\\\\\"name\\\\\\\":\\\\\\\"id\\\\\\\",\\\\\\\"index\\\\\\\":0,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"id\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"name\\\\\\\"," +
                        "\\\\\\\"index\\\\\\\":1,\\\\\\\"isPart\\\\\\\":false,\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"name\\\\\\\"}],\\\\\\\"writeMode\\\\\\\":\\\\\\\"overwrite\\\\\\\",\\\\\\\"fieldDelimiter\\\\\\\":\\\\\\\"\\\\\\\\u0001\\\\\\\",\\\\\\\"encoding\\\\\\\":\\\\\\\"utf-8\\\\\\\",\\\\\\\"fullColumnName\\\\\\\":[\\\\\\\"id\\\\\\\",\\\\\\\"name\\\\\\\"],\\\\\\\"path\\\\\\\":\\\\\\\"hdfs://ns1/user/hive/warehouse/dev2.db/hbasehdfs\\\\\\\",\\\\\\\"" +
                        "hadoopConfig\\\\\\\":{\\\\\\\"javax.jdo.option.ConnectionDriverName\\\\\\\":\\\\\\\"com.mysql.jdbc.Driver\\\\\\\",\\\\\\\"dfs.replication\\\\\\\":\\\\\\\"2\\\\\\\",\\\\\\\"dfs.ha.fencing.ssh.private-key-files\\\\\\\":\\\\\\\"~/.ssh/id_rsa\\\\\\\",\\\\\\\"dfs.nameservices\\\\\\\":\\\\\\\"ns1\\\\\\\",\\\\\\\"dfs.safemode.threshold.pct\\\\\\\":\\\\\\\"0.5\\\\\\\",\\\\\\\"dfs.ha.namenodes.ns1\\\\\\\":\\\\\\\"nn1,nn2\\\\\\\",\\\\" +
                        "\\\"dfs.journalnode.rpc-address\\\\\\\":\\\\\\\"0.0.0.0:8485\\\\\\\",\\\\\\\"dfs.journalnode.http-address\\\\\\\":\\\\\\\"0.0.0.0:8480\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn2\\\\\\\":\\\\\\\"kudu2:9000\\\\\\\",\\\\\\\"dfs.namenode.rpc-address.ns1.nn1\\\\\\\":\\\\\\\"kudu1:9000\\\\\\\",\\\\\\\"hive.metastore.warehouse.dir\\\\\\\":\\\\\\\"/user/hive/warehouse\\\\\\\",\\\\\\\"hive.server2.webui.host\\\\\\\":\\\\\\\"172.16.10.34\\\\\\\",\\\\\\\"" +
                        "hive.metastore.schema.verification\\\\\\\":\\\\\\\"false\\\\\\\",\\\\\\\"hive.server2.support.dynamic.service.discovery\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionPassword\\\\\\\":\\\\\\\"abc123\\\\\\\",\\\\\\\"hive.metastore.uris\\\\\\\":\\\\\\\"thrift://kudu1:9083\\\\\\\",\\\\\\\"hive.exec.dynamic.partition.mode\\\\\\\":\\\\\\\"nonstrict\\\\\\\",\\\\\\\"hadoop.proxyuser.admin.hosts\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"hive.zookeeper.quorum\\\\\\\":\\\\\\\"" +
                        "kudu1:2181,kudu2:2181,kudu3:2181\\\\\\\",\\\\\\\"ha.zookeeper.quorum\\\\\\\":\\\\\\\"kudu1:2181,kudu2:2181,kudu3:2181\\\\\\\",\\\\\\\"hive.server2.thrift.min.worker.threads\\\\\\\":\\\\\\\"200\\\\\\\",\\\\\\\"hive.server2.webui.port\\\\\\\":\\\\\\\"10002\\\\\\\",\\\\\\\"fs.defaultFS\\\\\\\":\\\\\\\"hdfs://ns1\\\\\\\",\\\\\\\"hadoop.proxyuser.admin.groups\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"dfs.ha.fencing.methods\\\\\\\":\\\\\\\"sshfence\\\\\\\",\\\\\\\"dfs.client.failover.proxy.provider.ns1\\\\\\\":\\\\\\\"" +
                        "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\\\\\\\",\\\\\\\"typeName\\\\\\\":\\\\\\\"yarn2-hdfs2-hadoop2\\\\\\\",\\\\\\\"hadoop.proxyuser.root.groups\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionURL\\\\\\\":\\\\\\\"jdbc:mysql://kudu2:3306/ide?useSSL=false\\\\\\\",\\\\\\\"dfs.qjournal.write-txns.timeout.ms\\\\\\\":\\\\\\\"60000\\\\\\\",\\\\\\\"fs.trash.interval\\\\\\\":\\\\\\\"30\\\\\\\",\\\\\\\"hadoop.proxyuser.root.hosts\\\\\\\":\\\\\\\"*\\\\\\\",\\\\\\\"dfs.namenode.shared.edits.dir\\\\\\\":\\\\\\\"" +
                        "qjournal://kudu1:8485;kudu2:8485;kudu3:8485/namenode-ha-data\\\\\\\",\\\\\\\"javax.jdo.option.ConnectionUserName\\\\\\\":\\\\\\\"dtstack\\\\\\\",\\\\\\\"hive.server2.thrift.port\\\\\\\":\\\\\\\"10000\\\\\\\",\\\\\\\"ha.zookeeper.session-timeout.ms\\\\\\\":\\\\\\\"5000\\\\\\\",\\\\\\\"hadoop.tmp.dir\\\\\\\":\\\\\\\"/data/hadoop_${user.name}\\\\\\\",\\\\\\\"dfs.journalnode.edits.dir\\\\\\\":\\\\\\\"/data/dtstack/hadoop/journal\\\\\\\",\\\\\\\"hive.server2.zookeeper.namespace\\\\\\\":\\\\\\\"hiveserver2\\\\\\\",\\\\\\\"hive.server2.enable.doAs\\\\\\\":\\\\\\\"/false\\\\\\\",\\\\\\\"" +
                        "dfs.namenode.http-address.ns1.nn2\\\\\\\":\\\\\\\"kudu2:50070\\\\\\\",\\\\\\\"dfs.namenode.http-address.ns1.nn1\\\\\\\":\\\\\\\"kudu1:50070\\\\\\\",\\\\\\\"md5zip\\\\\\\":\\\\\\\"6b4fce1ef5a7bd8e21624f8b9158a0f7\\\\\\\",\\\\\\\"hive.exec.scratchdir\\\\\\\":\\\\\\\"/user/hive/warehouse\\\\\\\",\\\\\\\"hive.server2.webui.max.threads\\\\\\\":\\\\\\\"100\\\\\\\",\\\\\\\"datanucleus.schema.autoCreateAll\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"hive.exec.dynamic.partition\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"hive.server2.thrift.bind.host\\\\\\\":\\\\\\\"kudu1\\\\\\\",\\\\\\\"dfs.ha.automatic-failover.enabled\\\\\\\":\\\\\\\"true\\\\\\\"},\\\\\\\"defaultFS\\\\\\\":\\\\\\\"" +
                        "hdfs://ns1\\\\\\\",\\\\\\\"connection\\\\\\\":[{\\\\\\\"jdbcUrl\\\\\\\":\\\\\\\"jdbc:hive2://172.16.8.107:10000/dev2\\\\\\\",\\\\\\\"table\\\\\\\":[\\\\\\\"hbasehdfs\\\\\\\"]}],\\\\\\\"fileType\\\\\\\":\\\\\\\"parquet\\\\\\\",\\\\\\\"sourceIds\\\\\\\":[13],\\\\\\\"username\\\\\\\":\\\\\\\"admin\\\\\\\",\\\\\\\"fullColumnType\\\\\\\":[\\\\\\\"string\\\\\\\",\\\\\\\"string\\\\\\\"]},\\\\\\\"name\\\\\\\":\\\\\\\"hdfswriter\\\\\\\"}}],\\\\\\\"setting\\\\\\\":{\\\\\\\"restore\\\\\\\":{\\\\\\\"maxRowNumForCheckpoint\\\\\\\":0,\\\\\\\"isRestore\\\\\\\":false,\\\\\\\"restoreColumnName\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"restoreColumnIndex\\\\\\\":0},\\\\\\\"errorLimit\\\\\\\":{\\\\\\\"record\\\\\\\":100},\\\\\\\"speed\\\\\\\":{\\\\\\\"bytes\\\\\\\":0,\\\\\\\"channel\\\\\\\":1}}}}\\\",\\\"dataSourceType\\\":7,\\\"taskId\\\":1391}\"}");

        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setJobId(jobId);
        scheduleJobTemplate.setProjectId(scheduleTaskShade.getProjectId());
        scheduleJobTemplate.setTaskId(scheduleTaskShade.getTaskId());
        scheduleJobTemplate.setTenantId(scheduleTaskShade.getTenantId());
        scheduleJobTemplate.setDtuicTenantId(scheduleTaskShade.getDtuicTenantId());
        scheduleJobTemplate.setJobKey("cronTrigger_941_20201210000000");
        scheduleJobTemplate.setJobName("cronJob_hbase_hdfs_20201210000000");
        scheduleJobTemplate.setType(0);
        scheduleJobTemplate.setBusinessDate("20201209000000");
        scheduleJobTemplate.setCycTime("20201210000000");
        scheduleJobTemplate.setDependencyType(0);
        scheduleJobTemplate.setStatus(0);
        scheduleJobTemplate.setTaskType(0);
        scheduleJobTemplate.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleJobTemplate.setGmtModified(new Timestamp(System.currentTimeMillis()));
        scheduleJobTemplate.setAppType(scheduleTaskShade.getAppType());
        ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJobTemplate);


        scheduleJobService.insertJobList(Lists.newArrayList(scheduleBatchJob), EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJobService.testCheckCanRun(jobId);
        scheduleJobService.testTrigger(jobId);

        QueryJobDTO queryJobDTO = new QueryJobDTO();
        queryJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        queryJobDTO.setTaskName("hbase_hdfs");
        queryJobDTO.setCurrentPage(1);
        queryJobDTO.setPageSize(10);
        queryJobDTO.setSearchType("fuzzy");
        queryJobDTO.setBizStartDay(1607493082L);
        queryJobDTO.setBizEndDay(1607493082L);
        queryJobDTO.setCycStartDay(1607529600L);
        queryJobDTO.setCycEndDay(1607615999L);
        queryJobDTO.setProjectId(scheduleTaskShade.getProjectId());
        queryJobDTO.setAppType(scheduleTaskShade.getAppType());
        queryJobDTO.setJobStatuses(Arrays.stream(RdosTaskStatus.values()).map(RdosTaskStatus::getStatus).map(String::valueOf).collect(Collectors.joining(",")));
        PageResult<List<com.dtstack.engine.api.vo.ScheduleJobVO>> listPageResult = null;
        try {
            listPageResult = scheduleJobService.queryJobs(queryJobDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(listPageResult);
        Assert.assertNotNull(listPageResult.getData());
        Assert.assertTrue(listPageResult.getData().stream().anyMatch(f -> f.getJobId().equalsIgnoreCase("9ead3d66")));
        Assert.assertNotNull(scheduleJobService.listByCyctimeAndJobName("2020", "cronJob_hbase_hdfs", EScheduleType.NORMAL_SCHEDULE.getType()));
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void createHourShade(){
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setTaskId(1392L);
        scheduleTaskShade.setName("test_min");
        scheduleTaskShade.setTaskType(2);
        scheduleTaskShade.setEngineType(0);
        scheduleTaskShade.setComputeType(1);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setTaskParams("## 任务运行方式：\n" +
                "## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步\n" +
                "## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session\n" +
                "flinkTaskRunMode=per_job\n" +
                "## per_job模式下jobManager配置的内存大小，默认1024（单位M)\n" +
                "## jobmanager.memory.mb=1024\n" +
                "## per_job模式下taskManager配置的内存大小，默认1024（单位M）\n" +
                "## taskmanager.memory.mb=1024\n" +
                "## per_job模式下每个taskManager 对应 slot的数量\n" +
                "## slots=1\n" +
                "## checkpoint保存时间间隔\n" +
                "## flink.checkpoint.interval=300000\n" +
                "## 任务优先级, 范围:1-1000\n" +
                "## job.priority=10");
        scheduleTaskShade.setScheduleConf("{\"beginHour\":\"0\",\"endHour\":\"23\",\"beginMin\":\"0\",\"gapHour\":\"3\",\"periodType\":\"1\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":0,\"isLastInstance\":true,\"endMin\":\"59\"}");
        scheduleTaskShade.setPeriodType(1);
        scheduleTaskShade.setScheduleStatus(1);
        scheduleTaskShade.setProjectScheduleStatus(0);
        scheduleTaskShadeDao.insert(scheduleTaskShade);
        scheduleJobService.createTodayTaskShade(scheduleTaskShade.getTaskId(),scheduleTaskShade.getAppType(),null);
        ScheduleJobDTO scheduleJobDTO = new ScheduleJobDTO();
        scheduleJobDTO.setAppType(scheduleTaskShade.getAppType());
        scheduleJobDTO.setTaskId(scheduleTaskShade.getTaskId());
        List<com.dtstack.engine.api.vo.ScheduleJobVO> scheduleJobVOS = scheduleJobService.minOrHourJobQuery(scheduleJobDTO);
        Assert.assertNotNull(scheduleJobVOS);
        Assert.assertTrue(scheduleJobVOS.stream().anyMatch(v -> v.getTaskId() == scheduleTaskShade.getTaskId()));
        ScheduleJobStatusVO statusCount = scheduleJobService.getStatusCount(scheduleTaskShade.getProjectId(), null, scheduleTaskShade.getAppType(), scheduleTaskShade.getDtuicTenantId());
        Assert.assertNotNull(statusCount);
        Integer all = statusCount.getAll();
        Assert.assertTrue(all > 1);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateStatusWithExecTime(){

        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
        Integer integer = scheduleJobService.updateStatusWithExecTime(scheduleJob);
        Assert.assertNotNull(integer);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSendTaskStartTrigger() throws Exception {

        ScheduleJob job = DataCollection.getData().getScheduleJobVirtual();
        scheduleJobService.sendTaskStartTrigger(job);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetFillDataDetailInfoOld() throws Exception {

        QueryJobDTO queryJobDTO = new QueryJobDTO();
        queryJobDTO.setBusinessDateSort("desc");
        queryJobDTO.setCurrentPage(1);
        queryJobDTO.setPageSize(20);
        queryJobDTO.setProjectId(13L);
        queryJobDTO.setSearchType("fuzzy");
        queryJobDTO.setSplitFiledFlag(true);
        queryJobDTO.setTenantId(3L);
        scheduleJobService.getFillDataDetailInfoOld(queryJobDTO,
                "P_dev2_HADOOP2hive_sdsadasd_2020_12_30_31_35",1L);
    }

    @Test
    public void testGeneralCount(){

        ScheduleJobDTO jobDTO = new ScheduleJobDTO();
        Integer integer = scheduleJobService.generalCount(jobDTO);
        Assert.assertNotNull(integer);
    }

    @Test
    public void testGeneralCountWithMinAndHour(){

        ScheduleJobDTO jobDTO = new ScheduleJobDTO();
        Integer integer = scheduleJobService.generalCountWithMinAndHour(jobDTO);
        Assert.assertNotNull(integer);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSetAlogrithmLabLog() throws Exception {

        ScheduleJob job = DataCollection.getData().getScheduleJobVirtual();
        scheduleJobService.setAlogrithmLabLog(8,14,job.getJobId(),"错误","",8);
    }

    @Test
    public void testGetLogInfoFromEngine(){

        ActionLogVO info = scheduleJobService.getLogInfoFromEngine("afaflajfla");
        Assert.assertNotNull(info);
    }

    @Test
    public void testListByBusinessDateAndPeriodTypeAndStatusList(){

        ScheduleJobDTO jobDTO = new ScheduleJobDTO();
        jobDTO.setTenantId(1L);
        jobDTO.setProjectId(-1L);
        List<ScheduleJob> scheduleJobs = scheduleJobService.listByBusinessDateAndPeriodTypeAndStatusList(jobDTO);
        Assert.assertNotNull(scheduleJobs);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDeleteJobsByJobKey(){

        scheduleJobService.deleteJobsByJobKey(Lists.newArrayList("falfjaljfla"));
    }

}
