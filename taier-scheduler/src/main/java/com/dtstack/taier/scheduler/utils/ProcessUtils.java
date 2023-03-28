package com.dtstack.taier.scheduler.utils;

public class ProcessUtils {

    public static Boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

}
