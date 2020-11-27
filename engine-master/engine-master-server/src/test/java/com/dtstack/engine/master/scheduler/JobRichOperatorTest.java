package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

    @Test
    public void testJobRichOperator() throws Exception {
        Map<Long,ScheduleTaskShade> tasks = Maps.newHashMap();
        List<ScheduleBatchJob> jobs = Lists.newArrayList();
        // task
        ScheduleTaskShade cronJobBySelfReliance1 = DataCollection.getData().getCronJobBySelfReliance1();
        ScheduleTaskShade cronJobBySelfReliance2 = DataCollection.getData().getCronJobBySelfReliance2();
        ScheduleTaskShade cronJobBySelfReliance3 = DataCollection.getData().getCronJobBySelfReliance3();
        ScheduleTaskShade cronJobBySelfReliance4 = DataCollection.getData().getCronJobBySelfReliance4();
        ScheduleTaskShade cronJobBySelfReliance5 = DataCollection.getData().getCronJobBySelfReliance5();
        tasks.put(cronJobBySelfReliance1.getTaskId(),cronJobBySelfReliance1);
        tasks.put(cronJobBySelfReliance2.getTaskId(),cronJobBySelfReliance2);
        tasks.put(cronJobBySelfReliance3.getTaskId(),cronJobBySelfReliance3);
        tasks.put(cronJobBySelfReliance4.getTaskId(),cronJobBySelfReliance4);
        tasks.put(cronJobBySelfReliance5.getTaskId(),cronJobBySelfReliance5);

        // 生成当天周期任务
        String triggerDay = new DateTime().toString("yyyy-MM-dd");

        for (Map.Entry<Long, ScheduleTaskShade> longScheduleTaskShadeEntry : tasks.entrySet()) {
            ScheduleTaskShade task = longScheduleTaskShadeEntry.getValue();
            String cronJobName = CRON_JOB_NAME + "_" + task.getName();
            List<ScheduleBatchJob> scheduleBatchJobs = jobGraphBuilder.buildJobRunBean(task, CRON_TRIGGER_TYPE, EScheduleType.NORMAL_SCHEDULE,
                    true, true, triggerDay, cronJobName, null, task.getProjectId(), task.getTenantId());

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


        for (ScheduleBatchJob scheduleBatchJob : jobs) {
            ScheduleTaskShade scheduleTaskShade = tasks.get(scheduleBatchJob.getTaskId());
            if (scheduleTaskShade != null) {
                JobCheckRunInfo jobCheckRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, scheduleBatchJob.getStatus(), scheduleBatchJob.getScheduleType(), scheduleTaskShade);
                Assert.assertNotNull(jobCheckRunInfo);
            }

        }
    }


    public void getCycTime() throws Exception {
        String cycTime = jobRichOperator.getCycTime(1);
        Assert.assertNotNull(cycTime);
    }








}
