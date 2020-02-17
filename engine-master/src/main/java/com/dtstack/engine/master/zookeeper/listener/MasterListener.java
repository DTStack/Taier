package com.dtstack.engine.master.zookeeper.listener;

import com.dtstack.engine.common.util.ExceptionUtil;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.master.node.FailoverStrategy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.zookeeper.ZkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * company: www.dtstack.com
 * @author toutian
 * create: 2019/10/22
 */
public class MasterListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(MasterListener.class);

    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 1000;

    private AtomicBoolean isMaster = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduledService;
    private ZkService zkService;
    private FailoverStrategy failoverStrategy;

    public MasterListener(FailoverStrategy failoverStrategy, ZkService zkService) {
        this.failoverStrategy = failoverStrategy;
        this.zkService = zkService;

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("MasterListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            logOutput++;
            isMaster.getAndSet(zkService.setMaster());
            failoverStrategy.setIsMaster(isMaster.get());

            if (LogCountUtil.count(logOutput, MULTIPLES)) {
                logger.info("MasterListener start again...");
                if (isMaster()) {
                    logger.info("i am is master...");
                }
            }
        } catch (Throwable e) {
            logger.error("MasterCheck error:{}", ExceptionUtil.getErrorMessage(e));
        }
    }

    public boolean isMaster() {
        return isMaster.get();
    }

    @Override
    public void close() throws Exception {
        scheduledService.shutdownNow();
    }
}
