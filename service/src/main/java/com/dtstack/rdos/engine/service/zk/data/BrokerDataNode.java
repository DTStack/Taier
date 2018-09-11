package com.dtstack.rdos.engine.service.zk.data;

import com.dtstack.rdos.engine.service.zk.ShardConsistentHash;

import java.util.Map;
import java.util.Set;

/**
 * @author toutian
 */
public class BrokerDataNode {

    private Map<String, BrokerDataShard> shards;
    private ShardConsistentHash consistentHash;

    public BrokerDataNode(Map<String, BrokerDataShard> brokerDataShardMap) {
        Set<String> shardNames = null;
        if (brokerDataShardMap != null && brokerDataShardMap.size() > 0) {
            this.shards = brokerDataShardMap;
            shardNames = brokerDataShardMap.keySet();
        }
        this.consistentHash = new ShardConsistentHash(shardNames);
    }

    public String getShard(String zkTaskId) {
        return consistentHash.get(zkTaskId);
    }

    public int getDataSize() {
        return shards.values().stream().map(t -> t.metaSize()).reduce(Integer::sum).orElse(0);
    }

    public Map<String, BrokerDataShard> getShards() {
        return shards;
    }

    public ShardConsistentHash getConsistentHash() {
        return consistentHash;
    }

    public void putElement(String key, Byte value) {
        String shard = getShard(key);
        shards.get(shard).put(key, value);
    }

    public void clear() {
        shards.values().forEach(t -> t.getMetas().clear());
    }
}
