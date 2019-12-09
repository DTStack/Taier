package com.dtstack.engine.flink.util;

import com.jcraft.jsch.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Map;
import java.util.Properties;


public class SFTPHandler {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_TIMEOUT = "timeout";
    private static final String KEY_RSA = "rsaPath";
    private static final String KEY_AUTHENTICATION = "auth";

    //密码校验
    private static final String PASSWORD_AUTHENTICATION = "1";

    //免密登录   需要私钥路径
    private static final String PUBKEY_AUTHENTICATION = "2";

    private static final String KEYWORD_FILE_NOT_EXISTS = "No such file";

    private static final int DEFAULT_HOST = 22;

    private Session session;
    private ChannelSftp channelSftp;

    private SFTPHandler(Session session, ChannelSftp channelSftp) {
        this.session = session;
        this.channelSftp = channelSftp;
    }

    public static SFTPHandler getInstance(Map<String, String> sftpConfig){
        checkConfig(sftpConfig);

        String host = MapUtils.getString(sftpConfig, KEY_HOST);
        int port = MapUtils.getIntValue(sftpConfig, KEY_PORT, DEFAULT_HOST);
        String username = MapUtils.getString(sftpConfig, KEY_USERNAME);
        String password = MapUtils.getString(sftpConfig, KEY_PASSWORD);
        String rsaPath = MapUtils.getString(sftpConfig, KEY_RSA);
        String authType = MapUtils.getString(sftpConfig, KEY_AUTHENTICATION);

        try {
            JSch jsch = new JSch();
            if (PUBKEY_AUTHENTICATION.equals(authType) && StringUtils.isNotBlank(rsaPath)) {
                jsch.addIdentity(rsaPath.trim(), "");
            }
            Session session = jsch.getSession(username, host, port);
            if (session == null) {
                throw new RuntimeException("Login failed. Please check if username and password are correct");
            }

            if (authType == null || PASSWORD_AUTHENTICATION.equals(authType)) {
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
        } catch (Exception e){
            String message = String.format("与ftp服务器建立连接失败 : [%s]",
                    "message:host =" + host + ",username = " + username + ",port =" + port);
            throw new RuntimeException(message, e);
        }
    }

    private static void checkConfig(Map<String, String> sftpConfig){
        if(sftpConfig == null || sftpConfig.isEmpty()){
            throw new IllegalArgumentException("The config of sftp is null");
        }

        if(StringUtils.isEmpty(sftpConfig.get(KEY_HOST))){
            throw new IllegalArgumentException("The host of sftp is null");
        }
    }

    public void downloadFile(String ftpPath, String localPath){
        if(!isFileExist(ftpPath)){
            throw new RuntimeException("File not exist on sftp:" + ftpPath);
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(localPath));
            channelSftp.get(ftpPath, os);
        } catch (Exception e){
            throw new RuntimeException("download file from sftp error", e);
        } finally {
            if(os != null){
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isFileExist(String ftpPath){
        try {
            channelSftp.lstat(ftpPath);
            return true;
        } catch (SftpException e){
            if (e.getMessage().contains(KEYWORD_FILE_NOT_EXISTS)) {
                return false;
            } else {
                throw new RuntimeException("Check file exists error", e);
            }
        }
    }

    public void close(){
        if (channelSftp != null) {
            channelSftp.disconnect();
        }

        if (session != null) {
            session.disconnect();
        }
    }
}
