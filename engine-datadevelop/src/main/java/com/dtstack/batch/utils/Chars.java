//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dtstack.batch.utils;

public class Chars {
    public static final String LINE_SEPARATOR = System.lineSeparator();

    public Chars() {
    }

    public static final boolean isChinese(char c) {
        return c >= 19968 && c <= '龥';
    }

    public static final boolean isCapital(char c) {
        return c >= 'A' && c <= 'Z';
    }
}
