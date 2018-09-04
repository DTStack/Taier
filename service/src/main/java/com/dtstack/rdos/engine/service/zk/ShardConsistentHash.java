package com.dtstack.rdos.engine.service.zk;

import com.google.common.collect.Lists;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class ShardConsistentHash {

    private static final int NUMBER_OF_REPLICAS = 5;

    private static ShardConsistentHash singleton = new ShardConsistentHash(NUMBER_OF_REPLICAS, Lists.newArrayList());

    public static ShardConsistentHash getInstance() {
        return singleton;
    }

    private MessageDigest md5;
    /**
     * 节点的复制因子,实际节点个数 * numberOfReplicas = 虚拟节点个数
     */
    private int numberOfReplicas;
    /**
     * 存储虚拟节点的hash值到真实节点的映射
     */
    private final SortedMap<Long, String> circle = new TreeMap<Long, String>();

    private ShardConsistentHash() {
    }

    public ShardConsistentHash(int numberOfReplicas, Collection<String> shards) {
        this.numberOfReplicas = numberOfReplicas;
        for (String shard : shards) {
            add(shard);
        }
    }

    public void add(String shard) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(getHash(shard + i), shard);
        }
    }

    public void remove(String shard) {
        for (int i = 0; i < numberOfReplicas; i++) {
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
        if (md5 == null) {
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("no md5 algrithm found");
            }
        }
        md5.reset();
        md5.update(key.getBytes());
        byte[] bKey = md5.digest();
        //具体的哈希函数实现细节--每个字节 & 0xFF 再移位
        long result = ((long) (bKey[3] & 0xFF) << 24)
                | ((long) (bKey[2] & 0xFF) << 16
                | ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF));
        return result & 0xffffffffL;
    }

}
