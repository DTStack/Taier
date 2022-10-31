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

package com.dtstack.taier.datasource.plugin.kerberos.core.util;

import lombok.Data;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:05 2020/10/29
 * @Description：UGI 缓存信息
 */
@Data
public class UGICacheData {
    /**
     * 过期时间戳 只有头结点才存在
     */
    private Long timeoutStamp;

    /**
     * UGI 信息
     */
    private UserGroupInformation ugi;

    public UGICacheData(UserGroupInformation ugi) {
        this.ugi = ugi;
        timeoutStamp = System.currentTimeMillis() + 10 * 1000;
    }
}
