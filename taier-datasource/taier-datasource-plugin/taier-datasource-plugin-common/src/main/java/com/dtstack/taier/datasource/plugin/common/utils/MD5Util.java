package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.exception.SourceException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午8:08 2022/2/21
 * company: www.dtstack.com
 */
public class MD5Util {

    /**
     * 获得字符串的md5值
     *
     * @param str 待加密的字符串
     * @return md5加密后的字符串
     */
    public static String getMd5String(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return HexUtil.bytes2Hex(md5.digest(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new SourceException("get md5 string error", e);
        }
    }
}