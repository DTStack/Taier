package com.dtstack.engine.alert.client;

import com.dtstack.engine.alert.AlterConfig;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.EventMonitor;
import com.dtstack.lang.data.R;
import com.dtstack.engine.alert.pool.CustomThreadFactory;

import java.util.concurrent.*;

/**
 * @Auther: dazhi
 * @Date: 2021/1/15 10:39 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractAlterClient implements AlterClient {

    private AlterConfig alterConfig;
    private LinkedBlockingQueue<AlterContext> alterQueue;
    private ExecutorService executorService;

    @Override
    public void setConfig(AlterConfig config) {
        if (config == null) {
            this.alterConfig = new AlterConfig();
        } else {
            this.alterConfig = config;
        }

        init();
    }

    private void init(){
        if (alterQueue == null) {
            alterQueue = new LinkedBlockingQueue<>(alterConfig.getQueueSize());
        }

        executorService = new ThreadPoolExecutor(alterConfig.getJobExecutorPoolCorePoolSize(), alterConfig.getJobExecutorPoolMaximumPoolSize(), alterConfig.getJobExecutorPoolKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(alterConfig.getQueueSize()),
                new CustomThreadFactory(getAlertGateCode()+"_alterJob"),
                new CustomThreadRunsPolicy(threadName, getAlertGateCode()));

    }

    private void emitJob2Queue() {


    }

    protected abstract String getAlertGateCode();


    @Override
    public R sendSyncAlter(AlterContext alterContext, EventMonitor eventMonitor) throws Exception {

        return null;
    }

    @Override
    public void sendAsyncAAlter(AlterContext alterContext, EventMonitor eventMonitor) throws Exception {

    }
}
