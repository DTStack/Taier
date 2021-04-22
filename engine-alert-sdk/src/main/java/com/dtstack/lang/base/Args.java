package com.dtstack.lang.base;

/**
 * 参数校验工具类(若校验不通过则抛出IllegalArgumentException异常)
 *
 * @Author 猫爸(maoba@dtstack.com)
 * @Date 2017-05-04 1:55 PM
 * @Motto 一生伏首拜阳明
 */
public final class Args {

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assert Fail] - this expression must be true.");
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isFalse(boolean expression) {
        isFalse(expression, "[Assert Fail] - this expression must be false.");
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言对象不为空
     *
     * @param object 需断言的对象
     */
    public static void notNull(Object object) {
        notNull(object, "[Assert Fail] - this object must not be null.");
    }

    /**
     * 断言对象不为空
     *
     * @param object 需断言的对象
     * @param message 对象为空时的异常说明文字
     */
    public static void notNull(Object object, String message) {
        if (com.dtstack.lang.Langs.isNull(object)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 字符串不能为空串
     */
    public static void notBlank(String text) {
        notBlank(text, "[Assert Fail] - this string text must not be blank.");
    }

    /**
     * 字符串不能为空串
     *
     * @param text 异常说明文字
     */
    public static void notBlank(String text, String message) {
        if (Strings.isBlank(text)) {
            throw new IllegalArgumentException(message);
        }
    }


}
