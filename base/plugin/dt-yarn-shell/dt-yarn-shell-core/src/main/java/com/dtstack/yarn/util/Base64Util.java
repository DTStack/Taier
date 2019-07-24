package com.dtstack.yarn.util;

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
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * base 解码
     *
     * @param encode
     * @return
     * @author toutian
     */
    public static String baseDecode(String encode) {
        return new String(Base64.getDecoder().decode(encode), StandardCharsets.UTF_8);
    }
}
