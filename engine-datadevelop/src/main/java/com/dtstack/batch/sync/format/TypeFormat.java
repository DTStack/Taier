package com.dtstack.batch.sync.format;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/4/26
 */
public interface TypeFormat {

    default String formatToString(String str) {
        return str;
    }
}
