package com.dtstack.engine.entrance;


import com.dtstack.engine.dtscript.service.zk.ShardConsistentHash;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class TestHash {
    private static final String IP_PREFIX = "shard";// 机器节点IP前缀

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        for (int k=0;k<20;k++){
        Map<String, Integer> map = new HashMap<String, Integer>();// 每台真实机器节点上保存的记录条数

        ShardConsistentHash consistentHash = new ShardConsistentHash(Lists.newArrayList("shard0"));// 每台真实机器引入100个虚拟节点
        map.put(IP_PREFIX+0,0);
        for (int i = 0; i < 500; i++) {
            String data = generateUniqueSign();
            String node = consistentHash.get(data);
            map.put(node, map.get(node) + 1);
        }

        consistentHash.add("shard1");
        map.put(IP_PREFIX+1,0);
        for (int i = 0; i < 500; i++) {
            String data = generateUniqueSign();
            String node = consistentHash.get(data);
            map.put(node, map.get(node) + 1);
        }

        for (int i = 2; i <= 11; i++) {
            map.put(IP_PREFIX + i, 0);
            consistentHash.add(IP_PREFIX + i);
        }
        for (int i = 0; i < 5000; i++) {
            String data = generateUniqueSign();
            String node = consistentHash.get(data);
            map.put(node, map.get(node) + 1);
        }

        for (int i = 12; i <= 22; i++) {
            map.put(IP_PREFIX + i, 0);
            consistentHash.add(IP_PREFIX + i);
        }
        for (int i = 0; i < 5000; i++) {
            String data = generateUniqueSign();
            String node = consistentHash.get(data);
            map.put(node, map.get(node) + 1);
        }


        // 打印每台真实机器节点保存的记录条数
        for (int i = 1; i <= 11; i++) {
            System.out.println(IP_PREFIX + i + "node record num："
                    + map.get("shard" + i));
        }
        }
    }


    private static int length = 8;
    private static Random random = new Random();
    private static Set<String> has = new HashSet<>();

    public static String generateUniqueSign() {

        String uniqueSign;
        int index = 100;
        while (true) {
            try {
                if (index > 100) {
                    Thread.sleep(100);
                }
                index = index + 1;
                uniqueSign = UUID.randomUUID().toString().replace("-", "");
                int len = uniqueSign.length();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < length; i++) {
                    int a = random.nextInt(len) + 1;
                    sb.append(uniqueSign.substring(a - 1, a));
                }
                uniqueSign = sb.toString();

                if (!has.add(uniqueSign)) {
                    continue;
                }
                break;
            } catch (Exception e) {
            }
        }
        return uniqueSign;
    }
}