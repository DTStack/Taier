package com.dtstack.taier.common.util;

import java.lang.reflect.Array;
import java.util.Objects;

public final class Strings {
    /**
     * 空字符串
     */
    public static final String EMPTY_STRING = "";
    /**
     * null字符串
     */
    public static final char BLANK_CHAR = ' ';
    public static final String EMPTY = "";
    private static final String REFERENCE = "{}";

    private Strings() {
    }

    /**
     * 判断字符串值是否为null
     *
     * @param string 需进行判断的字符串
     * @return 若字符串值为null, 则返回true, 否则返回false
     */
    public final static boolean isNull(String string) {
        return Objects.isNull(string);
    }

    /**
     * 判断字符串是否为null
     *
     * @param string 需进行判断的字符串
     * @return 若字符串部位null, 则返回true, 否则返回false
     */
    public final static boolean isNotNull(String string) {
        return !isNull(string);
    }

    /**
     * 判断字符串是否为空串(字符串非null,并且长度为0)
     *
     * @param string 需进行判断的字符串
     * @return 若字符串长度为0, 则返回true, 否则返回false
     */
    public final static boolean isEmpty(final String string) {
        return isNotNull(string) && string.length() == 0;
    }

    /**
     * 配租单字符串是否为null或空字符串
     *
     * @param string 需进行判断的字符串
     * @return 如字符串为null或者长度为0, 则返回true, 否则返回false
     */
    public final static boolean isNullOrEmpty(final String string) {
        return isNull(string) || isEmpty(string);
    }

    /**
     * 判断字符串不为空串(字符串非null,并且长度大于0)
     *
     * @param string 需进行判断的字符串
     * @return 若字符串不为空串(字符串长度小于0), 则返回true, 否则返回false
     */
    public final static boolean isNotEmpty(final String string) {
        return isNotNull(string) && string.length() > 0;
    }

    /**
     * 判断字符串是否为空白字符串(字符串不为null,并且为空字符串或者组成字符全部为空格符)
     *
     * @param string 需进行判断的字符串
     * @return 若字符串为空串(" ", " "), 则返回true, 否则返回false
     */
    public final static boolean isBlank(final String string) {
        return isNotNull(string) && string.trim().length() == 0;
    }

    /**
     * 判断字符串是否为空白字符串
     *
     * @param string 需进行判断的字符串
     * @return 若字符串不为空串, 则返回true, 否则返回false
     */
    public final static boolean isNotBlank(final String string) {
        return isNotNull(string) && string.trim().length() > 0;
    }

    /**
     * 字符重复
     *
     * @param ch     字符
     * @param repeat 重复次数
     * @return 返回repeat个ch组成的字符串
     */
    public static final String repeat(char ch, int repeat) {
        Objects.requireNonNull(ch);

        if (repeat > 0) {
            char[] buf = new char[repeat];
            for (int k = 0; k < repeat; k++) {
                buf[k] = ch;
            }
            return new String(buf);
        } else {
            return EMPTY_STRING;
        }
    }


    /**
     * 判断在字符串中是否包含中文字符
     *
     * @param text 需进行判断的字符串
     * @return 如字符串不含有中文字符(不包含中文标点), 则返回aflse
     */
    public final static boolean hasChinese(String text) {
        if (Objects.isNull(text)) {
            return false;
        }
        for (int k = 0; k < text.length(); k++) {
            if (isChinese(text.charAt(k))) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isChinese(char c) {
        return c >= 19968 && c <= '龥';
    }


    /**
     * 后去第一个非空字符串的值
     *
     * @param values 给定的值列表
     * @return
     */
    public static String value(String... values) {
        if (values != null && values.length > 0) {
            for (String value : values) {
                if (isNotBlank(value)) {
                    return value;
                }
            }
        }
        return null;
    }


    /***
     * 根据指定分隔符分割字符串---忽略在引号 和 括号 里面的分隔符
     * @param str
     * @param delimter
     * @return
     */
    public static String[] splitIgnoreQuotaBrackets(String str, String delimter) {
        String splitPatternStr = delimter + "(?![^()]*+\\))(?![^{}]*+})(?![^\\[\\]]*+\\])(?=(?:[^\"]|\"[^\"]*\")*$)";
        return str.split(splitPatternStr);
    }

    public static String format(String format, Object... objects) {
        Objects.requireNonNull(format);

        StringBuilder sbuf = new StringBuilder(format.length() + 60);
        if (Objects.nonNull(objects)) {
            int i = 0, k = 0;
            for (Object object : objects) {
                k = format.indexOf(REFERENCE, i);
                if (k == -1) {
                } else {
                    sbuf.append(format, i, k)
                            .append(toString(object));
                    i = k + 2;
                }
            }
            if (format.length() > i) {
                sbuf.append(format.substring(i));
            }
        }
        return sbuf.toString();
    }

    public static String toString(Object object) {
        if (null == object) {
            return "";
        } else if (object.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            int len = Array.getLength(object);
            for (int k = 0; k < len; k++) {
                sb.append(toString(Array.get(object, k))).append(',');
            }
            return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
        } else {
            return object.toString();
        }
    }
}
