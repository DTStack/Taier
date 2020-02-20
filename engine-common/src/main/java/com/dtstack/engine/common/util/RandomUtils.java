package com.dtstack.engine.common.util;

import java.util.Map;
import java.util.Random;

public class RandomUtils {
    private static Random random;

    //双重校验锁获取一个Random单例
    public static Random getRandom() {
        if(random==null){
            synchronized (RandomUtils.class) {
                if(random==null){
                    random =new Random();
                }
            }
        }

        return random;
    }

    public static int getRandomInt(int max) {
        return Math.abs(getRandom().nextInt())%max;
    }

    public static <K,V> V getRandomValueFromMap(Map<K,V> map) {
        int rn = getRandomInt(map.size());
        int i = 0;
        for (V value : map.values()) {
            if(i==rn){
                return value;
            }
            i++;
        }
        return null;
    }
}
