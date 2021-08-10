package com.dtstack.engine.alert;

import dt.insight.plat.lang.web.R;

/**
 * @Auther: dazhi
 * @Date: 2021/1/18 10:58 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AdapterEventMonitor implements EventMonitor {

    @Override
    public Boolean startEvent(AlterContext alterContext) {
        return Boolean.TRUE;
    }

    @Override
    public void refuseEvent(AlterContext alterContext) {}

    @Override
    public void joiningQueueEvent(AlterContext alterContext) {}

    @Override
    public void leaveQueueAndSenderBeforeEvent(AlterContext alterContext) {}

    @Override
    public void alterFailure(AlterContext alterContext, R r, Exception e) {}

    @Override
    public void alterSuccess(AlterContext alterContext, R r) {}
}
