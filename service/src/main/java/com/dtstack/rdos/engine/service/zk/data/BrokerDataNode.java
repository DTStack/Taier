package com.dtstack.rdos.engine.service.zk.data;

import com.dtstack.rdos.engine.service.zk.ShardConsistentHash;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.Map;

/**
 * @author toutian
 */
public class BrokerDataNode {

    public BrokerDataNode(Map<String, BrokerDataShard> brokerDataShardMap) {
        this.consistentHash = new ShardConsistentHash(5, Lists.newArrayList());
        if (brokerDataShardMap != null && brokerDataShardMap.size() > 0) {
            this.shards = new HashMap<>(brokerDataShardMap.size());
            for (Map.Entry<String, BrokerDataShard> entry : brokerDataShardMap.entrySet()) {
                Map<String, Byte> dataMap = entry.getValue().getMetas();
                BrokerDataInner inner = new BrokerDataInner(entry.getKey(), dataMap);
                consistentHash.add(entry.getKey());
                this.shards.put(entry.getKey(), inner);
            }
        }
    }

    private Map<String, BrokerDataInner> shards;
    private ShardConsistentHash consistentHash;

    public String getShard(String zkTaskId) {
        return consistentHash.get(zkTaskId);
    }

    public long getDataSize() {
        return shards.values().stream().map(t -> t.getSize()).reduce(Long::sum).orElse(0L);
    }

    public Map<String, BrokerDataInner> getShards() {
        return shards;
    }

    public void putElement(String key, Byte value) {
        String shard = getShard(key);
        shards.get(shard).getShardData().put(key, value);
    }

    public class BrokerDataInner {
        private String shardName;
        private Map<String, Byte> shardData;

        public BrokerDataInner(String shardName, Map<String, Byte> shardData) {
            this.shardName = shardName;
            this.shardData = shardData;
        }

        public String getShardName() {
            return shardName;
        }

        public void setShardName(String shardName) {
            this.shardName = shardName;
        }

        public Map<String, Byte> getShardData() {
            return shardData;
        }

        public void setShardData(Map<String, Byte> shardData) {
            this.shardData = shardData;
        }

        public long getSize() {
            return shardData == null ? 0 : shardData.size();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BrokerDataInner inner = (BrokerDataInner) o;

            if (shardName != null ? !shardName.equals(inner.shardName) : inner.shardName != null) return false;
            return shardData != null ? shardData.equals(inner.shardData) : inner.shardData == null;
        }

        @Override
        public int hashCode() {
            int result = shardName != null ? shardName.hashCode() : 0;
            result = 31 * result + (shardData != null ? shardData.hashCode() : 0);
            return result;
        }
    }
}
