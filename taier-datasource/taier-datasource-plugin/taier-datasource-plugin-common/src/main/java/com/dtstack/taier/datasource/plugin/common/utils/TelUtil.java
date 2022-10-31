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

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 22:47 2020/2/26
 * @Description：Tel 工具类
 */
@Slf4j
public class TelUtil {

    // ip:端口 正则解析，适配ipv6
    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("(?<host>(.*)):(?<port>\\d+)*");

    public static boolean checkTelnetAddr(String urls) {
        String[] addrs = urls.split(",");
        for (String addr : addrs) {
            Matcher matcher = HOST_PORT_PATTERN.matcher(addr);
            if (!matcher.find()) {
                throw new SourceException(String.format("address：%s wrong format", addr));
            }
            String host = matcher.group("host");
            String portStr = matcher.group("port");
            if (StringUtils.isBlank(host) || StringUtils.isBlank(portStr)) {
                throw new SourceException(String.format("address：%s missing ip or port", addr));
            }
            //集群内任一地址能telnet通则返回成功
            boolean connected = AddressUtil.telnet(host.trim(), Integer.parseInt(portStr.trim()));
            if (connected) {
                return true;
            }
        }
        throw new SourceException(String.format("all addresses ：%s can't connect", urls));
    }
}
