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

package com.dtstack.taier.datasource.plugin.spark;

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
 * @Date ：Created in 16:06 2020/12/23
 * @Description：SparkThrift 工具类
 */
@Slf4j
public class SparkThriftDriverUtil {
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
            throw new SourceException(String.format("Spark parse URL exception : %s", e.getMessage()), e);
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
        } catch (Exception e) {
            throw new SourceException(String.format("Setting schema exception : %s", e.getMessage()), e);
        }
        return conn;
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
}
