package com.dtstack.engine.master.utils;

import java.io.File;

public class CommonUtils {
    private final static String SEP = File.separator;
    private final static String TEST_CONF = "test_conf";

    public static void sleep(Integer millons) {
        try {
            Thread.sleep(millons);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setUserDirToTest() {
        String parentDir = System.getProperty("user.dir");
        while (parentDir != null && parentDir.contains(SEP)) {
            String testConfDir = parentDir + SEP + TEST_CONF;
            if (new File(testConfDir).exists()) {
                System.setProperty("user.dir.conf", testConfDir);
                return;
            } else {
                parentDir = parentDir.substring(0, parentDir.lastIndexOf(SEP));
            }
        }
        throw new RuntimeException("Not found dir named test_conf");
    }
}
