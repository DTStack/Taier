package com.dtstack.taier.develop.utils;

import java.io.File;

public class PathUtils {
    public static String removeMultiSeparatorChar(String path) {
        char[] chars = path.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            char cur = chars[i];
            if (cur == File.separatorChar) {
                char pre = chars[i - 1];
                if (pre == File.separatorChar) {
                    continue;
                }
            }
            stringBuilder.append(cur);
        }
        return stringBuilder.toString();
    }
}