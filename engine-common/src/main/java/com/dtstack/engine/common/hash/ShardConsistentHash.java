package com.dtstack.engine.common.hash;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;


/**
 * 切忌要保证线程安全
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class ShardConsistentHash {

    /**
     * 节点的复制因子,实际节点个数 * numberOfReplicas = 虚拟节点个数
     */
    private static final int NUMBER_OF_REPLICAS = 100;
    /**
     * 存储虚拟节点的hash值到真实节点的映射
     */
    private final SortedMap<Long, String> circle = new ConcurrentSkipListMap<>();

    public ShardConsistentHash() {
    }

    /**
     * 仅用于节点宕机后数据迁移，计算其他机器节点任务分配的分片.
     * 其他情况下请使用单例引用对象
     */
    public ShardConsistentHash(Collection<String> shards) {
        if (shards != null && !shards.isEmpty()) {
            for (String shard : shards) {
                add(shard);
            }
        }
    }

    public void add(String shard) {
        for (int i = 0; i < NUMBER_OF_REPLICAS; i++) {
            circle.put(getHash(shard + i), shard);
        }
    }

    public void remove(String shard) {
        for (int i = 0; i < NUMBER_OF_REPLICAS; i++) {
            circle.remove(getHash(shard + i));
        }
    }

    public String get(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        // shard 用String来表示,获得shard在哈希环中的hashCode
        long hash = getHash(key);
        //数据映射在虚拟节点所在环之间,就需要按顺时针方向寻找机器
        if (!circle.containsKey(hash)) {
            SortedMap<Long, String> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    public long getSize() {
        return circle.size();
    }

    private long getHash(String key) {
        try {
            return hash64A(key.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0L;
    }


    public static long hash64A(byte[] key) {
        return hash64A(ByteBuffer.wrap(key), 0x1234ABCD);
    }

    public static long hash64A(ByteBuffer buf, int seed) {
        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }

}
