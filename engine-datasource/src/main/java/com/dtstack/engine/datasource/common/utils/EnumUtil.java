package com.dtstack.engine.datasource.common.utils;

import com.dtstack.engine.datasource.common.enums.datasource.DataSourceTypeEnum;

import java.util.function.Predicate;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/22
 */
public class EnumUtil {

    /**
     * 根据传入的方法获取枚举
     *
     * @param tClass
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> T findEnum(Class<T> tClass, Predicate<T> predicate) {
        for (T t : tClass.getEnumConstants()) {
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(findEnum(DataSourceTypeEnum.class, t -> t.getVal().equals(1)));
    }
}
