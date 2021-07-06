package com.dtstack.batch.enums;

/**
 * 任务依赖类型
 * Date: 2018/6/12
 * Company: www.dtstack.com
 * @author xuchao
 */
public enum DependencyType {

    /**
     * 不依赖上一调度周期
     */
    NO_SELF_DEPENDENCY(0),

    /**
     * 自依赖上一个周期的完成状态
     */
    SELF_DEPENDENCY_SUCCESS(1),

    /**
     * 依赖下游任务的上一个周期完成状态
     */
    PRE_PERIOD_CHILD_DEPENDENCY_SUCCESS(2),

    /**
     * 自依赖上一个周期的结束:只要运行结束就可以
     */
    SELF_DEPENDENCY_END(3),

    /**
     * 依赖下游任务的上一个周期的结束:只要运行结束就可以
     */
    PRE_PERIOD_CHILD_DEPENDENCY_END(4);

    Integer type;

    DependencyType(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
