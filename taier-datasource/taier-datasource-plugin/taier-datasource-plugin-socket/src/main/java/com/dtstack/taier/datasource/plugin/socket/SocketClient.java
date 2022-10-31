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

package com.dtstack.taier.datasource.plugin.socket;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SocketSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * socket数据源客户端
 *
 * @author ：wangchuan
 * date：Created in 4:16 下午 2020/12/28
 * company: www.dtstack.com
 */
@Slf4j
public class SocketClient extends AbsNoSqlClient {

    // ip:port正则
    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("(?<host>(.*)):((?<port>\\d+))*");

    @Override
    public Boolean testCon(ISourceDTO source) {
        SocketSourceDTO socketSourceDTO = (SocketSourceDTO) source;
        String hostPort = socketSourceDTO.getHostPort();
        if (StringUtils.isBlank(hostPort)) {
            throw new SourceException("socket datasource ip and port not empty");
        }
        Matcher matcher = HOST_PORT_PATTERN.matcher(hostPort);
        if (matcher.find()) {
            String host = matcher.group("host");
            String portStr = matcher.group("port");
            if (StringUtils.isBlank(portStr)) {
                throw new SourceException("socket datasource port is not empty");
            }
            // 转化为int格式的端口
            int port = Integer.parseInt(portStr);
            InetAddress address = null;
            try {
                // 方法内支持ipv6
                address = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                throw new SourceException(String.format("socket connection exception：UnknownHostException：%s", e.getMessage()), e);
            }
            try(Socket socket = new Socket(address, port)) {
                // 往输出流发送一个字节的数据，Socket的SO_OOBINLINE属性没有打开，就会自动舍弃这个字节，该属性默认关闭
                socket.sendUrgentData(0xFF);
            } catch (IOException e) {
                throw new SourceException(String.format("socket connection exception：%s", e.getMessage()), e);
            }
        }
        return true;
    }
}
