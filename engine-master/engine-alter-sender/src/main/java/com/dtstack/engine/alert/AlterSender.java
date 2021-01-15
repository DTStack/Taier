package com.dtstack.engine.alert;

import com.dtstack.lang.data.R;

/**
 * @Auther: dazhi
 * @Date: 2021/1/11 4:01 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface AlterSender {

    /**
     * 同步发送消息
     *
     * @param alterContext 发送上下文对象
     * @param eventMonitor 事件监听器
     * @return
     */
    R sendSyncAlter(AlterContext alterContext,EventMonitor eventMonitor) throws Exception;

    /**
     * 异步发送
     *
     * @param alterContext
     * @param eventMonitor
     */
    void sendAsyncAAlter(AlterContext alterContext,EventMonitor eventMonitor) throws Exception;
}
