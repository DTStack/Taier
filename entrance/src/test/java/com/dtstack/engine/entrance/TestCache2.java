package com.dtstack.engine.entrance;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dtscript.service.zk.data.BrokerDataNode;
import com.dtstack.engine.dtscript.service.zk.data.BrokerDataShard;
import com.dtstack.engine.dtscript.service.zk.data.BrokerDataTreeMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/12
 */
public class TestCache2 {

    private BrokerDataNode localCache;

    public Map<String, BrokerDataNode> initMemTaskStatus() {
        Map<String, BrokerDataNode> memTaskStatus = Maps.newConcurrentMap();
        for (String broker : Lists.newArrayList("local1", "local2")) {
            Map<String, BrokerDataShard> brokerDataShardMap = Maps.newConcurrentMap();
            BrokerDataShard shard1 = brokerDataShardMap.computeIfAbsent("shard1", k -> BrokerDataShard.initBrokerDataShard());
            BrokerDataShard shard2 = brokerDataShardMap.computeIfAbsent("shard2", k -> BrokerDataShard.initBrokerDataShard());
            BrokerDataNode brokerDataNode = memTaskStatus.computeIfAbsent(broker, k -> new BrokerDataNode(brokerDataShardMap));
        }
        localCache = memTaskStatus.get("local1");
        return memTaskStatus;
    }

    public BrokerDataNode getLocalCache() {
        return localCache;
    }

    public static void main(String[] args) {


        Map<String, BrokerDataNode> allCache = new TestCache2().initMemTaskStatus();
        TestCache2 case2 = new TestCache2();
        case2.initMemTaskStatus();
        BrokerDataNode localNode = case2.getLocalCache();

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory("ZkShardListener"));
        scheduledService.scheduleWithFixedDelay(
                () -> {
                    for (Map.Entry<String, BrokerDataShard> shardEntry : localNode.getShards().entrySet()) {
                        BrokerDataShard brokerDataShard = shardEntry.getValue();
                        BrokerDataTreeMap<String, Byte> shardData = brokerDataShard.getMetas();
                        Iterator<Map.Entry<String, Byte>> it = shardData.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, Byte> data = it.next();
                            if (RdosTaskStatus.needClean(data.getValue().intValue())) {
                                brokerDataShard.getNewVersion().incrementAndGet();
                                shardData.remove(data.getKey());
                            }
                        }
                    }
                },
                0,
                1000,
                TimeUnit.MILLISECONDS);


        System.out.println(localNode.getShards());
        case2.updateLocalMemTaskStatus("1flink_09121039855", 5);
        System.out.println(localNode.getShards());

        Map<String, BrokerDataNode> newww = Maps.newHashMap();
        allCache.putAll(newww);

        case2.updateLocalMemTaskStatus("1flink_09121039856", 5);
        System.out.println(localNode.getShards());

        case2.updateLocalMemTaskStatus("1flink_09121039857", 5);
        System.out.println(localNode.getShards());

        case2.updateLocalMemTaskStatus("1flink_09121039858", 8);
    }


    public void updateLocalMemTaskStatus(String zkTaskId, Integer status) {
        if (zkTaskId == null || status == null) {
            throw new UnsupportedOperationException();
        }
        String shard = localCache.getShard(zkTaskId);
        localCache.getShards().get(shard).put(zkTaskId, status.byteValue());


    }
}
