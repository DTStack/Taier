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

package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * SSL 配置相关
 *
 * @author ：wangchuan
 * date：Created in 上午10:47 2022/2/28
 * company: www.dtstack.com
 */
@Data
@Builder
public class SSLConfig {

    /**
     * ssl 文件上传的时间戳, 用于和 sftp 上 文件比较避免认证文件重复下载
     */
    private Timestamp sslFileTimestamp;

    /**
     * ssl 认证文件夹的 sftp 绝对路径
     */
    private String remoteSSLDir;

    /**
     * ssl-client.xml 文件的文件名称
     */
    private String sslClientConf;

    /**
     * 其他扩展
     */
    private Map<String, Object> otherConfig;
}
