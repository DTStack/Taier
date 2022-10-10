package com.dtstack.taier.datasource.plugin.ftp;

import com.dtstack.taier.datasource.plugin.common.enums.SftpAuthType;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 21:41 2020/8/21
 * @Description：SFTP 处理工具
 */
@Slf4j
public class SFTPHandler {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_HOST = "host";
    public static final String KEY_PORT = "port";
    public static final String KEY_TIMEOUT = "timeout";
    public static final String KEY_RSA = "rsaPath";
    public static final String KEY_AUTHENTICATION = "auth";

    public static final int DEFAULT_HOST = 22;

    private Session session;
    private ChannelSftp channelSftp;

    private SFTPHandler(Session session, ChannelSftp channelSftp) {
        this.session = session;
        this.channelSftp = channelSftp;
    }

    /**
     * 获取实例
     *
     * @param sftpConfig
     * @return
     */
    public static SFTPHandler getInstance(Map<String, String> sftpConfig) throws JSchException {
        checkConfig(sftpConfig);

        String host = MapUtils.getString(sftpConfig, KEY_HOST);
        int port = MapUtils.getIntValue(sftpConfig, KEY_PORT, DEFAULT_HOST);
        String username = MapUtils.getString(sftpConfig, KEY_USERNAME);
        String password = MapUtils.getString(sftpConfig, KEY_PASSWORD);
        String rsaPath = MapUtils.getString(sftpConfig, KEY_RSA);
        String authType = MapUtils.getString(sftpConfig, KEY_AUTHENTICATION);

        com.jcraft.jsch.Logger logger = new SettleLogger();
        JSch.setLogger(logger);
        JSch jsch = new JSch();
        if (SftpAuthType.RSA.getType().toString().equals(authType) && StringUtils.isNotBlank(rsaPath)) {
            //需要添加私钥
            jsch.addIdentity(rsaPath.trim(), "");
        }
        Session session = jsch.getSession(username, host, port);
        if (session == null) {
            throw new SourceException("Failed to establish a connection with the ftp server, please check whether the user name and password are correct");
        }

        if (StringUtils.isBlank(authType) || SftpAuthType.PASSWORD.getType().toString().equals(authType)) {
            //默认走密码验证模式
            session.setPassword(password);
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(MapUtils.getIntValue(sftpConfig, KEY_TIMEOUT, 0));
        session.connect();

        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();

        return new SFTPHandler(session, channelSftp);
    }

    /**
     * 列出 sftp 下面的文件
     *
     * @param sftpPath sftp 路径
     * @return sftp 文件集合
     * @throws SftpException sftp 异常
     */
    public Vector listFile(String sftpPath) throws SftpException {
        return channelSftp.ls(sftpPath);
    }

    /**
     * 参数校验
     *
     * @param sftpConfig
     */
    private static void checkConfig(Map<String, String> sftpConfig) {
        if (sftpConfig == null || sftpConfig.isEmpty()) {
            throw new SourceException("The config of sftp is null");
        }

        if (StringUtils.isEmpty(sftpConfig.get(KEY_HOST))) {
            throw new SourceException("The host of sftp is null");
        }
    }

    /**
     * 关闭实例
     */
    public void close() {
        if (channelSftp != null) {
            channelSftp.disconnect();
        }

        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * 设置 Logger 为 DEBUG
     */
    static class SettleLogger implements com.jcraft.jsch.Logger {
        @Override
        public boolean isEnabled(int level) {
            return true;
        }

        @Override
        public void log(int level, String msg) {
            if (log.isDebugEnabled()) {
                log.debug(msg);
            }
        }
    }
}
