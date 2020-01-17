package com.dtstack.task;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.sdk.console.client.ConsoleNotifyApiClient;
import com.dtstack.sdk.console.domain.parameter.NotifyRecordParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.task.common.enums.JobCheckStatus;
import com.dtstack.task.dao.BatchAlarmRecordUserDao;
import com.dtstack.task.dao.BatchJobJobDao;
import com.dtstack.task.domain.BatchJob;
import com.dtstack.task.domain.BatchJobJob;
import com.dtstack.task.domain.BatchTaskShade;
import com.dtstack.task.server.alarm.BatchAlarmProcessor;
import com.dtstack.task.server.bo.ScheduleBatchJob;
import com.dtstack.task.server.impl.BatchFlowWorkJobService;
import com.dtstack.task.server.impl.BatchJobService;
import com.dtstack.task.server.impl.BatchTaskShadeService;
import com.dtstack.task.server.scheduler.JobCheckRunInfo;
import com.dtstack.task.server.scheduler.JobGraphBuilder;
import com.dtstack.task.server.scheduler.JobRichOperator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2019-11-05
 */
public class JobTest extends BaseTest {

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private BatchTaskShadeService batchTaskShadeService;

    @Resource
    private BatchJobJobDao batchJobJobDao;

    @Autowired
    private BatchFlowWorkJobService batchFlowWorkJobService;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private BatchAlarmProcessor batchAlarmProcessor;

    @Resource
    private BatchAlarmRecordUserDao batchAlarmRecordUserDao;

    @Resource
    private ConsoleNotifyApiClient consoleNotifyApiClient;

    @Test
    public void testTrigger(){

        BatchJob byId = batchJobService.getById(114l);
        try {
            batchJobService.sendTaskStartTrigger(byId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkCanRun(){
        BatchJob batchJob = batchJobService.getById(9918L);
        batchJob.setStatus(TaskStatus.UNSUBMIT.getStatus());
        ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(batchJob);
        List<BatchJobJob> batchJobJobs = batchJobJobDao.listByJobKey(batchJob.getJobKey());
        scheduleBatchJob.setJobJobList(batchJobJobs);
        BatchTaskShade batchTaskById = batchTaskShadeService.getBatchTaskById(batchJob.getTaskId(), 1);
        Map<Long,BatchTaskShade> taskShadeMap = new HashMap<>();
        taskShadeMap.put(batchJob.getTaskId(),batchTaskById);
        try {
            JobCheckRunInfo jobCheckRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, batchJob.getStatus(), batchJob.getType(), new HashSet<>(), new HashMap<>(), taskShadeMap);
            Assert.assertNotNull(jobCheckRunInfo);
            Assert.assertEquals(jobCheckRunInfo.getStatus(), JobCheckStatus.CAN_EXE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void TestFlow(){
        BatchJob batchJob = batchJobService.getById(274154l);
        boolean b = batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(batchJob.getJobId());
        System.out.println(b);
    }

    @Test
    public void createTaskShade(){
        batchJobService.createTodayTaskShade(29L);
    }


    @Test
    public void sendSync()throws Exception{
        batchJobService.startJob(batchJobService.getById(62L));
    }


    @Test
    public void sendAlarm(){
        batchAlarmProcessor.run();
    }

    @Test
    public void testConsoleAlarm(){
        NotifyRecordParam param = new NotifyRecordParam();
        param.setTenantId(279L);
        param.setProjectId(495L);
        param.setAppType(AppType.RDOS.getType());
        param.setContent("告警测试");
        param.setStatus(8);

        ApiResponse<Long> longApiResponse = consoleNotifyApiClient.generateContent(param);
        System.out.println(longApiResponse);
    }

    @Test
    public void testName(){
        String getTaskNameByJobKey = batchTaskShadeService.getTaskNameByJobKey("cronTrigger_479_20191213011500", 1);
        Assert.assertEquals(getTaskNameByJobKey,"cronTrigger_479_20191213011500");

    }

    @Test
    public void testErrorJobList(){
        PageResult statusJobList = batchJobService.getStatusJobList(null, null, 1, 1001L, 0, 5, 1);
        Assert.assertNotNull(statusJobList);
        Assert.assertNotNull(statusJobList.getData());
        Assert.assertEquals(statusJobList.getPageSize(),5);
    }
}
