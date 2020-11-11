package com.dtstack.engine.master.listener;

import com.dtstack.engine.master.failover.FailoverStrategy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.scheduler.ScheduleJobBack;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
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
public class MasterListener implements LeaderLatchListener, Listener {

    private static final Logger logger = LoggerFactory.getLogger(MasterListener.class);

    private final static int CHECK_INTERVAL = 10000;

    private final AtomicBoolean isMaster = new AtomicBoolean(false);
    private final FailoverStrategy failoverStrategy;
    private final ScheduleJobBack scheduleJobBack;

    private final ScheduledExecutorService scheduledService;
    private LeaderLatch latch;

    public MasterListener(FailoverStrategy failoverStrategy,
                          ScheduleJobBack scheduleJobBack,
                          CuratorFramework curatorFramework,
                          String latchPath,
                          String localAddress) throws Exception {
        this.failoverStrategy = failoverStrategy;
        this.scheduleJobBack = scheduleJobBack;

        this.latch = new LeaderLatch(curatorFramework, latchPath, localAddress);
        this.latch.addListener(this);
        this.latch.start();

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                60000,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public boolean isMaster() {
        return isMaster.get();
    }

    @Override
    public void isLeader() {
        isMaster.set(Boolean.TRUE);
    }

    @Override
    public void notLeader() {
        isMaster.set(Boolean.FALSE);
    }

    @Override
    public void close() throws Exception {
        this.latch.close();
        scheduledService.shutdownNow();
    }

    @Override
    public void run() {
        logger.info("i am master:{} ...", isMaster.get());

        failoverStrategy.setIsMaster(isMaster.get());
        scheduleJobBack.setIsMaster(isMaster.get());
    }

}
