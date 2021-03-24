package com.dtstack.engine.alert;

import com.dtstack.lang.data.R;

/**
 * @Auther: dazhi
 * @Date: 2021/1/14 3:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface EventMonitor {

    /**
     * 开始告警事件
     * @param alterContext
     */
    Boolean startEvent(AlterContext alterContext);

    /**
     * 拒绝事件
     *
     * @param alterContext 上下文对象
     */
    void refuseEvent(AlterContext alterContext);

    /**
     * 入队事件
     *
     * @param alterContext 上下文对象
     */
    void joiningQueueEvent(AlterContext alterContext);

    /**
     * 出队事件
     *
     * @param alterContext 上下文对象
     */
    void leaveQueueAndSenderBeforeEvent(AlterContext alterContext);

    /**
     * 告警失败事件
     *
     * @param alterContext 上下文对象
     * @param r 结果
     * @param e 失败原因异常
     */
    void alterFailure(AlterContext alterContext, R r,Exception e);

    /**
     * 告警成功事件
     *
     * @param alterContext 上下文对象
     * @param r 结果
     */
    void alterSuccess(AlterContext alterContext, R r);
}
