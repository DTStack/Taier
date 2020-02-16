package com.dtstack.engine.master.cache;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.hash.ShardConsistentHash;
import com.dtstack.engine.master.data.BrokerDataNode;
import com.dtstack.engine.master.data.BrokerDataShard;
import com.dtstack.engine.master.data.BrokerDataTreeMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
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
@Component
public class ZkShardManager implements Runnable, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ZkShardManager.class);

    private static final long DATA_CLEAN_INTERVAL = 1000;

    private static final String SHARD_NODE = "shard";

    @Autowired
    private ZkLocalCache zkLocalCache;

    private final AtomicInteger shardSequence = new AtomicInteger(1);
    private ShardConsistentHash consistentHash;
    private Map<String, BrokerDataShard> shards;
    private Map<String, ReentrantLock> cacheShardLocks = Maps.newConcurrentMap();

    @Override
    public void afterPropertiesSet() throws Exception {
        BrokerDataNode brokerDataNode = zkLocalCache.getBrokerData();
        this.shards = brokerDataNode.getShards();
        this.consistentHash = brokerDataNode.getConsistentHash();
        if (consistentHash.getSize() == 0) {
            createShardNode(1);
        } else {
            for (String shardName : shards.keySet()) {
                initShardNode(shardName);
            }
        }
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("ZkShardManager"));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                DATA_CLEAN_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public ReentrantLock tryLock(String shard) {
        return cacheShardLocks.get(shard);
    }

    @Override
    public void run() {
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
    }

    private void initShardNode(String shardName) {
        cacheShardLocks.put(shardName, new ReentrantLock());
    }

    public void createShardNode(float nodeNum) {
        acquireBrokerLock();
        try {
            for (int i = 0; i < nodeNum; i++) {
                String shardName = SHARD_NODE + shardSequence.getAndIncrement();
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
