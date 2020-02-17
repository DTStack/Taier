package com.dtstack.engine.common.hash;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author toutian
 */
public class Shard {

    private Map<String, ShardData> shards = new ConcurrentHashMap<String, ShardData>();
    private ShardConsistentHash consistentHash = new ShardConsistentHash();

    public Shard() {
    }

    public String getShard(String jobId) {
        return consistentHash.get(jobId);
    }

    public int getDataSize() {
        return shards.values().stream().map(ShardData::metaSize).reduce(Integer::sum).orElse(0);
    }

    public Map<String, ShardData> getShards() {
        return shards;
    }

    public ShardConsistentHash getConsistentHash() {
        return consistentHash;
    }
}
