package com.dtstack.taiga.scheduler.server.builder.dependency;

import com.dtstack.taiga.common.enums.Deleted;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.ScheduleJobJob;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.pluginapi.util.DateUtil;
import com.dtstack.taiga.scheduler.enums.RelyType;
import com.dtstack.taiga.scheduler.server.builder.cron.ScheduleConfManager;
import com.dtstack.taiga.scheduler.server.builder.cron.ScheduleCorn;
import com.dtstack.taiga.scheduler.utils.JobKeyUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 5:14 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class UpstreamNextJobDependencyHandler extends AbstractDependencyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpstreamDependencyHandler.class);

    /**
     * 上游任务
     */
    protected List<ScheduleTaskShade> taskShadeList;

    public UpstreamNextJobDependencyHandler(String keyPreStr, ScheduleTaskShade currentTaskShade, List<ScheduleTaskShade> taskShadeList) {
        super(keyPreStr, currentTaskShade);
        this.taskShadeList = taskShadeList;
    }

    @Override
    public List<ScheduleJobJob> generationJobJobForTask(ScheduleCorn corn, Date currentDate, String currentJobKey) {
        List<ScheduleJobJob> jobJobList = Lists.newArrayList();

        for (ScheduleTaskShade taskShade : taskShadeList) {
            try {
                ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
                scheduleJobJob.setTenantId(currentTaskShade.getTenantId());
                scheduleJobJob.setJobKey(currentJobKey);
                scheduleJobJob.setParentJobKey(getJobKey(taskShade,currentDate));
                scheduleJobJob.setJobKeyType(RelyType.UPSTREAM_NEXT_JOB.getType());
                scheduleJobJob.setRule(getRule(corn.getScheduleConf()));
                scheduleJobJob.setIsDeleted(Deleted.NORMAL.getStatus());
                jobJobList.add(scheduleJobJob);
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }
        return jobJobList;
    }

    private String getJobKey(ScheduleTaskShade scheduleTaskShade, Date currentDate) throws Exception {
        ScheduleCorn corn = ScheduleConfManager.parseFromJson(scheduleTaskShade.getScheduleConf());
        // 上游任务
        Date upstreamTask = corn.isMatch(currentDate) ? currentDate : corn.last(currentDate);

        // 上游任务的上一个周期
        Date upstreamTaskLastCycle = corn.last(upstreamTask);

        String lastDate = DateUtil.getDate(upstreamTaskLastCycle, DateUtil.STANDARD_DATETIME_FORMAT);

        if (StringUtils.isBlank(lastDate)) {
            throw new RdosDefineException("no find upstream task of last cycle");
        }

        return JobKeyUtils.generateJobKey(keyPreStr,scheduleTaskShade.getTaskId(), lastDate);
    }
}
