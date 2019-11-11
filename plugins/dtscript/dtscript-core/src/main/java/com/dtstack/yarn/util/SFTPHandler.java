package com.dtstack.yarn.util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;


public class SFTPHandler {

    private static final String KEY_USERNAME = "sftpConf.username";
    private static final String KEY_PASSWORD = "sftpConf.password";
    private static final String KEY_HOST = "sftpConf.host";
    private static final String KEY_PORT = "sftpConf.port";
    private static final String KEY_TIMEOUT = "sftpConf.timeout";

    private static final String KEYWORD_FILE_NOT_EXISTS = "No such file";

    private static final int DEFAULT_HOST = 22;

    private Session session;
    private ChannelSftp channelSftp;

    private SFTPHandler(Session session, ChannelSftp channelSftp) {
        this.session = session;
        this.channelSftp = channelSftp;
    }

    public static SFTPHandler getInstance(Configuration sftpConfig){
        checkConfig(sftpConfig);

        String host = sftpConfig.get(KEY_HOST);
        int port = Integer.parseInt(sftpConfig.get(KEY_PORT));
        String username = sftpConfig.get(KEY_USERNAME);

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            if (session == null) {
                throw new RuntimeException("Login failed. Please check if username and password are correct");
            }

            session.setPassword(sftpConfig.get(KEY_PASSWORD));
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            if (sftpConfig.get(KEY_TIMEOUT)!=null){
                session.setTimeout(Integer.parseInt(sftpConfig.get(KEY_TIMEOUT)));
            }
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

    private static void checkConfig(Configuration sftpConfig){
        if(sftpConfig == null){
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
