package com.dtstack.engine.entrance;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.service.zk.data.BrokerDataNode;
import com.dtstack.engine.service.zk.data.BrokerDataShard;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/11
 */
public class CacheTest {
    private volatile Map<String, BrokerDataNode> core;
    private volatile BrokerDataNode localDataCache;
    private volatile AtomicBoolean requiresCopyOnWrite = new AtomicBoolean(false);

    public CacheTest() {
        this.core = Maps.newConcurrentMap();
        Map<String, BrokerDataShard> brokerDataShardMap = new HashMap<>();
        brokerDataShardMap.put("default",BrokerDataShard.initBrokerDataShard());
        localDataCache = core.computeIfAbsent("local",k->new BrokerDataNode(brokerDataShardMap));
    }

    public Map<String, BrokerDataNode> cloneData() {
        try {
            return new ConcurrentHashMap<String, BrokerDataNode>(core);
        } finally {
            requiresCopyOnWrite.set(true);
        }
    }

    private void copy() {
        if (requiresCopyOnWrite.compareAndSet(true, false)) {
            core = new ConcurrentHashMap<>(core);
            localDataCache = core.get("local");
        }
    }

    public Map<String, BrokerDataNode> getCore() {
        return core;
    }

    public BrokerDataNode getLocalDataCache() {
        return localDataCache;
    }

    public static void main(String[] args) throws Exception{
        CacheTest test = new CacheTest();
        test.updateLocalMemTaskStatus("001_2",1);
        System.out.println(test.getCore().get("local").getShards().get("default").getMetas());

        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        CountDownLatch latch3 = new CountDownLatch(1);
        ExecutorService submitExecutor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("submitDealer"));
        submitExecutor.submit(() -> {
            try {
                Map<String, BrokerDataNode> core2 = test.cloneData();
                latch3.countDown();
                System.out.println("11111:"+core2.get("local").getShards().get("default").getMetas());
                latch1.await();
                System.out.println("11112:"+core2.get("local").getShards().get("default").getMetas());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                latch2.countDown();
            }
        });
        submitExecutor.submit(() -> {
            try {
                latch3.await();
                test.updateLocalMemTaskStatus("001_2",5);
                test.updateLocalMemTaskStatus("001_3",5);
                test.updateLocalMemTaskStatus("001_4",5);
                test.updateLocalMemTaskStatus("001_5",5);
                test.updateLocalMemTaskStatus("001_6",5);
                System.out.println("22222:"+test.getCore().get("local").getShards().get("default").getMetas());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                latch1.countDown();
            }
        });



        latch2.await();
        System.out.println("33333:"+test.getCore().get("local").getShards().get("default").getMetas());
        submitExecutor.shutdownNow();
    }

    public void updateLocalMemTaskStatus(String zkTaskId, Integer status) {
        if (zkTaskId == null || status == null) {
            throw new UnsupportedOperationException();
        }
        copy();
        String shard = localDataCache.getShard(zkTaskId);
        localDataCache.getShards().get(shard).put(zkTaskId, status.byteValue());
    }
}
