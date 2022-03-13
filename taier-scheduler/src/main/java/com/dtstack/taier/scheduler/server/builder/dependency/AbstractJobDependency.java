package com.dtstack.taier.scheduler.server.builder.dependency;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.DependencyType;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.enums.RelyRule;
import com.dtstack.taier.scheduler.server.builder.ScheduleConf;
import com.dtstack.taier.scheduler.service.ScheduleJobService;

import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 4:06 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractJobDependency implements JobDependency {

    /**
     * 前缀
     */
    protected String keyPreStr;



    /**
     * 当前任务
     */
    protected ScheduleTaskShade currentTaskShade;

    /**
     * 上游任务
     */
    protected List<ScheduleTaskShade> taskShadeList;


    protected ScheduleJobService scheduleJobService;

    public AbstractJobDependency(String keyPreStr,
                                 ScheduleTaskShade currentTaskShade,
                                 ScheduleJobService scheduleJobService,
                                 List<ScheduleTaskShade> taskShadeList) {
        this.keyPreStr = keyPreStr;
        this.taskShadeList = taskShadeList;
        this.currentTaskShade = currentTaskShade;
        this.scheduleJobService = scheduleJobService;
    }

    /**
     * 获得依赖规则
     *
     * @param scheduleConf 调度信息
     * @return 依赖规则
     */
    protected Integer getRule(ScheduleConf scheduleConf) {
        Integer selfReliance = scheduleConf.getSelfReliance();

        if (DependencyType.SELF_DEPENDENCY_SUCCESS.getType().equals(selfReliance)
                ||DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS.getType().equals(selfReliance) ) {
            return RelyRule.RUN_SUCCESS.getType();
        } else if (DependencyType.SELF_DEPENDENCY_END.getType().equals(selfReliance)
                || DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_END.getType().equals(selfReliance)) {
            return RelyRule.RUN_FINISH.getType();
        }
        return RelyRule.RUN_SUCCESS.getType();
    }

    /**
     * 判断是否是同一天
     *
     * @param lastDate 下一个周期
     * @param currentDate 当期周期
     * @param lastJobKey 下一个周期key
     * @return lastJobKey
     */
    protected String needCreateKey(Date lastDate,Date currentDate,String lastJobKey) {
        if (!DateUtil.isSameDay(lastDate,currentDate)) {
            // 不是同一天
            ScheduleJob scheduleJob = scheduleJobService.lambdaQuery()
                    .select(ScheduleJob::getJobId)
                    .eq(ScheduleJob::getJobKey, lastJobKey)
                    .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .one();
            if (scheduleJob == null) {
                return null;
            }
        }
        return lastJobKey;
    }
}
