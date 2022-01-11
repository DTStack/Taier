package com.dtstack.engine.master.server.builder.dependency;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJobJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.master.enums.RelyType;
import com.dtstack.engine.master.server.builder.ScheduleConf;
import com.dtstack.engine.master.server.builder.cron.ScheduleCorn;
import com.dtstack.engine.master.utils.JobKeyUtils;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.List;


/**
 * @Auther: dazhi
 * @Date: 2022/1/4 4:03 PM
 * @Email: dazhi@dtstack.com
 * @Description: 生成自依赖
 */
public class SelfRelianceDependencyHandler extends AbstractDependencyHandler {


    public SelfRelianceDependencyHandler(String keyPreStr, ScheduleTaskShade currentTaskShade, List<ScheduleTaskShade> taskShadeList) {
        super(keyPreStr, currentTaskShade, taskShadeList);
    }

    @Override
    public List<ScheduleJobJob> generationJobJobForTask(ScheduleCorn corn, Date currentDate, String currentJobKey) {
        // 获得上一次执行的时间
        String lastDate = DateUtil.getDate(corn.last(currentDate), DateUtil.STANDARD_DATETIME_FORMAT);
        String lastJobKey = JobKeyUtils.generateJobKey(keyPreStr,currentTaskShade.getTaskId(), lastDate);

        List<ScheduleJobJob> jobJobList = Lists.newArrayList();
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setTenantId(currentTaskShade.getTenantId());
        scheduleJobJob.setJobKey(currentJobKey);
        scheduleJobJob.setParentJobKey(lastJobKey);
        scheduleJobJob.setParentJobKeyType(RelyType.SELF_RELIANCE.getType());
        scheduleJobJob.setRule(getRule(corn.getScheduleConf()));
        jobJobList.add(scheduleJobJob);
        return jobJobList;
    }

}
