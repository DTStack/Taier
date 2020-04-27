package com.dtstack.engine.worker.enums;

import com.dtstack.engine.common.enums.EJobType;

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

    public static final String CLASSLOADER_DTSTACK_CACHE = "classloader.dtstack-cache";
    public static final String CLASSLOADER_DTSTACK_CACHE_TRUE = "true";
    public static final String CLASSLOADER_DTSTACK_CACHE_FALSE = "false";
}
