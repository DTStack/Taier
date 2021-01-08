package com.dtstack.lang.base;


import java.util.Objects;


/**
 * @Author 猫爸(maoba@dtstack.com)
 * @Date 2017-05-04 10:20 AM
 * @Motto 一生伏首拜阳明
 */
public final class Strings {
    private static final String DELIM_STR = "{}";
    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 判断字符串是否为空串
     *
     * @param string 需进行判断的字符串
     * @return 若字符串为null或长度为0, 则返回true, 否则返回false
     */
    public final static boolean isEmpty(final String string) {
        return com.dtstack.lang.Langs.isNull(string) || string.length() == 0;
    }

    /**
     * 判断字符串不为空串
     *
     * @param string 需进行判断的字符串
     * @return 若字符串不为空串(字符串长度>0), 则返回true, 否则返回false
     */
    public final static boolean isNotEmpty(final String string) {
        return !isEmpty(string);
    }

    /**
     * 判断字符串是否为空串
     *
     * @param string 需进行判断的字符串
     * @return 若字符串为空串(null,"","   "), 则返回true, 否则返回false
     */
    public final static boolean isBlank(final String string) {
        return !isNotBlank(string);
    }

    /**
     * 判断字符串是否为空串
     *
     * @param string 需进行判断的字符串
     * @return 若字符串不为空串, 则返回true, 否则返回false
     */
    public final static boolean isNotBlank(final String string) {
        return com.dtstack.lang.Langs.isNotNull(string) && string.trim().length() > 0;
    }

    public static final String repeat(char ch, int repeat) {
        Args.notNull(ch, "repeat char must not be null");
        if (repeat > 0) {
            char[] buf = new char[repeat];
            for (int k = 0; k < repeat; k++) {
                buf[k] = ch;
            }
            return new String(buf);
        } else {
            return EMPTY;
        }
    }

    public final static String center(String string, char ch, int length) {
        if (string.length() < length) {
            int pad = (length - string.length()) / 2;
            return new StringBuffer()
                    .append(repeat(ch, pad))
                    .append(string)
                    .append(repeat(ch, pad))
                    .toString();
        } else {
            return string;
        }
    }

    public final static String leftPad(String string, int size) {
        Objects.requireNonNull(string);
        if (string.length() < size) {
            return repeat(' ', size - string.length()) + string;
        } else {
            return string;
        }
    }

    public final static String rightPad(String string, int size) {
        Objects.requireNonNull(string);
        if (string.length() < size) {
            return string + repeat(' ', size - string.length());
        } else {
            return string;
        }
    }

    /**
     * plainText是否包含sk
     *
     * @param plainText
     * @param sk
     * @return
     */
    public final static boolean contains(String plainText, String sk) {
        if (Objects.isNull(plainText) || Objects.isNull(sk)) {
            return false;
        }
        return plainText.toUpperCase().contains(sk.toUpperCase());
    }

    public final static String format(String format, Object... objects) {
        Objects.requireNonNull(format);

        StringBuilder sbuf = new StringBuilder(format.length() + 60);
        if (Objects.nonNull(objects)) {
            int i = 0, k = 0;
            for (Object object : objects) {
                k = format.indexOf(DELIM_STR, i);
                if (k == -1) {
                } else {
                    sbuf.append(format.substring(i, k))
                            .append(com.dtstack.lang.Langs.toString(object));
                    i = k + 2;
                }
            }
            if (format.length() > i) {
                sbuf.append(format.substring(i));
            }
        }
        return sbuf.toString();
    }
}
