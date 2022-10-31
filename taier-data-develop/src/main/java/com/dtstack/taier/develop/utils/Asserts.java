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

package com.dtstack.taier.develop.utils;


import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.Strings;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;


/**
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a> 2020/12/15
 * @desc 服务业务异常校验
 */
public class Asserts {
    private Asserts() {
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, Object... args) {
        isTrue(expression, errorCode.getDescription(), args);
    }

    /**
     * 判断传入表达式是否为true,若为false,则抛出参数校验失败异常
     *
     * @param expression 需要进行断言的表达式
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assert Fail] - 表达式计算结果必须为True");
    }

    /**
     * 判断传入的表达式是否为true,若为false,则抛出参数校验失败异常
     *
     * @param expression 需进行断言的表达式
     * @param format     异常信息,其中大括号内容为占位符
     * @param args       占位符替换内容
     */
    public static void isTrue(boolean expression, String format, Object... args) {
        if (!expression) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }


    /**
     * 判断传入表达式是否为false,若不为false,则抛出参数校验失败异常
     *
     * @param expression 需进行断言的表达式
     */
    public static void isFalse(boolean expression, ErrorCode errorCode, Object... args) {
        isFalse(expression, errorCode.getDescription(), args);
    }

    /**
     * 判断传入表达式是否为false,若不为false,则抛出参数校验失败异常
     *
     * @param expression 需进行断言的表达式
     */
    public static void isFalse(boolean expression) {
        isFalse(expression, "[Assert Fail] - 表达式计算结果必须为False");
    }

    /**
     * 判断传入的表达式是否为false, 若不为false, 则抛出参数校验异常
     *
     * @param expression 需进行断言的表达式
     * @param format     异常信息,其中大括号内容为占位符
     * @param args       占位符替换内容
     */
    public static void isFalse(boolean expression, String format, Object... args) {
        if (expression) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }


    public static void notNull(Object object, ErrorCode errorCode, Object... args) {
        notNull(object, errorCode.getDescription(), args);
    }

    /**
     * 判断对象是否为非null,若为null则抛出参数校验失败异常
     *
     * @param object 需进行判断的对象
     */
    public static void notNull(Object object) {
        notNull(object, "[Assert Fail] - 入参必须为非null对象");
    }

    /**
     * 判断对象是否为非null,若为null则抛出参数校验失败异常
     *
     * @param object 需进行判断的对象
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static void notNull(Object object, String format, Object... args) {
        if (null == object) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }

    /**
     * 断言数值不为null，且包含于闭区间[start, end]中
     *
     * @param number 该数值
     * @param start  起始区间
     * @param end    结束区间
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static void between(Integer number, Integer start, Integer end, String format, Object... args) {
        if (number < start || number > end) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }


    /**
     * 断言为正数
     *
     * @param intNumber 需进行判断的参数
     * @param format    异常信息,支持slf4j式占位符替换
     * @param args      占位符替换内容
     */
    public static final void positive(Integer intNumber, String format, Object... args) {
        isTrue(intNumber != null && intNumber > 0, format, args);
    }

    public static final void positive(Long longNumber, String format, Object... args) {
        isTrue(longNumber != null && longNumber > 0, format, args);
    }


    public static final void notBlank(String text, ErrorCode errorCode, Object... args) {
        notBlank(text, errorCode.getDescription(), args);
    }

    public static final void notBlank(String text, String format, Object... args) {
        hasLength(text, format, args);
    }

    public static final void hasLength(String text, ErrorCode errorCode, Object... args) {
        hasLength(text, errorCode.getDescription(), args);
    }

    /**
     * 断言不为null & 不为空 & 长度大于0
     *
     * @param text 需进行断言的字符串
     */
    public static final void hasLength(String text) {
        hasLength(text, "[Assert Fail] - 入参不能为null或空字符串");
    }

    /**
     * 断言不为null & 不为空 & 长度大于0
     *
     * @param text   需进行断言的字符串
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static final void hasLength(String text, String format, Object... args) {
        if (text == null || Strings.isEmpty(text)) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }

    public static final void lengthBetween(String text, int start, int end, ErrorCode errorCode, Object... args) {
        lengthBetween(text, start, end, errorCode.getDescription(), args);
    }

    /**
     * 断言不为null & 长度在固定范围
     *
     * @param text   需进行断言的字符串
     * @param start  起始长度
     * @param end    截止长度
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static final void lengthBetween(String text, int start, int end, String format, Object... args) {
        if (text == null
                || text.length() < start
                || text.length() > end) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }

    /**
     * 断言不为null & 长度小于等于固定值
     *
     * @param text   需进行断言的字符串
     * @param limit  最大限制长度
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static final void lengthLessEqual(String text, int limit, String format, Object... args) {
        if (text == null
                || text.length() > limit) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }


    public static final void lengthEqual(String text, int limit, ErrorCode errorCode, Object... args) {
        lengthEqual(text, limit, errorCode.getDescription(), args);
    }

    /**
     * 断言不为null & 长度等于固定值
     *
     * @param text   需进行断言的字符串
     * @param limit  最大限制长度
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static final void lengthEqual(String text, int limit, String format, Object... args) {
        if (text == null
                || text.length() != limit) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }

    public static final void hasText(String text, ErrorCode errorCode, Object... args) {
        hasText(text, errorCode.getDescription(), args);
    }

    /**
     * 判断入参是否是否为空白字符串,若字符串为null或为空白字符串则抛出异常
     *
     * @param text 需进行断言的字符串
     */
    public static final void hasText(String text) {
        hasText(text, "[Assert Fail] - 入参不能为null或空白字符串");
    }

    /**
     * 判断入参是否是否为空白字符串,若字符串为null或为空白字符串则抛出异常
     *
     * @param text   需进行断言的字符串
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static final void hasText(String text, String format, Object... args) {
        if (text == null || Strings.isBlank(text)) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }

    /**
     * 判断入参是否是否为空集合,若为null或为空则抛出异常
     *
     * @param collection 需进行断言的集合
     * @param format     异常信息,支持slf4j式占位符替换
     * @param args       占位符替换内容
     */
    public static final void notEmpty(Collection collection, String format, Object... args) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new RdosDefineException(Strings.format(format, args));
        }
    }
}
