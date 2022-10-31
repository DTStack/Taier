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

package com.dtstack.taier.datasource.plugin.ftp;

import org.apache.commons.net.ftp.FTP;

/**
 * ftp/sftp 协议枚举类
 *
 * @author ：wangchuan
 * date：Created in 下午4:43 2021/6/21
 * company: www.dtstack.com
 */
public enum ProtocolEnum {

    /**
     * 文件传输协议
     */
    FTP,

    /**
     * 安全文件传输协议
     */
    SFTP
}
