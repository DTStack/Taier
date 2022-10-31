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

package com.dtstack.taier.common.util;

import com.dtstack.taier.pluginapi.util.HexUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-07-31
 * Time: 10:32
 */
public class MD5Util {
    /**
     * 获得字符串的md5值
     *
     * @param str 待加密的字符串
     * @return md5加密后的字符串
     */
    public static String getMD5String(String str) {
        byte[] bytes = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            bytes = md5.digest(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return HexUtil.bytes2Hex(bytes);

    }



    /**
     * 获得字符串的md5大写值
     *
     * @param str 待加密的字符串
     * @return md5加密后的大写字符串
     */
    public static String getMD5UpperString(String str) {
        return getMD5String(str).toUpperCase();
    }

    /**
     * 获得文件的md5值
     *
     * @param file 文件对象
     * @return 文件的md5
     */
    public static String getFileMD5String(File file) {
        String ret = "";
        try(FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();) {

            ByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            ret = HexUtil.bytes2Hex(md5.digest());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 获得文件md5值大写字符串
     *
     * @param file 文件对象
     * @return 文件md5大写字符串
     */
    public static String getFileMD5UpperString(File file) {
        return getFileMD5String(file).toUpperCase();
    }

    /**
     * 校验文件的md5值
     *
     * @param file 目标文件
     * @param md5  基准md5
     * @return 校验结果
     */
    public static boolean checkFileMD5(File file, String md5) {
        return getFileMD5String(file).equalsIgnoreCase(md5);
    }

    /**
     * 校验字符串的md5值
     *
     * @param str 目标字符串
     * @param md5 基准md5
     * @return 校验结果
     */
    public static boolean checkMD5(String str, String md5) {
        return getMD5String(str).equalsIgnoreCase(md5);
    }

    /**
     * 获得加盐md5，算法过程是原字符串md5后连接加盐字符串后再进行md5
     *
     * @param str  待加密的字符串
     * @param salt 盐
     * @return 加盐md5
     */
    public static String getMD5AndSalt(String str, String salt) {
        return getMD5String(getMD5String(str).concat(salt));
    }
      /*private static String bytesToHex(byte[] bytes) {
        // 将MD5输出的二进制结果转换为小写的十六进制
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                result.append("0");
            }
            result.append(hex);
        }
        return result.toString();
    }*/
}