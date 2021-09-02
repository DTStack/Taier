package com.dtstack.engine.master.server.scheduler;

import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: dazhi
 * @Date: 2020/11/14 4:30 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobRichOperatorTest extends AbstractTest {

    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private ScheduleJobService batchJobService;

    private static final String CRON_JOB_NAME = "cronJob";
    private static final String FILL_DATA_TYPE = "fillData";
    private static final String CRON_TRIGGER_TYPE = "cronTrigger";

    private Map<Long,ScheduleTaskShade> tasks = Maps.newHashMap();
    private ScheduleTaskTaskShade taskTask;

    public void init(){
        // task
        ScheduleTaskShade cronJobBySelfReliance1 = DataCollection.getData().getCronJobBySelfReliance1();
        ScheduleTaskShade cronJobBySelfReliance2 = DataCollection.getData().getCronJobBySelfReliance2();
        ScheduleTaskShade cronJobBySelfReliance3 = DataCollection.getData().getCronJobBySelfReliance3();
        ScheduleTaskShade cronJobBySelfReliance4 = DataCollection.getData().getCronJobBySelfReliance4();
        ScheduleTaskShade cronJobBySelfReliance5 = DataCollection.getData().getCronJobBySelfReliance5();

        // tasktask任务
        ScheduleTaskShade cronJobBySelfRelianceTask1 = DataCollection.getData().getCronJobBySelfRelianceTaskTask();
        ScheduleTaskShade cronJobBySelfRelianceTask2 = DataCollection.getData().getCronJobBySelfRelianceTaskTask2();
        taskTask = DataCollection.getData().getTaskTask();

        tasks.put(cronJobBySelfReliance1.getTaskId(),cronJobBySelfReliance1);
        tasks.put(cronJobBySelfReliance2.getTaskId(),cronJobBySelfReliance2);
        tasks.put(cronJobBySelfReliance3.getTaskId(),cronJobBySelfReliance3);
        tasks.put(cronJobBySelfReliance4.getTaskId(),cronJobBySelfReliance4);
        tasks.put(cronJobBySelfReliance5.getTaskId(),cronJobBySelfReliance5);
        tasks.put(cronJobBySelfRelianceTask2.getTaskId(),cronJobBySelfRelianceTask2);
        tasks.put(cronJobBySelfRelianceTask1.getTaskId(),cronJobBySelfRelianceTask1);
    }

    @Test
    public void testJobRichOperator() throws Exception {
        List<ScheduleBatchJob> jobs = Lists.newArrayList();
        // 生成当天周期任务
        String triggerDay = new DateTime().toString("yyyy-MM-dd");
        AtomicInteger count = new AtomicInteger();
        // 生成前天的周期任务
        String triggerYesterdayDay = new DateTime().minusDays(1).toString("yyyy-MM-dd");
        for (Map.Entry<Long, ScheduleTaskShade> longScheduleTaskShadeEntry : tasks.entrySet()) {
            ScheduleTaskShade task = longScheduleTaskShadeEntry.getValue();
            String cronJobName = CRON_JOB_NAME + "_" + task.getName();
            List<ScheduleBatchJob> scheduleBatchJobs = jobGraphBuilder.buildJobRunBean(task, CRON_TRIGGER_TYPE, EScheduleType.NORMAL_SCHEDULE,
                    true, true, triggerYesterdayDay, cronJobName, null, task.getProjectId(), task.getTenantId(),count);

            jobs.addAll(scheduleBatchJobs);
        }

        for (Map.Entry<Long, ScheduleTaskShade> longScheduleTaskShadeEntry : tasks.entrySet()) {
            ScheduleTaskShade task = longScheduleTaskShadeEntry.getValue();
            String cronJobName = CRON_JOB_NAME + "_" + task.getName();
            List<ScheduleBatchJob> scheduleBatchJobs = jobGraphBuilder.buildJobRunBean(task, CRON_TRIGGER_TYPE, EScheduleType.NORMAL_SCHEDULE,
                    true, true, triggerDay, cronJobName, null, task.getProjectId(), task.getTenantId(),count);

            jobs.addAll(scheduleBatchJobs);
        }

        jobs.sort((ebj1, ebj2) -> {
            Long date1 = Long.valueOf(ebj1.getCycTime());
            Long date2 = Long.valueOf(ebj2.getCycTime());
            if (date1 < date2) {
                return -1;
            } else if (date1 > date2) {
                return 1;
            }
            return 0;
        });

        batchJobService.insertJobList(jobs, EScheduleType.NORMAL_SCHEDULE.getType());

        ScheduleBatchJob scheduleBatchJobJob = null;
        for (ScheduleBatchJob scheduleBatchJob : jobs) {
            ScheduleTaskShade scheduleTaskShade = tasks.get(scheduleBatchJob.getTaskId());
            if (scheduleTaskShade != null) {
                JobCheckRunInfo jobCheckRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, scheduleBatchJob.getStatus(), scheduleBatchJob.getScheduleType(), scheduleTaskShade);
                Assert.assertNotNull(jobCheckRunInfo);
            }
            List<ScheduleJobJob> batchJobJobList = scheduleBatchJob.getBatchJobJobList();
            if (CollectionUtils.isNotEmpty(batchJobJobList)) {
                scheduleBatchJobJob = scheduleBatchJob;
            }
        }

        if (CollectionUtils.isNotEmpty(jobs)) {
            // task为null时
            ScheduleBatchJob scheduleBatchJob = jobs.get(0);
            JobCheckRunInfo jobCheckRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, scheduleBatchJob.getStatus(), scheduleBatchJob.getScheduleType(), null);
            Assert.assertNotNull(jobCheckRunInfo);

            // 任务状态不是提交状态时
            ScheduleTaskShade scheduleTaskShade = tasks.get(scheduleBatchJob.getTaskId());
            JobCheckRunInfo jobCheckRunInfo1 = jobRichOperator.checkJobCanRun(scheduleBatchJob, RdosTaskStatus.FAILED.getStatus(), scheduleBatchJob.getScheduleType(), scheduleTaskShade);
            Assert.assertNotNull(jobCheckRunInfo1);

            // 任务状态不是提交状态时
            if (scheduleBatchJobJob != null) {
                List<ScheduleJobJob> batchJobJobList = scheduleBatchJobJob.getBatchJobJobList();
                ScheduleJobJob scheduleJobJob = batchJobJobList.get(0);
                scheduleJobJob.setParentJobKey("1234567890");

                ScheduleTaskShade scheduleTaskShade1 = tasks.get(scheduleBatchJobJob.getTaskId());
                JobCheckRunInfo jobCheckRunInfo3 = jobRichOperator.checkJobCanRun(scheduleBatchJobJob, scheduleBatchJobJob.getStatus(), scheduleBatchJobJob.getScheduleType(), scheduleTaskShade1);
                Assert.assertNotNull(jobCheckRunInfo3);
            }
        }

    }

    @Test
    public void getCycTime() throws Exception {
        String cycTime = jobRichOperator.getCycTime(1);
        Assert.assertNotNull(cycTime);
    }








}
