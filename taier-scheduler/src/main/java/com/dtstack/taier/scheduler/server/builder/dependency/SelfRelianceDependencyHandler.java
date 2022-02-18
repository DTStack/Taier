package com.dtstack.taier.scheduler.server.builder.dependency;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.enums.RelyType;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleCorn;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.utils.JobKeyUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;


/**
 * @Auther: dazhi
 * @Date: 2022/1/4 4:03 PM
 * @Email: dazhi@dtstack.com
 * @Description: 生成自依赖
 */
public class SelfRelianceDependencyHandler extends AbstractDependencyHandler {

    public SelfRelianceDependencyHandler(String keyPreStr, ScheduleTaskShade currentTaskShade, ScheduleJobService scheduleJobService) {
        super(keyPreStr, currentTaskShade,scheduleJobService);
    }

    @Override
    public List<ScheduleJobJob> generationJobJobForTask(ScheduleCorn corn, Date currentDate, String currentJobKey) {
        // 获得上一次执行的时间
        Date last = corn.last(currentDate);
        String lastDate = DateUtil.getDate(last, DateUtil.STANDARD_DATETIME_FORMAT);
        String lastJobKey = JobKeyUtils.generateJobKey(keyPreStr,currentTaskShade.getTaskId(), lastDate);

        // 判断是否上一次执行的时间和当前时间是否是同一天，如果是的话插入，不是的话，去查询一下数据库是否有实例生成。
        lastJobKey = needCreateKey(last,currentDate,lastJobKey);
        if (StringUtils.isBlank(lastJobKey)) {
            return Lists.newArrayList();
        }

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
