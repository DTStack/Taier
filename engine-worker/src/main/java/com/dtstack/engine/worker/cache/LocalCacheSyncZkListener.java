package com.dtstack.engine.worker.cache;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.service.zookeeper.ZkDistributed;
import com.dtstack.engine.service.data.BrokerDataShard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public class LocalCacheSyncZkListener implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LocalCacheSyncZkListener.class);

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
    private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();
    private static long CHECK_INTERVAL = 2000;
    private int logOutput = 0;

    public LocalCacheSyncZkListener() {
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("LocalCacheSyncZkListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
        zkLocalCache.setLocalCacheSyncZkListener(this);
    }

    @Override
    public void run() {
        try {
            logOutput++;
            if (PublicUtil.count(logOutput, 5)) {
                logger.warn("LocalCacheSyncZkListener start again");
            }
            syncLocalCache();
        } catch (Throwable e) {
            logger.error("LocalCacheSyncZkListener error:{}", ExceptionUtil.getErrorMessage(e));
        }
    }

    private void syncLocalCache() {
        String localAddress = zkDistributed.getLocalAddress();
        Map<String, BrokerDataShard> shards = zkLocalCache.cloneShardData();
        for (Map.Entry<String, BrokerDataShard> entry : shards.entrySet()) {
            if (entry.getValue().getVersion() == entry.getValue().getNewVersion().longValue()) {
                continue;
            }
            entry.getValue().setVersion(entry.getValue().getNewVersion().longValue());
            zkDistributed.synchronizedBrokerDataShard(localAddress, entry.getKey(), entry.getValue(), true);
        }
    }

}
