package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.service.zk.ConsistentHash;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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

    private static final float DATA_LENGTH = 200;
    private static final long CHECK_INTERVAL = 5;
    private static final long SHARD_IDLE_TIMES = 10;
    private static final int NUMBER_OF_REPLICAS = 5;

    private static final String SHARD_NODE = "shard";
    private static final String SHARD_LOCK = "_lock";
    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private final AtomicInteger shardSequence = new AtomicInteger(1);
    //一致性hash
    private ConsistentHash<String> shardsCsist = null;
    private Map<String, AtomicInteger> shardIdles = Maps.newHashMap();
    private Map<String, InterProcessMutex> mutexs = Maps.newHashMap();

    public ZkShardListener() {
        shardsCsist = new ConsistentHash<>(NUMBER_OF_REPLICAS, Lists.newArrayList());
        createShardNode(1);
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("ZkShardListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            List<String> dataShards = zkDistributed.getBrokerDataChildren();
            Map<String, Integer> brokerDataNodeMap = new HashMap<>(dataShards.size());
            int totalSize = 0;
            for (String dShard : dataShards) {
                BrokerDataNode brokerDataNode = zkDistributed.getBrokerDataShard(dShard);
                int size = brokerDataNode.getMetas().size();
                totalSize += size;
                brokerDataNodeMap.put(dShard, size);
            }
            if (totalSize > DATA_LENGTH) {
                float needNode = totalSize / DATA_LENGTH - brokerDataNodeMap.size();
                createShardNode(needNode);
            } else if (brokerDataNodeMap.size() > 1) {
                for (Map.Entry<String, Integer> entry : brokerDataNodeMap.entrySet()) {
                    shardIdleDoubleCheck(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            logger.error("getBrokersChildren error:{}", ExceptionUtil.getErrorMessage(e));
        }
    }

    private void shardIdleDoubleCheck(String dShard, int size) {
        AtomicInteger c = shardIdles.computeIfAbsent(dShard, k -> new AtomicInteger(0));
        int idleCount = c.getAndIncrement();
        if (size == 0) {
            if (idleCount >= SHARD_IDLE_TIMES) {
                try {
                    if (mutexs.get(dShard).acquire(30, TimeUnit.SECONDS)) {
                        BrokerDataNode brokerDataNode = zkDistributed.getBrokerDataShard(dShard);
                        if (brokerDataNode != null && brokerDataNode.getMetas().size() == 0) {
                            zkDistributed.deleteBrokerDataShard(dShard);
                            shardIdles.remove(dShard);
                            shardsCsist.remove(dShard);
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
                        mutexs.remove(dShard);
                        zkDistributed.deleteBrokerDataShardLock(dShard + SHARD_LOCK);
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

    private void createShardNode(float nodeNum) {
        for (int i = 0; i < nodeNum; i++) {
            String shardName = SHARD_NODE + shardSequence.getAndIncrement();
            zkDistributed.createBrokerDataShard(shardName);
            InterProcessMutex mutex = zkDistributed.createBrokerDataShardLock(shardName + SHARD_LOCK);
            mutexs.put(shardName, mutex);
            shardsCsist.add(shardName);
        }
    }
}
