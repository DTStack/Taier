package com.dtstack.engine.alert.client;

import com.dtstack.engine.alert.AdapterEventMonitor;
import com.dtstack.engine.alert.AlterConfig;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.EventMonitor;
import com.dtstack.engine.alert.pool.AlterDiscardPolicy;
import com.dtstack.engine.alert.pool.CustomThreadFactory;
import com.dtstack.lang.data.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: dazhi
 * @Date: 2021/1/15 10:39 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractAlterClient implements AlterClient,Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private AlterConfig alterConfig;
    private LinkedBlockingQueue<AlterContext> alterQueue;
    private ExecutorService executorService;
    private final AtomicBoolean open = new AtomicBoolean(Boolean.TRUE);

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
            alterQueue = new LinkedBlockingQueue<>();
        }

        String threadName = this.getClass().getSimpleName() + "_" + getAlertGateCode()+"_alterJob";

        executorService = new ThreadPoolExecutor(
                alterConfig.getJobExecutorPoolCorePoolSize(),
                alterConfig.getJobExecutorPoolMaximumPoolSize(),
                alterConfig.getJobExecutorPoolKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new CustomThreadFactory(threadName),
                new AlterDiscardPolicy(threadName, getAlertGateCode()));
        executorService.submit(this);
    }


    @Override
    public R sendSyncAlter(AlterContext alterContext, EventMonitor eventMonitor) throws Exception {
        eventMonitor = setDefaultEvent(alterContext, eventMonitor);
        return sendAlter(alterContext, eventMonitor);
    }

    @Override
    public void sendAsyncAAlter(AlterContext alterContext, EventMonitor eventMonitor) throws Exception {
        eventMonitor = setDefaultEvent(alterContext, eventMonitor);

        if (!alterQueue.contains(alterContext)) {
            logger.warn("元素:"+alterContext.getMark()+"已在队列中存在");
            return;
        }

        if (alterQueue.size() > alterConfig.getQueueSize()) {
            // 响应告警拒绝事件
            logger.warn("元素:"+alterContext.getMark()+"被拒绝");
            eventMonitor.refuseEvent(alterContext);
        } else {
            eventMonitor.joiningQueueEvent(alterContext);
            alterQueue.put(alterContext);
        }
    }

    private EventMonitor setDefaultEvent(AlterContext alterContext, EventMonitor eventMonitor) {
        if (alterContext.getEventMonitor() == null) {
            if (eventMonitor == null) {
                eventMonitor = new AdapterEventMonitor();
            }

            alterContext.setEventMonitor(eventMonitor);
        }
        return eventMonitor;
    }

    public void close(){
        open.compareAndSet( Boolean.TRUE,Boolean.FALSE);
        executorService.shutdown();
    }

    @Override
    public void run() {
        while (open.get()) {
            try {
                AlterContext alterContext = alterQueue.poll(30, TimeUnit.SECONDS);
                if (alterContext != null) {
                    // 响应出队事件
                    EventMonitor eventMonitor = alterContext.getEventMonitor();
                    eventMonitor.leaveQueueEvent(alterContext);
                    sendAlter(alterContext,eventMonitor);
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    private R sendAlter(AlterContext alterContext,EventMonitor eventMonitor) throws Exception {
        try {
            R r = send(alterContext);
            eventMonitor.alterSuccess(alterContext,r);
            return r;
        } catch (Exception e) {
            logger.error("", e);
            // 触发告警失败事件
            eventMonitor.alterFailure(alterContext,null,e);
            throw e;
        }
    }

    /**
     * 发送告警
     *
     * @param alterContext
     * @return
     */
    protected abstract R send(AlterContext alterContext) throws Exception;

    /**
     * 获得告警类型
     *
     * @return
     */
    protected abstract String getAlertGateCode();

}
