package com.dtstack.engine.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/17
 */
public enum ClassLoaderType {
    //
    NONE,
    //
    CHILD_FIRST,
    //
    PARENT_FIRST,
    //
    CHILD_FIRST_CACHE,
    //
    PARENT_FIRST_CACHE;

    public static ClassLoaderType getClassLoaderType(EJobType jobType) {
        if (EJobType.SYNC == jobType || EJobType.SQL == jobType) {
            return PARENT_FIRST_CACHE;
        } else {
            return PARENT_FIRST;
        }
    }
}
