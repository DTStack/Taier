package com.dtstack.taier.scheduler.server.builder.dependency;

import com.dtstack.taier.common.enums.DependencyType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.ScheduleTaskTaskShade;
import com.dtstack.taier.scheduler.server.builder.ScheduleConf;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleCorn;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.service.ScheduleTaskShadeService;
import com.dtstack.taier.scheduler.service.ScheduleTaskTaskService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 5:32 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class DependencyManager {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskService;

    @Autowired
    private ScheduleTaskTaskService scheduleTaskTaskService;

    /**
     * 获得依赖处理器链
     *
     * @param keyPreStr        前缀
     * @param currentTaskShade 任务
     * @param corn             执行周期
     * @return 依赖处理器链
     */
    public JobDependency getDependencyHandler(String keyPreStr, ScheduleTaskShade currentTaskShade, ScheduleCorn corn) {
        // 查询上游任务
        List<ScheduleTaskTaskShade> scheduleTaskTaskShadeList = scheduleTaskTaskService.lambdaQuery()
                .eq(ScheduleTaskTaskShade::getTaskId, currentTaskShade.getTaskId())
                .eq(ScheduleTaskTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                .list();

        List<Long> parentTaskIds = scheduleTaskTaskShadeList.stream().map(ScheduleTaskTaskShade::getParentTaskId).collect(Collectors.toList());

        // 如果没有上游任务，就不需要UpstreamDependencyHandler
        List<ScheduleTaskShade> taskShadeList = null;
        if (CollectionUtils.isNotEmpty(parentTaskIds)) {
            // 查询任务
            taskShadeList = scheduleTaskService.lambdaQuery()
                    .in(ScheduleTaskShade::getTaskId, parentTaskIds)
                    .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
        }

        // 上游任务的依赖处理器
        JobDependency jobDependency = new UpstreamDependencyHandler(keyPreStr, currentTaskShade,scheduleJobService,taskShadeList);

        // 判断是否设置自依赖
        ScheduleConf scheduleConf = corn.getScheduleConf();
        if (DependencyType.SELF_DEPENDENCY_SUCCESS.getType().equals(scheduleConf.getSelfReliance())
                || DependencyType.SELF_DEPENDENCY_END.getType().equals(scheduleConf.getSelfReliance())) {
            // 需要自依赖任务, 对上游依赖做增强，生成自依赖
            jobDependency = new SelfRelianceDependencyHandler(keyPreStr, currentTaskShade, scheduleJobService, taskShadeList, jobDependency);
        } else if (DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS.getType().equals(scheduleConf.getSelfReliance())
                || DependencyType.PRE_PERIOD_CHILD_DEPENDENCY_END.getType().equals(scheduleConf.getSelfReliance())) {
            if (CollectionUtils.isNotEmpty(taskShadeList)) {
                // 依赖下游任务的上一个周期 对jobDependency进行增强
                jobDependency = new UpstreamNextJobDependencyHandler(keyPreStr, currentTaskShade, scheduleJobService, taskShadeList,jobDependency);
            }
        }

        return jobDependency;
    }
}
