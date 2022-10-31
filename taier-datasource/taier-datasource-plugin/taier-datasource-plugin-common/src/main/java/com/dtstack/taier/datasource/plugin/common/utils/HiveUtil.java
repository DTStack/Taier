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

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 分隔符工具类
 *
 * @author ：wangchuan
 * date：Created in 下午3:05 2022/4/29
 * company: www.dtstack.com
 */
public class HiveUtil {

    /**
     * hive 默认分隔符
     */
    private static final char DEFAULT_DELIMITER = '\001';

    /**
     * 获取表分隔符, 忽略转义字符、多字符分隔符场景
     *
     * @param delimiterStr 分隔符字符串
     * @return char 类型分隔符
     */
    public static char getDelimiter(String delimiterStr) {
        char delimiter;
        if (StringUtils.isEmpty(delimiterStr)) {
            delimiter = DEFAULT_DELIMITER;
        } else if (delimiterStr.startsWith("\\")) {
            int index = delimiterStr.lastIndexOf("\\");
            String ignoreEscape = delimiterStr.substring(index);
            if (StringUtils.isBlank(ignoreEscape)) {
                delimiter = ignoreEscape.charAt(0);
            } else {
                delimiter = DEFAULT_DELIMITER;
            }
        } else {
            delimiter = delimiterStr.charAt(0);
        }
        return delimiter;
    }

    /**
     * 按照指定分隔符切分行, 结果集去除双引号
     *
     * @param line         行字符串
     * @param delimiterStr 分隔符
     * @return 切分后的数据
     */
    public static String[] splitByDelimiterStr(String line, String delimiterStr) {
        if (StringUtils.isBlank(line)) {
            return new String[]{};
        }
        char delimiter = getDelimiter(delimiterStr);
        Splitter splitter = new Splitter(delimiter);
        List<String> sqlArray = splitter.splitEscaped(line);
        return sqlArray.stream().map(str -> {
            if (StringUtils.isNotEmpty(str) && str.length() > 1 && str.startsWith("\"") && str.endsWith("\"")) {
                return str.substring(1, str.lastIndexOf("\"")).replace("\\", "");
            }
            return str;
        }).toArray(String[]::new);
    }
}
