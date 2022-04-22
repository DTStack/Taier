package com.dtstack.taier.develop.utils;

import com.dtstack.taier.common.exception.DtCenterDefException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-12-25 11:50
 * @Description:
 */
public class EncoderUtil {

    public static String encoderURL(String s, String enc) {
        try {
            return URLEncoder.encode(s, enc);
        } catch (UnsupportedEncodingException e) {
            throw new DtCenterDefException(String.format("字符串解析异常:%s", e.getMessage()), e);
        }
    }
}
