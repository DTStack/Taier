package com.dtstack.taiga.scheduler.server.builder.dependency;

import com.dtstack.taiga.common.enums.Deleted;
import com.dtstack.taiga.dao.domain.ScheduleJobJob;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.pluginapi.util.DateUtil;
import com.dtstack.taiga.scheduler.enums.RelyType;
import com.dtstack.taiga.scheduler.server.builder.cron.ScheduleCorn;
import com.dtstack.taiga.scheduler.utils.JobKeyUtils;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;


/**
 * @Auther: dazhi
 * @Date: 2022/1/4 4:03 PM
 * @Email: dazhi@dtstack.com
 * @Description: 生成自依赖
 */
public class SelfRelianceDependencyHandler extends AbstractDependencyHandler {


    public SelfRelianceDependencyHandler(String keyPreStr, ScheduleTaskShade currentTaskShade) {
        super(keyPreStr, currentTaskShade);
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
        scheduleJobJob.setJobKeyType(RelyType.SELF_RELIANCE.getType());
        scheduleJobJob.setRule(getRule(corn.getScheduleConf()));
        scheduleJobJob.setIsDeleted(Deleted.NORMAL.getStatus());
        jobJobList.add(scheduleJobJob);
        return jobJobList;
    }

}
