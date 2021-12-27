//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dtstack.batch.utils;

import java.util.Objects;

public abstract class Strings {
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_PLACEHOLDER = "-";
    public static final String NULL_STRING = null;
    public static final char BLANK_CHAR = ' ';
    public static final String DELIM_COMMA = ",";
    private static final String DELIM_STR = "{}";

    private Strings() {
    }

    public static final boolean isNull(String string) {
        return Objects.isNull(string);
    }

    public static final boolean isNotNull(String string) {
        return !isNull(string);
    }

    public static final boolean isEmpty(String string) {
        return isNotNull(string) && string.length() == 0;
    }

    public static final boolean isNullOrEmpty(String string) {
        return isNull(string) || isEmpty(string);
    }

    public static final boolean isNotEmpty(String string) {
        return isNotNull(string) && string.length() > 0;
    }

    public static final boolean isBlank(String string) {
        return isNotNull(string) && string.trim().length() == 0;
    }

    public static final boolean isNotBlank(String string) {
        return isNotNull(string) && string.trim().length() > 0;
    }

    public static final String repeat(char ch, int repeat) {
        Objects.requireNonNull(ch);
        if (repeat <= 0) {
            return "";
        } else {
            char[] buf = new char[repeat];

            for(int k = 0; k < repeat; ++k) {
                buf[k] = ch;
            }

            return new String(buf);
        }
    }

    public static final String center(String string, int length) {
        return center(string, ' ', length);
    }

    public static final String center(String string, char ch, int length) {
        if (string.length() < length) {
            int pad = (length - string.length()) / 2;
            return repeat(ch, pad) + string + repeat(ch, pad);
        } else {
            return string;
        }
    }

    public static final String leftPad(String string, int size) {
        Objects.requireNonNull(string);
        return string.length() < size ? repeat(' ', size - string.length()) + string : string;
    }

    public static final String rightPad(String string, int size) {
        Objects.requireNonNull(string);
        return string.length() < size ? string + repeat(' ', size - string.length()) : string;
    }

    public static final String format(String format, Object... objects) {
        Objects.requireNonNull(format);
        StringBuilder sbuf = new StringBuilder(format.length() + 60);
        if (Objects.nonNull(objects)) {
            int i = 0;
            int k = -1;
            Object[] var5 = objects;
            int var6 = objects.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Object object = var5[var7];
                k = format.indexOf("{}", i);
                if (k != -1) {
                    sbuf.append(format.substring(i, k)).append(String.valueOf(object));
                    i = k + 2;
                }
            }

            if (format.length() > i) {
                sbuf.append(format.substring(i));
            }
        }

        return sbuf.toString();
    }

    public static final boolean hasChinese(String text) {
        if (Objects.isNull(text)) {
            return false;
        } else {
            for(int k = 0; k < text.length(); ++k) {
                if (Chars.isChinese(text.charAt(k))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static final String def(String text, String defaultText) {
        return isNullOrEmpty(text) ? defaultText : text;
    }

    public static final String nullToEmpty(String text) {
        return Objects.isNull(text) ? "" : text;
    }

    public static boolean isCharSequence(Object object) {
        return Objects.nonNull(object) && CharSequence.class.isAssignableFrom(object.getClass());
    }

    public static String value(String... values) {
        if (values != null && values.length > 0) {
            String[] var1 = values;
            int var2 = values.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String value = var1[var3];
                if (isNotBlank(value)) {
                    return value;
                }
            }
        }

        return null;
    }
}
