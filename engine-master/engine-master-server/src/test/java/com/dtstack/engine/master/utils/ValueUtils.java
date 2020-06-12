package com.dtstack.engine.master.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ValueUtils {
    private static AtomicLong id = new AtomicLong(-1080L);
    private static final String strValue = "test__engine2020__";
    private static AtomicInteger incrementedValue = new AtomicInteger(100);
    private static Map<String, Long> ids = new ConcurrentHashMap<>();
    private static Map<String, Map<String, String>> strs = new ConcurrentHashMap<>();

    public static Long changedIdForDiffMethod() {
        return getId(getMethodName());
    }

    public static String changedStrForDiffMethod(String identifier) {
        return getStr(getMethodName(), identifier);
    }

    public static Long getId(String methodName) {
        if (ids.containsKey(methodName)) {
            return ids.get(methodName);
        } else {
            ids.put(methodName, id.get());
            return id.getAndDecrement();
        }
    }

    public static String getStr(String methodName, String identifier) {
        if (strs.containsKey(methodName)) {
            Map<String, String> map = strs.get(methodName);
            if (map.containsKey(identifier)) {
                return map.get(identifier);
            } else {
                String result = strValue + incrementedValue.get();
                map.put(identifier, result);
                incrementedValue.getAndIncrement();
                return result;
            }
        } else {
            strs.put(methodName, new ConcurrentHashMap<>());
            String result = strValue + incrementedValue.get();
            strs.get(methodName).put(identifier, result);
            incrementedValue.getAndIncrement();
            return result;
        }
    }

    private static String getMethodName() {
        StackTraceElement[] stack =Thread.currentThread().getStackTrace();
        StackTraceElement method = stack[3];
        return method.getMethodName();
    }

    private ValueUtils() {}
}
