package com.dtstack.engine.base.filesystem.manager.sftp;

import com.dtstack.engine.common.enums.SftpType;
import com.dtstack.engine.common.util.RetryUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * company: www.dtstack.com
 * author: yuemo
 * create: 2020-01-19
 */
public class SftpFactory extends BasePooledObjectFactory<ChannelSftp>  {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SftpFactory.class);

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_TIMEOUT = "timeout";
    private static final String KEY_RSA = "rsaPath";
    private static final String KEY_AUTHENTICATION = "auth";

    private static final int DEFAULT_PORT = 22;

    private static final int DEFAULT_TIME_OUT = 0;

    private static final int DEFAULT_RETRY_TIMES = 3;
    private static final int SLEEP_TIME_MILLI_SECOND = 2000;

    private String host;
    private int port;
    private String username;
    private String password;
    private String rsaPath;
    private int authType;
    private int timeout;

    public SftpFactory(Map<String, String> sftpConfig) {
        checkConfig(sftpConfig);

        host = MapUtils.getString(sftpConfig, KEY_HOST);
        port = MapUtils.getIntValue(sftpConfig, KEY_PORT, DEFAULT_PORT);
        username = MapUtils.getString(sftpConfig, KEY_USERNAME);
        password = MapUtils.getString(sftpConfig, KEY_PASSWORD);
        rsaPath = MapUtils.getString(sftpConfig, KEY_RSA);
        authType = MapUtils.getInteger(sftpConfig, KEY_AUTHENTICATION, SftpType.PASSWORD_AUTHENTICATION.getType());
        timeout = MapUtils.getIntValue(sftpConfig, KEY_TIMEOUT, DEFAULT_TIME_OUT);
    }

    private void checkConfig(Map<String, String> sftpConfig) {
        if(sftpConfig == null || sftpConfig.isEmpty()){
            throw new IllegalArgumentException("The config of sftp is null");
        }
        if(StringUtils.isEmpty(sftpConfig.get(KEY_HOST))){
            throw new IllegalArgumentException("The host of sftp is null");
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
            if (SftpType.PUBKEY_AUTHENTICATION.getType() == authType && StringUtils.isNotBlank(rsaPath)) {
                jsch.addIdentity(rsaPath.trim(), "");
            }
            Session session = jsch.getSession(username, host, port);
            if (session == null) {
                throw new RuntimeException("Login failed. Please check if username and password are correct");
            }

            if (SftpType.PASSWORD_AUTHENTICATION.getType()==authType) {
                //默认走密码验证模式
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(timeout);
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
