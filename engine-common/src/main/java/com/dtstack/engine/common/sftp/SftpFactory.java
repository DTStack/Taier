package com.dtstack.engine.common.sftp;

import com.dtstack.engine.common.enums.SftpType;
import com.dtstack.engine.common.util.RetryUtil;
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

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SftpFactory.class);

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
            logger.error("Create ChannelSftp error : " + e);
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

        logger.info("create执行, 与ftp服务器建立连接成功 : " + channelSftp);

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
                logger.info("SftpFactory destroyObject is called");
                channelSftp.disconnect();
                channelSftp.getSession().disconnect();
            } catch (JSchException e) {
                logger.error("destroySftpObject error: ", e);
            }
        } else {
            logger.error("When destroyObject channelSftp, channelSftp is null");
        }


    }

}
