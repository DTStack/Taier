/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 反射工具类
 *
 * @author ：wangchuan
 * date：Created in 上午10:18 2021/5/21
 * company: www.dtstack.com
 */
public class ReflectUtil {

    /**
     * 判断类中指定字段是否存在
     *
     * @param c         class 类型
     * @param fieldName 字段名称
     * @param <T>       对象类型
     * @return 是否存在
     */
    public static <T> Boolean fieldExists(Class<T> c, String fieldName) {
        if (Objects.isNull(c) || StringUtils.isBlank(fieldName)) {
            throw new SourceException("class or fieldName can not be null...");
        }
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定对象中指定字段的值，指定字段不存在则返回默认值，不抛异常
     *
     * @param returnClassType            返回值的 class 类型
     * @param obj                        对象
     * @param fieldName                  字段名称
     * @param fieldNotExistsDefaultValue 字段不存在情况的默认值
     * @param <T>                        返回值对象类型
     * @return 字段值
     */
    public static <T> T getFieldValueNotThrow(Class<T> returnClassType, Object obj, String fieldName, T fieldNotExistsDefaultValue) {
        return getFieldValueNotThrow(returnClassType, obj, fieldName, fieldNotExistsDefaultValue, null);
    }

    /**
     * 获取指定对象中指定字段的值，指定字段不存在则返回默认值，不抛异常，当返回值为 null 时返回设置的默认值
     *
     * @param returnClassType            返回值的 class 类型
     * @param obj                        对象
     * @param fieldName                  字段名称
     * @param fieldNotExistsDefaultValue 字段不存在情况的默认值
     * @param valueIsNullDefaultValue    字段值为 null 时的默认值
     * @param <T>                        返回值对象类型
     * @return 字段值
     */
    public static <T> T getFieldValueNotThrow(Class<T> returnClassType, Object obj, String fieldName, T fieldNotExistsDefaultValue, T valueIsNullDefaultValue) {
        return getFieldValue(returnClassType, false, obj, fieldName, fieldNotExistsDefaultValue, valueIsNullDefaultValue);
    }

    /**
     * 获取指定对象中指定字段的值，指定字段不存则抛异常
     *
     * @param returnClassType 返回值的 class 类型
     * @param obj             对象
     * @param fieldName       字段名称
     * @param <T>             返回值对象类型
     * @return 字段值
     */
    public static <T> T getFieldValueThrow(Class<T> returnClassType, Object obj, String fieldName) {
        return getFieldValueThrow(returnClassType, obj, fieldName, null);
    }

    /**
     * 获取指定对象中指定字段的值，指定字段不存则抛异常，当返回值为 null 时返回设置的默认值
     *
     * @param returnClassType         返回值的 class 类型
     * @param obj                     对象
     * @param fieldName               字段名称
     * @param valueIsNullDefaultValue 字段值为 null 时的默认值
     * @param <T>                     返回值对象类型
     * @return 字段值
     */
    public static <T> T getFieldValueThrow(Class<T> returnClassType, Object obj, String fieldName, T valueIsNullDefaultValue) {
        return getFieldValue(returnClassType, true, obj, fieldName, null, valueIsNullDefaultValue);
    }

    /**
     * 获取指定对象中指定字段的值，指定字段不存在则返回默认值
     *
     * @param returnClassType            返回值的 class 类型
     * @param fieldNotExistIsThrow       字段不存在时否抛出异常
     * @param obj                        对象
     * @param fieldName                  字段名称
     * @param fieldNotExistsDefaultValue 字段不存在情况的默认值
     * @param valueIsNullDefaultValue    字段值为 null 时的默认值
     * @param <T>                        返回值对象类型
     * @return 字段值
     */
    @SuppressWarnings("unchecked")
    private static <T> T getFieldValue(Class<T> returnClassType, Boolean fieldNotExistIsThrow, Object obj, String fieldName, T fieldNotExistsDefaultValue, T valueIsNullDefaultValue) {
        if (Objects.isNull(returnClassType) || Objects.isNull(obj) || StringUtils.isBlank(fieldName)) {
            throw new SourceException("returnClassType or obj or fieldName can not be null...");
        }
        Boolean fieldExists = fieldExists(obj.getClass(), fieldName);
        if (BooleanUtils.isFalse(fieldExists)) {
            // 如果字段不存在且设置抛异常则抛出异常，不然返回字段不存在的默认值
            if (BooleanUtils.isTrue(fieldNotExistIsThrow)) {
                throw new SourceException(String.format("field [%s] is not exists in objType [%s]", fieldName, obj.getClass().getName()));
            }
            return fieldNotExistsDefaultValue;
        }
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object returnObj = field.get(obj);
            // 如果字段值为 null，并且设置返回默认值的时候，return 默认值
            if (Objects.isNull(returnObj) && Objects.nonNull(valueIsNullDefaultValue)) {
                return valueIsNullDefaultValue;
            }
            return (T) returnObj;
        } catch (Exception e) {
            throw new SourceException(String.format("get field value failed, fieldName : %s, objType : %s. %s", fieldName, obj.getClass().getName(), e.getMessage()));
        }
    }

    /**
     * 重新设置静态/非静态变量字段属性
     *
     * @param c           class 类型
     * @param fieldName   字段名
     * @param obj         对象
     * @param targetParam 目标值
     * @param <T>         对象类型
     */
    public static <T> void setField(Class<T> c, String fieldName, Object obj, Object targetParam) {
        try {
            Field field = c.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, targetParam);
        } catch (Exception e) {
            throw new SourceException(String.format("set class: %s field: %s fail", c.getName(), fieldName));
        }
    }
}
