package com.dtstack.taiga.scheduler.server.builder.dependency;

import com.dtstack.taiga.common.enums.DependencyType;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.scheduler.enums.RelyRule;
import com.dtstack.taiga.scheduler.server.builder.ScheduleConf;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 4:06 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractDependencyHandler implements DependencyHandler {

    /**
     * 前缀
     */
    protected String keyPreStr;

    /**
     * 当前任务
     */
    protected ScheduleTaskShade currentTaskShade;

    /**
     * 下一个依赖处理器
     */
    private DependencyHandler nextDependencyHandler;

    public AbstractDependencyHandler(String keyPreStr,
                                     ScheduleTaskShade currentTaskShade) {
        this.keyPreStr = keyPreStr;
        this.currentTaskShade = currentTaskShade;
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

    @Override
    public DependencyHandler next() {
        return nextDependencyHandler;
    }

    @Override
    public void setNext(DependencyHandler dependencyHandler) {
        this.nextDependencyHandler = dependencyHandler;
    }
}
