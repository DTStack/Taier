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

package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.dto.SSLConfig;
import com.dtstack.taier.datasource.api.pool.PoolConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.sql.Connection;
import java.util.Map;

/**
 * 抽象类 sourceDTO
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractSourceDTO implements ISourceDTO {

    /**
     * 连接池配置信息，如果传入则认为开启连接池
     */
    private PoolConfig poolConfig;

    /**
     * sftp 配置, 如果不传该配置, 默认路径为本地
     */
    private Map<String, String> sftpConf;

    /**
     * kerberos 配置
     */
    private Map<String, Object> kerberosConfig;

    /**
     * 统一 ssl 认证文件路径
     */
    private SSLConfig sslConfig;

    /**
     * jdbc connection
     */
    private Connection connection;
}
