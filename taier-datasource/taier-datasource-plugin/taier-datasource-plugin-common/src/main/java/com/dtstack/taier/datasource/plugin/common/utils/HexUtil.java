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

package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.utils.AssertUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author ：wangchuan
 * date：Created in 下午8:08 2022/2/21
 * company: www.dtstack.com
 */
public class HexUtil {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * bytes数组转16进制String
     *
     * @param data bytes数组
     * @return 转化结果
     */
    public static String bytes2Hex(final byte[] data) {
        return bytes2Hex(data, true);
    }

    /**
     * bytes数组转16进制String
     *
     * @param data        bytes数组
     * @param toLowerCase 是否小写
     * @return 转化结果
     */
    public static String bytes2Hex(final byte[] data, final boolean toLowerCase) {
        return bytes2Hex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * bytes数组转16进制String
     *
     * @param data     bytes数组
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
     * 指定值进行base64编码
     *
     * @param value value
     * @return 编码后值
     */
    public static String base64En(String value) {
        AssertUtils.notBlank(value, "base64 encode value can't be blank");
        return Base64.getEncoder().encodeToString((value).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 指定值进行base64解码
     *
     * @param value value
     * @return 解码后值
     */
    public static String base64De(String value) {
        AssertUtils.notBlank(value, "base64 decode value can't be blank");
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}