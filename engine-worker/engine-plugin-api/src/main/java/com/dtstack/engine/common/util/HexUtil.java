package com.dtstack.engine.common.util;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-07-31
 * Time: 11:36
 */
public class HexUtil {
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * 16进制转byte数组
     * @param data 16进制字符串
     * @return byte数组
     * @throws Exception 转化失败的异常
     */
    public static byte[] hex2Bytes(final String data) throws Exception {
        final int len = data.length();

        if ((len & 0x01) != 0) {
            throw new Exception("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data.charAt(j), j) << 4;
            j++;
            f = f | toDigit(data.charAt(j), j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    /**
     * bytes数组转16进制String
     * @param data bytes数组
     * @return 转化结果
     */
    public static String bytes2Hex(final byte[] data) {
        return bytes2Hex(data, true);
    }

    /**
     * bytes数组转16进制String
     * @param data bytes数组
     * @param toLowerCase 是否小写
     * @return 转化结果
     */
    public static String bytes2Hex(final byte[] data, final boolean toLowerCase) {
        return bytes2Hex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }


    /**
     * bytes数组转16进制String
     * @param data bytes数组
     * @param toDigits DIGITS_LOWER或DIGITS_UPPER
     * @return 转化结果
     */
    private static String bytes2Hex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return new String(out);
    }
    /**
     * 16转化为数字
     * @param ch 16进制
     * @param index 索引
     * @return 转化结果
     * @throws Exception 转化失败异常
     */
    private static int toDigit(final char ch, final int index)
            throws Exception {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }

}