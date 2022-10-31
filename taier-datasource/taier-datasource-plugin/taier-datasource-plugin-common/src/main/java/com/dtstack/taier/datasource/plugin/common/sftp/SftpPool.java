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

package com.dtstack.taier.datasource.plugin.common.sftp;

import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * company: www.dtstack.com
 * author: yuemo
 * create: 2020-01-19
 */
@Slf4j
public class SftpPool {

    private GenericObjectPool<ChannelSftp> pool;

    public SftpPool(SftpFactory factory) {
        this.pool = new GenericObjectPool<>(factory);
    }

    public SftpPool(SftpFactory factory, SftpPoolConfig sftpPoolConfig) {
        this.pool = new GenericObjectPool<>(factory, sftpPoolConfig);
    }

    public SftpPool(SftpFactory factory, int maxTotal, int maxIdle, int minIdle) {
        this.pool = new GenericObjectPool<>(factory, new SftpPoolConfig(maxTotal, maxIdle, minIdle));
    }

    /**
     * 获取一个sftp连接对象
     */
    public ChannelSftp borrowObject() {
        try {
            ChannelSftp channelSftp = pool.borrowObject();
            log.info("get Sfpt channelSftp : " + channelSftp);
            return channelSftp;
        } catch (Exception e) {
            throw new RuntimeException("从Sfpt连接池中获取连接失败", e);
        }
    }

    /**
     * 归还一个sftp连接对象
     */
    public void returnObject(ChannelSftp channelSftp) {
        if (channelSftp != null) {
            pool.returnObject(channelSftp);
            log.info("return channelSftp  : " + channelSftp);
        }
    }

}