package com.dtstack.engine.service.zk.cache;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.service.zk.ZkDistributed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.PublicUtil;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public class ZkSyncLocalCacheListener implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ZkSyncLocalCacheListener.class);

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
    private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

    private static long CHECK_INTERVAL = 2000;
    private int logOutput = 0;

    public ZkSyncLocalCacheListener() {
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("ZkSyncLocalCacheListener"));
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
            if (PublicUtil.count(logOutput, 5)) {
                logger.warn("ZkSyncLocalCacheListener start again");
            }
            Map<String, Integer> zkSize = zkDistributed.getAliveBrokerShardSize();
            zkLocalCache.setZkDataSizeCache(zkSize);
        } catch (Throwable e) {
            logger.error("AllTaskStatusListener error:{}", ExceptionUtil.getErrorMessage(e));
        }
    }
}
