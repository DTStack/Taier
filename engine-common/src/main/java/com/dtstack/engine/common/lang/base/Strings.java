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

package com.dtstack.engine.common.lang.base;

import java.util.Objects;

public abstract class Strings {
    /**
     * 空字符串
     */
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_PLACEHOLDER = "-";
    /**
     * null字符串
     */
    public static final String NULL_STRING = null;
    public static final char BLANK_CHAR = ' ';
    public static final String DELIM_COMMA = ",";
    private static final String DELIM_STR = "{}";

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

    public final static String center(String string, int length) {
        return center(string, BLANK_CHAR, length);
    }

    /**
     * 字符串居中
     *
     * @param string 需要居中显示的字符串
     * @param ch     除字符串之外的其余字符占位符
     * @param length 总长度
     * @return 居中的字符串
     */
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
                            .append(String.valueOf(object));
                    i = k + 2;
                }
            }
            if (format.length() > i) {
                sbuf.append(format.substring(i));
            }
        }
        return sbuf.toString();
    }

//    /**
//     * 判断在字符串中是否包含中文字符
//     *
//     * @param text 需进行判断的字符串
//     * @return 如字符串不含有中文字符(不包含中文标点), 则返回aflse
//     */
//    public final static boolean hasChinese(String text) {
//        if (Objects.isNull(text)) {
//            return false;
//        }
//        for (int k = 0; k < text.length(); k++) {
//            if (Chars.isChinese(text.charAt(k))) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 若字符串为null或为空字符串,则返回默认值
     *
     * @param text        需处理的字符串
     * @param defaultText 默认值
     * @return 字符串
     */
    public static final String def(String text, String defaultText) {
        if (isNullOrEmpty(text)) {
            return defaultText;
        } else {
            return text;
        }
    }

    /**
     * 若字符串为null则返回空字符串
     *
     * @param text 待处理的字符串
     * @return 原字符串或空字符串
     */
    public static final String nullToEmpty(String text) {
        return Objects.isNull(text) ? EMPTY_STRING : text;
    }

    /**
     * 是否是字符串类型
     *
     * @param object 需进行判断的
     * @return 若非null并且为字符串则返回true
     */
    public static boolean isCharSequence(Object object) {
        return Objects.nonNull(object) && CharSequence.class.isAssignableFrom(object.getClass());
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
}
