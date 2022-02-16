package com.dtstack.taier.scheduler.server.builder.dependency;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.enums.RelyType;
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
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class UpstreamNextJobDependencyHandler extends AbstractDependencyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpstreamDependencyHandler.class);

    /**
     * 上游任务
     */
    protected List<ScheduleTaskShade> taskShadeList;

    private final ScheduleJobService scheduleJobService;

    public UpstreamNextJobDependencyHandler(String keyPreStr, ScheduleTaskShade currentTaskShade, List<ScheduleTaskShade> taskShadeList, ScheduleJobService scheduleJobService) {
        super(keyPreStr, currentTaskShade);
        this.taskShadeList = taskShadeList;
        this.scheduleJobService = scheduleJobService;
    }

    @Override
    public List<ScheduleJobJob> generationJobJobForTask(ScheduleCorn corn, Date currentDate, String currentJobKey) {
        List<ScheduleJobJob> jobJobList = Lists.newArrayList();

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
        // 上游任务
        Date upstreamTask = corn.isMatch(currentDate) ? currentDate : corn.last(currentDate);

        // 判断是否上一次执行的时间和当前时间是否是同一天，如果是的话插入，不是的话，去查询一下数据库是否有实例生成。
        if (!DateUtil.isSameDay(upstreamTask,currentDate)) {
            // 不是同一天
            ScheduleJob scheduleJob = scheduleJobService.lambdaQuery()
                    .select(ScheduleJob::getJobId)
                    .eq(ScheduleJob::getJobKey, upstreamTask)
                    .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .one();
            if (scheduleJob == null) {
                return null;
            }
        }
        // 上游任务的上一个周期
        Date upstreamTaskLastCycle = corn.last(upstreamTask);

        String lastDate = DateUtil.getDate(upstreamTaskLastCycle, DateUtil.STANDARD_DATETIME_FORMAT);

        if (StringUtils.isBlank(lastDate)) {
            throw new RdosDefineException("no find upstream task of last cycle");
        }

        return JobKeyUtils.generateJobKey(keyPreStr,scheduleTaskShade.getTaskId(), lastDate);
    }
}
