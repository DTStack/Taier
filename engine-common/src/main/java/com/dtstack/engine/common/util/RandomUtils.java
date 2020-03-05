package com.dtstack.engine.common.util;

import java.util.Collection;
import java.util.Random;

public class RandomUtils {
    private static Random random;

    //双重校验锁获取一个Random单例
    public static Random getRandom() {
        if (random == null) {
            synchronized (RandomUtils.class) {
                if (random == null) {
                    random = new Random();
                }
            }
        }

        return random;
    }

    public static int getRandomInt(int max) {
        return Math.abs(getRandom().nextInt()) % max;
    }

    public static <V> V getRandomValueFromMap(Collection<V> list) {
        if (list.isEmpty()) {
            return null;
        }
        int rn = getRandomInt(list.size());
        int i = 0;
        for (V value : list) {
            if (i == rn) {
                return value;
            }
            i++;
        }
        return null;
    }
}
