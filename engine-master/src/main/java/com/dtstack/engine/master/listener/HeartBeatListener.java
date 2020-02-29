package com.dtstack.engine.master.listener;

import com.dtstack.engine.common.util.ExceptionUtil;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.data.BrokerHeartNode;
import com.dtstack.engine.master.zookeeper.ZkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class HeartBeatListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatListener.class);

    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 1000;

    private final ScheduledExecutorService scheduledService;

    private ZkService zkService;

    public HeartBeatListener(ZkService zkService) {
        this.zkService = zkService;

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
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
            BrokerHeartNode brokerHeartNode = BrokerHeartNode.initBrokerHeartNode();
            brokerHeartNode.setSeq(1L);
            brokerHeartNode.setAlive(true);
            zkService.updateSynchronizedLocalBrokerHeartNode(zkService.getLocalAddress(), brokerHeartNode, false);
            if (LogCountUtil.count(logOutput, MULTIPLES)) {
                logger.info("HeartBeatListener start again...");
            }
        } catch (Throwable e) {
            logger.error(ExceptionUtil.getErrorMessage(e));
        }
    }

    @Override
    public void close() throws Exception {
        scheduledService.shutdownNow();
    }
}
