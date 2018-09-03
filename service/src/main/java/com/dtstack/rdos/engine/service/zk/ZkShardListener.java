package com.dtstack.rdos.engine.service.zk;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * data 数据分配及空闲检测
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class ZkShardListener implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ZkShardListener.class);

    private static final int DATA_LENGTH = 200;
    private static final long CHECK_INTERVAL = 5;
    private static final long SHARD_IDLE_TIME = 60;

    private static final String SHARD_NODE = "shard";
    private static final String SHARD_LOCK = "_lock";
    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private final AtomicInteger shardSequence = new AtomicInteger(1);
    //一致性hash
    private ConsistentHash<String> shardsCsist = null;
    private Map<String, AtomicInteger> shardIdles = Maps.newHashMap();
    private Map<String, InterProcessMutex> mutexs = Maps.newHashMap();

    public ZkShardListener() {
        shardsCsist = new ConsistentHash<>(5, Lists.newArrayList(getShardName()));
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("ZkShardListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            List<String> dataShards = zkDistributed.getBrokerDataChildren();
            int totalSize = 0;
            for (String dShard : dataShards) {
                BrokerDataNode brokerDataNode = zkDistributed.getBrokerDataShard(dShard);
                int size = brokerDataNode.getMetas().size();
                totalSize += size;
                shardIdleDoubleCheck(dShard, size);
            }
            if (totalSize / dataShards.size() >= DATA_LENGTH) {
                String shardName = getShardName();
                zkDistributed.createBrokerDataShard(shardName);
                shardsCsist.add(shardName);
                InterProcessMutex mutex = zkDistributed.createBrokerDataShardLock(shardName + SHARD_LOCK);
                mutexs.put(shardName, mutex);
            }
        } catch (Exception e) {
            logger.error("getBrokersChildren error:{}", ExceptionUtil.getErrorMessage(e));
        }
    }

    private void shardIdleDoubleCheck(String dShard, int size) {
        AtomicInteger c = shardIdles.computeIfAbsent(dShard, k -> new AtomicInteger(0));
        int idleCount = c.getAndIncrement();
        if (size == 0) {
            if (idleCount * CHECK_INTERVAL >= SHARD_IDLE_TIME) {
                try {
                    if (mutexs.get(dShard).acquire(30, TimeUnit.SECONDS)) {
                        BrokerDataNode brokerDataNode = zkDistributed.getBrokerDataShard(dShard);
                        if (brokerDataNode != null && brokerDataNode.getMetas().size() == 0) {
                            zkDistributed.deleteBrokerDataShard(dShard);
                            zkDistributed.deleteBrokerDataShard(dShard + SHARD_LOCK);
                            mutexs.remove(dShard);
                            shardIdles.remove(dShard);
                        }
                    }
                } catch (Exception e) {
                    logger.error("{} {}:shardIdleDoubleCheck error:{}", zkDistributed.getLocalAddress(), dShard,
                            ExceptionUtil.getErrorMessage(e));
                } finally {
                    try {
                        if (mutexs.get(dShard).isAcquiredInThisProcess()) {
                            mutexs.get(dShard).release();
                        }
                    } catch (Exception e) {
                        logger.error("{} {}:shardIdleDoubleCheck error:{}", zkDistributed.getLocalAddress(), dShard,
                                ExceptionUtil.getErrorMessage(e));
                    }
                }
            }
        } else {
            c.set(0);
        }
    }

    private String getShardName() {
        return SHARD_NODE + shardSequence.getAndIncrement();
    }
}
