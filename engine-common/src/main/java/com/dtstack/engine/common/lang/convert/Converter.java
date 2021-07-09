package com.dtstack.engine.common.lang.convert;

import java.util.Objects;

/**
 * 类型转换
 *
 * @param <S>
 * @param <T>
 */
public abstract class Converter<S, T> {
    public T convert(S source) {
        if (Objects.isNull(source)) {
            return null;
        } else {
            return doConvert(source);
        }
    }

    protected abstract T doConvert(S source);
}