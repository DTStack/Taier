package com.dtstack.engine.master.cache;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.hash.ShardConsistentHash;
import com.dtstack.engine.common.hash.Shard;
import com.dtstack.engine.common.hash.ShardData;
import com.dtstack.engine.master.resource.ComputeResourceType;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * data 数据分片及空闲检测
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class ShardManager implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ShardManager.class);

    private static final long DATA_CLEAN_INTERVAL = 1000;
    private static final String SHARD_NODE = "shard";
    private static final ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(ComputeResourceType.values().length, new CustomThreadFactory("ShardManager"));

    private AtomicInteger shardSequence = new AtomicInteger(1);
    private Shard shard = new Shard();
    private Map<String, ShardData> shards;
    private ShardConsistentHash consistentHash;
    private Map<String, ReentrantLock> cacheShardLocks = Maps.newConcurrentMap();

    public ShardManager() {
        this.shards = shard.getShards();
        this.consistentHash = shard.getConsistentHash();
        if (consistentHash.getSize() == 0) {
            createShardNode(1);
        } else {
            for (String shardName : shards.keySet()) {
                initShardNode(shardName);
            }
        }
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                DATA_CLEAN_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public String getShardName(String jobId) {
        return consistentHash.get(jobId);
    }

    public ShardData getShardData(String jobId) {
        String shardName = consistentHash.get(jobId);
        return shards.get(shardName);
    }

    public Map<String, ShardData> getShards() {
        return shards;
    }

    public int getShardDataSize() {
        return shard.getDataSize();
    }

    public ReentrantLock tryLock(String shard) {
        return cacheShardLocks.get(shard);
    }

    @Override
    public void run() {
        for (Map.Entry<String, ShardData> shardEntry : shards.entrySet()) {
            ShardData shardData = shardEntry.getValue();
            ConcurrentSkipListMap<String, Integer> metas = shardData.getMetas();
            Iterator<Map.Entry<String, Integer>> it = metas.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> jobWithStatus = it.next();
                if (RdosTaskStatus.needClean(jobWithStatus.getValue())) {
                    it.remove();
                    shardData.getNewVersion().incrementAndGet();
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
                shards.put(shardName, ShardData.initShardData());
            }
            logger.info("ZkShardManager resizeShard-create:{} start", nodeNum);
            resizeShard();
            logger.info("ZkShardManager resizeShard-create:{} end", nodeNum);
        } finally {
            releaseLock();
        }
    }

    private void resizeShard() {
        for (Map.Entry<String, ShardData> shardEntry : shards.entrySet()) {
            String shard = shardEntry.getKey();
            ShardData brokerDataShard = shardEntry.getValue();
            ConcurrentSkipListMap<String, Integer> shardData = brokerDataShard.getMetas();
            Iterator<Map.Entry<String, Integer>> it = shardData.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> data = it.next();
                String jobId = data.getKey();
                String newShard = consistentHash.get(jobId);
                if (newShard.equals(shard)) {
                    continue;
                }
                shards.get(newShard).put(jobId, data.getValue());
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
