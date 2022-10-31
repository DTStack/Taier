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

import java.io.File;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:24 2020/8/28
 * @Description：文件路径处理
 */
public class PathUtils {

    /**
     * 默认 kerberos 下载目录名称
     */
    private static final String KERBEROS_PATH_NAME = "kerberosConf";

    /**
     * 默认 confDir 下载目录名称
     */
    private static final String CONF_DIR = "confDir";

    /**
     * 默认 es ssl 下载目录名称
     */
    private static final String ES_SSL_CONF_DIR = "keyPath";

    /**
     * ssl 下载目录名称
     */
    private static final String SSL_CONF_DIR = "ssl";

    /**
     * 处理路径中存在多个分隔符的情况
     *
     * @param path
     * @return
     */
    public static String removeMultiSeparatorChar(String path) {
        return path.replaceAll("//*", "/");
    }

    /**
     * 获取 kerberos 文件存储路径
     *
     * @return kerberos 文件存储目录
     */
    public static String getKerberosConfDir() {
        return formatDir(KERBEROS_PATH_NAME);
    }

    /**
     * 获取 es ssl 认证目录
     *
     * @return ssl 认证目录
     */
    public static String getEsSSLConfDir() {
        return formatDir(ES_SSL_CONF_DIR);
    }

    /**
     * 获取 ssl 认证目录
     *
     * @return ssl 认证目录
     */
    public static String getSSLConfDir() {
        return formatDir(SSL_CONF_DIR);
    }

    /**
     * 获取 conf 文件存储路径
     *
     * @return conf 文件存储路径
     */
    public static String getConfDir() {
        return formatDir(CONF_DIR);
    }

    private static String formatDir(String confDir) {
        return System.getProperty("user.dir") + File.separator + confDir + File.separator;
    }
}
