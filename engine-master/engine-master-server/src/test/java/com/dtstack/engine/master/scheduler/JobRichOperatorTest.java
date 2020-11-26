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
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
        List<ScheduleTaskShade> tasks = Lists.newArrayList();
        List<ScheduleBatchJob> jobs = Lists.newArrayList();
        // task
        tasks.add(DataCollection.getData().getCronJobBySelfReliance1());
        tasks.add(DataCollection.getData().getCronJobBySelfReliance2());
        tasks.add(DataCollection.getData().getCronJobBySelfReliance3());
        tasks.add(DataCollection.getData().getCronJobBySelfReliance4());
        tasks.add(DataCollection.getData().getCronJobBySelfReliance5());

        // 生成当天周期任务
        String triggerDay = new DateTime().toString("yyyy-MM-dd");

        for (ScheduleTaskShade task : tasks) {
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
            JobCheckRunInfo jobCheckRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, scheduleBatchJob.getStatus(), scheduleBatchJob.getScheduleType(), null, null, new HashMap<>());
            System.out.println(jobCheckRunInfo.getStatus());
        }
    }








}
