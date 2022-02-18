package com.dtstack.taier.scheduler.server.builder.dependency;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.enums.RelyRule;
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
 * @Date: 2022/1/4 4:37 PM
 * @Email: dazhi@dtstack.com
 * @Description: 获得上游任务依赖
 */
public class UpstreamDependencyHandler extends AbstractDependencyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpstreamDependencyHandler.class);

    /**
     * 上游任务
     */
    protected List<ScheduleTaskShade> taskShadeList;

    public UpstreamDependencyHandler(String keyPreStr, ScheduleTaskShade currentTaskShade, List<ScheduleTaskShade> taskShadeList, ScheduleJobService scheduleJobService) {
        super(keyPreStr, currentTaskShade,scheduleJobService);
        this.taskShadeList = taskShadeList;
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
                scheduleJobJob.setJobKeyType(RelyType.UPSTREAM.getType());
                scheduleJobJob.setRule(RelyRule.RUN_SUCCESS.getType());
                scheduleJobJob.setIsDeleted(Deleted.NORMAL.getStatus());
                jobJobList.add(scheduleJobJob);
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }
        return jobJobList;
    }

    /**
     * 获得jobKey
     *
     * @param scheduleTaskShade 任务
     * @param currentDate 当前时间
     * @return jobKey
     */
    public String getJobKey(ScheduleTaskShade scheduleTaskShade, Date currentDate) throws Exception {
        ScheduleCorn corn = ScheduleConfManager.parseFromJson(scheduleTaskShade.getScheduleConf());
        // 上一个周期
        Date lastDate = corn.isMatch(currentDate) ? currentDate : corn.last(currentDate);
        String lastDateStr = DateUtil.getDate(corn.isMatch(currentDate) ? currentDate : corn.last(currentDate), DateUtil.STANDARD_DATETIME_FORMAT);

        if (StringUtils.isBlank(lastDateStr)) {
            throw new RdosDefineException("no find upstream task of last cycle");
        }
        String jobKey = JobKeyUtils.generateJobKey(keyPreStr, scheduleTaskShade.getTaskId(), lastDateStr);
        return needCreateKey(lastDate,currentDate,jobKey);
    }
}
