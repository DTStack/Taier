package com.dtstack.taier.scheduler.server.builder.dependency;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.enums.RelyType;
import com.dtstack.taier.scheduler.server.builder.ScheduleConf;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleConfManager;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleCorn;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.utils.JobKeyUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 5:14 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class UpstreamNextJobDependencyHandler extends DecoratorJobDependency {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpstreamDependencyHandler.class);

    public UpstreamNextJobDependencyHandler(String keyPreStr,
                                         ScheduleTaskShade currentTaskShade,
                                         ScheduleJobService scheduleJobService,
                                         List<ScheduleTaskShade> taskShadeList,
                                         JobDependency jobDependency) {
        super(keyPreStr, currentTaskShade, scheduleJobService, taskShadeList,jobDependency);
    }

    @Override
    public List<ScheduleJobJob> generationJobJobForTask(ScheduleCorn corn, Date currentDate, String currentJobKey) {
        List<ScheduleJobJob> jobJobList = super.generationJobJobForTask(corn, currentDate, currentJobKey);

        for (ScheduleTaskShade taskShade : taskShadeList) {
            try {
                String jobKey = getJobKey(taskShade, currentDate);

                // 如果获取不到key，说明是第一天生成实例，则不生成这条边
                if (StringUtils.isBlank(jobKey)) {
                    continue;
                }

                ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
                scheduleJobJob.setTenantId(currentTaskShade.getTenantId());
                scheduleJobJob.setJobKey(currentJobKey);
                scheduleJobJob.setParentJobKey(jobKey);
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

        ScheduleConf scheduleConf = corn.getScheduleConf();
        Date beginDate = scheduleConf.getBeginDate();
        Date endDate = scheduleConf.getEndDate();

        // 上游任务
        Date upstreamTask = corn.isMatch(currentDate) ? currentDate : corn.last(currentDate);
        // 上游任务的上一个周期
        Date lastDate = corn.last(upstreamTask);

        if (!corn.isMatch(currentDate)) {
            // 该任务不在调度周期内 且上游任务和当前任务不在同一计划时间内，返回空字符串
            if (beginDate.before(lastDate) || endDate.after(lastDate)) {
                return "";
            }
        }

        String lastDateStr = DateUtil.getDate(lastDate, DateUtil.STANDARD_DATETIME_FORMAT);

        if (StringUtils.isBlank(lastDateStr)) {
            throw new RdosDefineException("no find upstream task of last cycle");
        }
        String jobKey = JobKeyUtils.generateJobKey(keyPreStr, scheduleTaskShade.getTaskId(), lastDateStr);
        return needCreateKey(lastDate,currentDate,jobKey);
    }
}
