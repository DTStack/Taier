package com.dtstack.engine.master.utils;

public class CommonUtils {
    public static void sleep(Integer millons) {
        try {
            Thread.sleep(millons);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
