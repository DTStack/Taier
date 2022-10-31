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

package com.dtstack.taier.datasource.plugin.restful.core;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.utils.AddressUtil;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RestfulSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;

import java.net.URI;

/**
 * restful 客户端
 *
 * @author ：wangchuan
 * date：Created in 上午10:37 2021/8/11
 * company: www.dtstack.com
 */
public class RestfulClient extends AbsNoSqlClient {

    private static final int UNDEFINED_PORT = -1;

    private static final int DEFAULT_PORT = 80;

    @Override
    public Boolean testCon(ISourceDTO source) {
        RestfulSourceDTO restfulSourceDTO = (RestfulSourceDTO) source;
        URI uri = URI.create(restfulSourceDTO.getUrl());
        // 默认端口 80
        int port = uri.getPort() != UNDEFINED_PORT ? uri.getPort() : DEFAULT_PORT;
        boolean telnetCheck = AddressUtil.telnet(uri.getHost(), port);
        if (!telnetCheck) {
            throw new SourceException(String.format("failed to telnet host: %s and port: %s", uri.getHost(), port));
        }
        return true;
    }
}
