package com.dtstack.engine.master.utils;

import java.util.HashMap;
import java.util.Map;

public class ValueUtils {
    private static Long id = -1080L;
    private static String strValue = "test__engine2020__";
    private static Integer incrementedValue = 100;
    private static Map<String, Long> ids = new HashMap<>();
    private static Map<String, Map<String, String>> strs = new HashMap<>();

    public static Long changedId() {
        StackTraceElement[] stack =Thread.currentThread().getStackTrace();
        StackTraceElement method = stack[2];
        String methodName = method.getMethodName();
        if (ids.containsKey(methodName)) {
            return ids.get(methodName);
        } else {
            ids.put(methodName, id);
            return id--;
        }
    }

    public static String changedStr(String identifier) {
        StackTraceElement[] stack =Thread.currentThread().getStackTrace();
        StackTraceElement method = stack[2];
        String methodName = method.getMethodName();
        if (strs.containsKey(methodName)) {
            Map<String, String> map = strs.get(methodName);
            if (map.containsKey(identifier)) {
                return map.get(identifier);
            } else {
                String result = strValue + incrementedValue;
                map.put(identifier, result);
                incrementedValue++;
                return result;
            }
        } else {
            strs.put(methodName, new HashMap<>());
            String result = strValue + incrementedValue;
            strs.get(methodName).put(identifier, result);
            incrementedValue++;
            return result;
        }
    }

    public static Long getId(String methodName) {
        return ids.get(methodName);
    }

    public static String getStr(String methodName, String identifier) {
        return strs.get(methodName).get(identifier);
    }

    private ValueUtils() {}
}
