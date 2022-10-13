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

package com.dtstack.taier.pluginapi.sftp;

import com.dtstack.taier.pluginapi.enums.SftpType;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * company: www.dtstack.com
 * author: yuemo
 * create: 2020-01-19
 */
public class SftpFactory extends BasePooledObjectFactory<ChannelSftp> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SftpFactory.class);

    private static final int DEFAULT_RETRY_TIMES = 3;
    private static final int SLEEP_TIME_MILLI_SECOND = 2000;

    private SftpConfig sftpConfig;

    public SftpFactory(SftpConfig sftpConfig) {
        checkConfig(sftpConfig);

        this.sftpConfig = sftpConfig;
    }

    private static void checkConfig(SftpConfig sftpConfig) {
        if (sftpConfig == null) {
            throw new IllegalArgumentException("The config of sftp is null");
        }
        if (StringUtils.isBlank(sftpConfig.getHost())) {
            throw new IllegalArgumentException("The host of sftp is null");
        }
        if (sftpConfig.getPort() == null) {
            throw new IllegalArgumentException("The port of sftp is null");
        }
        if (StringUtils.isBlank(sftpConfig.getUsername())) {
            throw new IllegalArgumentException("The username of sftp is null");
        }
        if (StringUtils.isBlank(sftpConfig.getPath())) {
            throw new IllegalArgumentException("The path of sftp is null");
        }
    }

    @Override
    public ChannelSftp create() {
        try {
            return create(DEFAULT_RETRY_TIMES);
        } catch (Exception e) {
            LOGGER.error("Create ChannelSftp error : " + e);
            throw new RuntimeException(e);
        }
    }

    public ChannelSftp create(int retryNumber) throws Exception {
        return RetryUtil.executeWithRetry(this::getChannelSftp,
                retryNumber, SLEEP_TIME_MILLI_SECOND, false);
    }

    private ChannelSftp getChannelSftp() throws JSchException {

        JSch jsch = new JSch();
        if (SftpType.PUBKEY_AUTHENTICATION.getType() == sftpConfig.getAuth() && StringUtils.isNotBlank(sftpConfig.getRsaPath())) {
            jsch.addIdentity(sftpConfig.getRsaPath().trim(), "");
        }
        Session session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
        if (session == null) {
            throw new RuntimeException("Login failed. Please check if username and password are correct");
        }

        if (SftpType.PASSWORD_AUTHENTICATION.getType() == sftpConfig.getAuth()) {
            //默认走密码验证模式
            session.setPassword(sftpConfig.getPassword());
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(sftpConfig.getTimeout());
        session.connect();

        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("create执行, 与ftp服务器建立连接成功 : " + channelSftp);
        }

        return channelSftp;

    }

    @Override
    public boolean validateObject(PooledObject<ChannelSftp> p) {
        if (p.getObject().isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public PooledObject<ChannelSftp> wrap(ChannelSftp channelSftp) {
        return new DefaultPooledObject<>(channelSftp);
    }

    // 销毁对象
    @Override
    public void destroyObject(PooledObject<ChannelSftp> p) {
        ChannelSftp channelSftp = p.getObject();

        if (channelSftp != null) {
            try {
                LOGGER.info("SftpFactory destroyObject is called");
                channelSftp.disconnect();
                channelSftp.getSession().disconnect();
            } catch (JSchException e) {
                LOGGER.error("destroySftpObject error: ", e);
            }
        } else {
            LOGGER.error("When destroyObject channelSftp, channelSftp is null");
        }


    }

}
