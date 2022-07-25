package com.dtstack.taier.develop.utils;

/**
 * @Auther: dazhi
 * @Date: 2021/12/27 10:40 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobUtils {

    public static Integer checkLevel(Integer currentLevel, Integer maxLevel) {
        if (currentLevel == null || currentLevel <= 0) {
            return 6;
        }

        if (currentLevel > maxLevel) {
            return maxLevel;
        }

        return currentLevel;
    }
}
