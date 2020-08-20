package com.dtstack.engine.common.util;

/**
 * company: www.dtstack.com
 * @author: toutian
 * create: 2020/8/20
 */
public class SleepUtil {
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
