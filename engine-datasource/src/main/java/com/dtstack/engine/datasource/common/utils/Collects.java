package com.dtstack.engine.datasource.common.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 数组集合工具类
 * @description:
 * @author: liuxx
 * @date: 2021/3/12
 */
public class Collects {


    public final static boolean isEmpty(String[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    public final static boolean isNotEmpty(String[] array) {
        return !isEmpty(array);
    }

    public final static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.size() == 0;
    }

    public final static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 构建一个不可增删改的List
     *
     * @return 空的List集合
     */
    public final static List emptyList() {
        return Collections.emptyList();
    }

}
