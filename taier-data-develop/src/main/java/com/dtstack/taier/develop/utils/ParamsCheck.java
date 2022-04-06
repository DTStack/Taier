package com.dtstack.taier.develop.utils;

public class ParamsCheck {

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new RuntimeException("参数不正确");
        } else {
            return reference;
        }
    }

    public static <T> T checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new RuntimeException(msg);
        } else {
            return reference;
        }
    }
}