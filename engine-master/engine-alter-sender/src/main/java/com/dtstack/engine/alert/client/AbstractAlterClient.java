package com.dtstack.engine.alert.client;

import com.dtstack.engine.alert.AlterConfig;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.EventMonitor;
import com.dtstack.lang.data.R;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Auther: dazhi
 * @Date: 2021/1/15 10:39 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractAlterClient implements AlterClient {

    private AlterConfig alterConfig;
    private LinkedBlockingQueue<AlterContext> alterQueue;

    @Override
    public void setConfig(AlterConfig config) {
        this.alterConfig = config;
    }

    @Override
    public R sendSyncAlter(AlterContext alterContext, EventMonitor eventMonitor) throws Exception {

        return null;
    }

    @Override
    public void sendAsyncAAlter(AlterContext alterContext, EventMonitor eventMonitor) throws Exception {

    }
}
