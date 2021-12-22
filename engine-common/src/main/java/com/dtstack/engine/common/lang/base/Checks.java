package com.dtstack.engine.common.lang.base;

import java.util.Collection;

/**
 * SLOGAN:让未来变成现在
 * 更好用的参数校验工具类,支持slf4j式消息占位符替换
 *
 * @author <a href="mailto:maoba@dtstack.com">猫爸</a> 2018-04-03 17:13.
 */
public final class Checks {

    /**
     * 判断传入表达式是否为true,若为false,则抛出参数校验失败异常
     *
     * @param expression 需要进行断言的表达式
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assert Fail] - 表达式计算结果必须为True");
    }

    public static void isFalse(boolean expression) {
        isFalse(expression, "[Assert Fail] - 表达式计算结果必须为False");
    }

    public static void isFalse(boolean expression, String format, Object... args) {
        if (expression) {
            throw new IllegalArgumentException(Strings.format(format, args));
        }
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
            throw new IllegalArgumentException(Strings.format(format, args));
        }
    }

    /**
     * 判断对象是否为null,若非null则抛出参数校验失败异常
     *
     * @param object 需进行判断的对象
     */
    public static void isNull(Object object) {
        isNull(object, "[Assert Fail] - 入参必须为null对象");
    }

    /**
     * 判断对象是否为null,若非null则抛出参数校验失败异常
     *
     * @param object 需进行判断的对象
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static void isNull(Object object, String format, Object... args) {
        if (!Validates.isNull(object)) {
            throw new IllegalArgumentException(Strings.format(format, args));
        }
    }

    /**
     * 判断对象是否为非null,若为null则抛出参数校验失败异常
     *
     * @param object 需进行判断的对象
     */
    public static void nonNull(Object object) {
        nonNull(object, "[Assert Fail] - 入参必须为非null对象");
    }

    /**
     * 判断对象是否为非null,若为null则抛出参数校验失败异常
     *
     * @param object 需进行判断的对象
     * @param format 异常信息,支持slf4j式占位符替换
     * @param args   占位符替换内容
     */
    public static void nonNull(Object object, String format, Object... args) {
        if (!Validates.nonNull(object)) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    public static void hasText(String text, String format, Object... args) {
        if (Strings.isEmpty(text)) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    public static void hasText(String text) {
        hasText(text, "[Assert Fail] - 入参不能为空字符串");
    }

    /**
     * 判断参数是否为非负数,若为负数,则抛出参数校验失败异常
     *
     * @param intNumber 需进行判断的参数
     */
    public static final void nonNegative(Integer intNumber) {
        nonNegative(intNumber, "[Assert Fail] - ===> [{}] 入参不能为负数", intNumber);
    }

    public static final void nonNegative(Long longNumber) {
        nonNegative(longNumber, "[Assert Fail] - ===> [{}] 入参不能为负数", longNumber);
    }

    /**
     * 判断参数是否为非负数,若为负数,则抛出参数校验失败异常
     *
     * @param longNumber 需进行判断的参数
     * @param format     异常信息,支持slf4j式占位符替换
     * @param args       占位符替换内容
     */
    public static final void nonNegative(Long longNumber, String format, Object... args) {
        isTrue(Validates.nonNegative(longNumber), format, args);
    }

    /**
     * 判断参数是否为非负数,若为负数,则抛出参数校验失败异常
     *
     * @param intNumber 需进行判断的参数
     * @param format    异常信息,支持slf4j式占位符替换
     * @param args      占位符替换内容
     */
    public static final void nonNegative(Integer intNumber, String format, Object... args) {
        isTrue(Validates.nonNegative(intNumber), format, args);
    }

    /**
     * 判断集合不能为空,若入参集合为null或入参集合为空,则抛出参数校验失败异常
     *
     * @param collection 需进行判断的集合
     */
    public static final void nonEmpty(Collection collection) {
        nonEmpty(collection, "[Assert Fail] - 集合为空");
    }

    /**
     * 判断集合不能为空,若入参集合为null或入参集合为空,则抛出参数校验失败异常
     *
     * @param collection 需进行判断的集合
     * @param format     异常信息,支持slf4j式占位符替换
     * @param args       占位符替换内容
     */
    public static final void nonEmpty(Collection collection, String format, Object... args) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(Strings.format(format, args));
        }
    }
}
