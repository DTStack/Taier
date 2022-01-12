package com.dtstack.engine.master.server.builder.dependency;

import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJobJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.master.enums.RelyRule;
import com.dtstack.engine.master.enums.RelyType;
import com.dtstack.engine.master.server.builder.AbstractJobBuilder;
import com.dtstack.engine.master.server.builder.cron.ScheduleConfManager;
import com.dtstack.engine.master.server.builder.cron.ScheduleCorn;
import com.dtstack.engine.master.service.ScheduleTaskService;
import com.dtstack.engine.master.service.ScheduleTaskTaskService;
import com.dtstack.engine.master.utils.JobKeyUtils;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 4:37 PM
 * @Email: dazhi@dtstack.com
 * @Description: 获得上游任务依赖
 */
public class UpstreamDependencyHandler extends AbstractDependencyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpstreamDependencyHandler.class);

    public UpstreamDependencyHandler(String keyPreStr, ScheduleTaskShade currentTaskShade, List<ScheduleTaskShade> taskShadeList) {
        super(keyPreStr, currentTaskShade, taskShadeList);
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
                scheduleJobJob.setParentJobKeyType(RelyType.UPSTREAM.getType());
                scheduleJobJob.setRule(RelyRule.RUN_SUCCESS.getType());
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
        String lastDate = DateUtil.getDate(corn.isMatch(currentDate) ? currentDate : corn.last(currentDate), DateUtil.STANDARD_DATETIME_FORMAT);

        if (StringUtils.isBlank(lastDate)) {
            throw new RdosDefineException("no find upstream task of last cycle");
        }
        return JobKeyUtils.generateJobKey(keyPreStr,scheduleTaskShade.getTaskId(), lastDate);
    }
}
