package com.dtstack.engine.dtscript.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public class Base64Util {

    /**
     * base 加密
     *
     * @param text
     * @return
     * @author toutian
     */
    public static String baseEncode(String text) {
        return new String(Base64.getEncoder().encode(text.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * base 解码
     *
     * @param encode
     * @return
     * @author toutian
     */
    public static String baseDecode(String encode) {
        return new String(Base64.getDecoder().decode(encode.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }


    public static byte[] baseEncode(byte[] encode) {
        return Base64.getEncoder().encode(encode);
    }

    public static byte[] baseDecode(byte[] encode) {
        return Base64.getDecoder().decode(encode);
    }
}
