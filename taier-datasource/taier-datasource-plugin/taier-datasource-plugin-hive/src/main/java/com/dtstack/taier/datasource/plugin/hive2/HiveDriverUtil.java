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

package com.dtstack.taier.datasource.plugin.hive2;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hive.jdbc.HiveDriver;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.util.Properties;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 14:03 2020/12/23
 * @Description：Hive 工具类
 */
@Slf4j
public class HiveDriverUtil {
    private static final HiveDriver HIVE_DRIVER = new HiveDriver();

    /**
     * HIVE_JDBC 前缀
     */
    private static final String JDBC_PREFIX = "jdbc:hive2://";

    /**
     * HIVE_JDBC 前缀长度
     */
    private static final Integer JDBC_PREFIX_LENGTH = JDBC_PREFIX.length();

    /**
     * 解析 URL 配置信息
     *
     * @param url
     * @param properties
     * @return
     */
    private static DriverPropertyInfo[] parseProperty(String url, Properties properties) {
        try {
            return HIVE_DRIVER.getPropertyInfo(url, properties);
        } catch (Exception e) {
            throw new SourceException(String.format("Hive parse URL exception : %s", e.getMessage()), e);
        }
    }

    /**
     * 获取 Schema 信息
     *
     * @param url
     * @return
     */
    private static String getSchema(String url) {
        return parseProperty(url, null)[2].value;
    }

    /**
     * 设置 Schema 信息
     *
     * @param conn
     * @param url
     * @return
     */
    public static Connection setSchema(Connection conn, String url, String schema) {
        schema = StringUtils.isBlank(schema) ? getSchema(url) : schema;
        if (StringUtils.isBlank(schema)) {
            return conn;
        }

        try {
            conn.setSchema(schema);
            return conn;
        } catch (Exception e) {
            throw new SourceException(String.format("Setting schema exception : %s", e.getMessage()), e);
        }
    }

    /**
     * 去除 Schema 信息
     *
     * @param url
     * @return
     */
    public static String removeSchema(String url) {
        String schema = getSchema(url);
        return removeSchema(url, schema);
    }

    /**
     * 去除 Schema 信息
     *
     * @param url
     * @param schema
     * @return
     */
    private static String removeSchema(String url, String schema) {
        if (StringUtils.isBlank(schema) || !url.toLowerCase().contains(JDBC_PREFIX)) {
            return url;
        }

        String urlWithoutPrefix = url.substring(JDBC_PREFIX_LENGTH);
        return JDBC_PREFIX + urlWithoutPrefix.replaceFirst("/" + schema, "/");
    }

    /**
     * Unicode 编码转字符串
     *
     * @param string 支持 Unicode 编码和普通字符混合的字符串
     * @return 解码后的字符串
     */
    public static String unicodeToStr(String string) {
        String prefix = "\\u";
        if (string == null || !string.contains(prefix)) {
            // 传入字符串为空或不包含 Unicode 编码返回原内容
            return string;
        }

        StringBuilder value = new StringBuilder(string.length() >> 2);
        String[] strings = string.split("\\\\u");
        String hex, mix;
        char hexChar;
        int ascii, n;

        if (strings[0].length() > 0) {
            // 处理开头的普通字符串
            value.append(strings[0]);
        }

        try {
            for (int i = 1; i < strings.length; i++) {
                hex = strings[i];
                if (hex.length() > 3) {
                    mix = "";
                    if (hex.length() > 4) {
                        // 处理 Unicode 编码符号后面的普通字符串
                        mix = hex.substring(4);
                    }
                    hex = hex.substring(0, 4);

                    try {
                        Integer.parseInt(hex, 16);
                    } catch (Exception e) {
                        // 不能将当前 16 进制字符串正常转换为 10 进制数字，拼接原内容后跳出
                        value.append(prefix).append(strings[i]);
                        continue;
                    }

                    ascii = 0;
                    for (int j = 0; j < hex.length(); j++) {
                        hexChar = hex.charAt(j);
                        // 将 Unicode 编码中的 16 进制数字逐个转为 10 进制
                        n = Integer.parseInt(String.valueOf(hexChar), 16);
                        // 转换为 ASCII 码
                        ascii += n * ((int) Math.pow(16, (hex.length() - j - 1)));
                    }

                    // 拼接解码内容
                    value.append((char) ascii).append(mix);
                } else {
                    // 不转换特殊长度的 Unicode 编码
                    value.append(prefix).append(hex);
                }
            }
        } catch (Exception e) {
            // Unicode 编码格式有误，解码失败
            return null;
        }

        return value.toString();
    }
}
