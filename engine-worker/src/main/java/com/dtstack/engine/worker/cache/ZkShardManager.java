package com.dtstack.engine.worker.cache;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.service.zookeeper.ShardConsistentHash;
import com.dtstack.engine.service.zookeeper.ZkDistributed;
import com.dtstack.engine.service.data.BrokerDataNode;
import com.dtstack.engine.service.data.BrokerDataShard;
import com.dtstack.engine.service.data.BrokerDataTreeMap;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * data 数据分配及空闲检测
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class ZkShardManager implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ZkShardManager.class);

    private static final long IDLE_CHECK_INTERVAL = 5000;
    private static final long DATA_CLEAN_INTERVAL = 1000;
    private static final long SHARD_IDLE_TIMES = 10;

    private static final String SHARD_NODE = "shard";
    private static final String SHARD_LOCK = "_lock";

    private static ZkShardManager zkShardManager = new ZkShardManager();

    public static ZkShardManager getInstance() {
        return zkShardManager;
    }

    private ZkDistributed zkDistributed;

    private final AtomicInteger shardSequence = new AtomicInteger(1);
    private ShardConsistentHash consistentHash;
    private Map<String, BrokerDataShard> shards;
    private Map<String, AtomicInteger> shardIdles = Maps.newHashMap();
    private Map<String, InterProcessMutex> mutexs = Maps.newConcurrentMap();
    private Map<String, ReentrantLock> cacheShardLocks = Maps.newConcurrentMap();

    public void init(ZkDistributed zkDistributed) {
        this.zkDistributed = zkDistributed;
        BrokerDataNode brokerDataNode = ZkLocalCache.getInstance().getBrokerData();
        this.shards = brokerDataNode.getShards();
        this.consistentHash = brokerDataNode.getConsistentHash();
        if (consistentHash.getSize() == 0) {
            createShardNode(1);
        } else {
            for (String shardName : shards.keySet()) {
                initShardNode(shardName);
            }
        }
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory("ZkShardListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                IDLE_CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
        scheduledService.scheduleWithFixedDelay(
                () -> {
                    for (Map.Entry<String, BrokerDataShard> shardEntry : shards.entrySet()) {
                        BrokerDataShard brokerDataShard = shardEntry.getValue();
                        BrokerDataTreeMap<String, Byte> shardData = brokerDataShard.getMetas();
                        Iterator<Map.Entry<String, Byte>> it = shardData.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, Byte> data = it.next();
                            if (RdosTaskStatus.needClean(data.getValue().intValue())) {
                                brokerDataShard.getNewVersion().incrementAndGet();
                                it.remove();
                            }
                        }
                    }
                },
                0,
                DATA_CLEAN_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public InterProcessMutex getShardLock(String shard) {
        return mutexs.get(shard);
    }

    public ReentrantLock tryLock(String shard) {
        return cacheShardLocks.get(shard);
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
                ReentrantLock cacheLock = this.tryLock(dShard);
                try {
                    if (cacheLock.tryLock(30, TimeUnit.SECONDS) && lock.acquire(30, TimeUnit.SECONDS)) {
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
                                cacheShardLocks.remove(dShard);
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
                    } finally {
                        cacheLock.unlock();
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
        cacheShardLocks.put(shardName, new ReentrantLock());
    }

    public void createShardNode(float nodeNum) {
        acquireBrokerLock();
        try {
            for (int i = 0; i < nodeNum; i++) {
                String shardName = SHARD_NODE + shardSequence.getAndIncrement();
                zkDistributed.createBrokerDataShard(shardName);
                InterProcessMutex mutex = zkDistributed.createBrokerDataShardLock(shardName + SHARD_LOCK);
                mutexs.put(shardName, mutex);
                ReentrantLock lock = new ReentrantLock();
                lock.lock();
                cacheShardLocks.put(shardName, lock);
                consistentHash.add(shardName);
                shards.put(shardName, BrokerDataShard.initBrokerDataShard());
            }
            logger.info("ZkShardManager resizeShard-create:{} start", nodeNum);
            resizeShard();
            logger.info("ZkShardManager resizeShard-create:{} end", nodeNum);
        } finally {
            releaseLock();
        }
    }

    private void resizeShard() {
        for (Map.Entry<String, BrokerDataShard> shardEntry : shards.entrySet()) {
            String shard = shardEntry.getKey();
            BrokerDataShard brokerDataShard = shardEntry.getValue();
            BrokerDataTreeMap<String, Byte> shardData = brokerDataShard.getMetas();
            Iterator<Map.Entry<String, Byte>> it = shardData.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Byte> data = it.next();
                String zkTaskId = data.getKey();
                String newShard = consistentHash.get(zkTaskId);
                if (newShard.equals(shard)) {
                    continue;
                }
                shards.get(newShard).put(zkTaskId, data.getValue());
                it.remove();
            }
        }
    }

    private void acquireBrokerLock() {
        for (ReentrantLock lock : cacheShardLocks.values()) {
            lock.lock();
        }
    }

    private void releaseLock() {
        for (ReentrantLock lock : cacheShardLocks.values()) {
            lock.unlock();
        }
    }

}
