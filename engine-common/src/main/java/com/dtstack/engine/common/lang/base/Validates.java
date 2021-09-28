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
import java.util.regex.Pattern;

public class Validates {
    /**
     * 邮箱地址正则表达式
     */
    private static Pattern p_mail = Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5\\.]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    /**
     * 数字或者英文字母表达式
     */
    private static Pattern p_numberAlpha = Pattern.compile("^[a-zA-Z0-9]+$");
    /**
     * IPV4正则表达式
     */
    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");


    private Validates() {
    }

    /**
     * 判断对象是否null
     *
     * @param object 需进行判断的对象
     * @return 若对象为null, 则返回true, 否则返回false
     */
    public final static boolean isNull(Object object) {
        return Objects.isNull(object);
    }

    /**
     * 判断对象非null
     *
     * @param object 需进行判断的对象
     * @return 若对象为非null, 则返回true, 否则返回false
     */
    public final static boolean nonNull(Object object) {
        return Objects.nonNull(object);
    }

    /**
     * 判断是否是IPv4地址
     *
     * @param ip 需进行判断的ip
     * @return 若为IPv4地址, 则返回true, 否则返回false
     */

    public final static boolean isIPv4(String ip) {
        return ip != null && IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 判断int型数据为非负数
     *
     * @param number 整型
     * @return 若int型数据为Null或者负数则返回false, 否则返回true
     */
    public final static boolean nonNegative(Integer number) {
        return Objects.nonNull(number)
                && number.compareTo(Integer.valueOf(0)) >= 0;
    }

    /**
     * 判断long型数据为非负数
     *
     * @param number 长整型
     * @return 若int型数据为Null或者负数则返回false, 否则返回true
     */
    public final static boolean nonNegative(Long number) {
        return Objects.nonNull(number)
                && number.compareTo(Long.valueOf(0L)) >= 0;
    }

    /**
     * 判断字符串是否符合邮件地址规则
     *
     * @param text 欲判断的文本
     * @return 若为合法的邮件地址, 则返回true, 否则返回false
     */
    public final static boolean mail(String text) {
        return Strings.isNotBlank(text) && p_mail.matcher(text).matches();
    }

    /**
     * 字符串是否只包含数字和应为字母(为null时返回false)
     *
     * @param text 预进行判断的字符串
     * @return 若字符串仅仅包含数字或英文字母, 则返回true
     */
    public final static boolean numberOrAlpha(String text) {
        return Strings.isNotNull(text) && p_numberAlpha.matcher(text).matches();
    }
}