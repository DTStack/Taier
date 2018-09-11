package com.dtstack.rdos.engine.service.zk;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.service.zk.cache.ZkLocalCache;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataShard;
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
public class ZkShardManager implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ZkShardManager.class);

    private static final long CHECK_INTERVAL = 5;
    private static final long SHARD_IDLE_TIMES = 10;

    private static final String SHARD_NODE = "shard";
    private static final String SHARD_LOCK = "_lock";

    private static ZkShardManager zkShardManager = new ZkShardManager();
    public static ZkShardManager getInstance() {
        return zkShardManager;
    }

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private final AtomicInteger shardSequence = new AtomicInteger(1);
    private ShardConsistentHash consistentHash;
    private Map<String, AtomicInteger> shardIdles = Maps.newHashMap();
    private Map<String, InterProcessMutex> mutexs = Maps.newConcurrentMap();

    public void init() {
        BrokerDataNode brokerDataNode = ZkLocalCache.getInstance().getBrokerData();
        this.consistentHash = brokerDataNode.getConsistentHash();
        if (consistentHash.getSize()==0){
            createShardNode(1);
        }
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("ZkShardListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.SECONDS);
    }

    public InterProcessMutex getShardLock(String shard) {
        return mutexs.get(shard);
    }

    @Override
    public void run() {
        try {
            List<String> dataShards = zkDistributed.getBrokerDataChildren(zkDistributed.getLocalAddress());
            Map<String, Integer> brokerDataNodeMap = new HashMap<>(dataShards.size());
            for (String dShard : dataShards) {
                BrokerDataShard brokerDataShard = zkDistributed.getBrokerDataShard(zkDistributed.getLocalAddress(), dShard);
                brokerDataNodeMap.put(dShard, brokerDataShard.metaSize());
            }
            if (brokerDataNodeMap.size() > 1) {
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
                boolean destroy = false;
                InterProcessMutex lock = this.getShardLock(dShard);
                try {
                    if (lock.acquire(30, TimeUnit.SECONDS)) {
                        BrokerDataShard brokerDataShard = zkDistributed.getBrokerDataShard(zkDistributed.getLocalAddress(), dShard);
                        if (brokerDataShard != null && brokerDataShard.metaSize() == 0) {
                            zkDistributed.deleteBrokerDataShard(dShard);
                            shardIdles.remove(dShard);
                            consistentHash.remove(dShard);
                            destroy = true;
                        }
                    }
                } catch (Exception e) {
                    logger.error("{} {}:shardIdleDoubleCheck error:{}", zkDistributed.getLocalAddress(), dShard,
                            ExceptionUtil.getErrorMessage(e));
                } finally {
                    try {
                        if (lock.isAcquiredInThisProcess()) {
                            //先从内存中将锁移除
                            if (destroy) {
                                mutexs.remove(dShard);
                            }
                            //再释放锁
                            lock.release();
                            //清楚锁zk路径
                            if (destroy) {
                                zkDistributed.deleteBrokerDataShardLock(dShard + SHARD_LOCK);
                                logger.info("{} {} SHARD IDLE TIMES out, destroy success", zkDistributed.getLocalAddress(), dShard);
                            }
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

    private void initShardNode(String shardName) {
        InterProcessMutex mutex = zkDistributed.createBrokerDataShardLock(shardName + SHARD_LOCK);
        mutexs.put(shardName, mutex);
        consistentHash.add(shardName);
    }

    public void createShardNode(float nodeNum) {
        for (int i = 0; i < nodeNum; i++) {
            String shardName = SHARD_NODE + shardSequence.getAndIncrement();
            zkDistributed.createBrokerDataShard(shardName);
            InterProcessMutex mutex = zkDistributed.createBrokerDataShardLock(shardName + SHARD_LOCK);
            mutexs.put(shardName, mutex);
            consistentHash.add(shardName);
        }
    }
}
