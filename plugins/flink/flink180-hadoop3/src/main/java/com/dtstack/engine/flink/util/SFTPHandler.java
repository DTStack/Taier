package com.dtstack.engine.flink.util;

import com.jcraft.jsch.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;


public class SFTPHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SFTPHandler.class);

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

    public static SFTPHandler getInstance(String host, int port, String username, String password, Integer timeout) {
        return getInstance(new HashMap<String, String>() {{
            put(KEY_HOST, host);
            put(KEY_PORT, String.valueOf(port));
            put(KEY_USERNAME, username);
            put(KEY_PASSWORD, password);
            put(KEY_TIMEOUT, timeout == null ? null : timeout.toString());
        }});
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
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * 下载目录
     *
     * 覆盖本地路径
     *
     * @param ftpDir
     * @param localDir
     * @return
     */
    public int downloadDir(String ftpDir, String localDir) {
        int sum = 0;
        try {
            reCreateLocalDir(localDir);

            try {
                Vector files = channelSftp.ls(ftpDir);
                if (files == null) {
                    return 0;
                }
                for (Iterator<ChannelSftp.LsEntry> iterator = files.iterator(); iterator.hasNext(); ) {
                    ChannelSftp.LsEntry str = iterator.next();
                    String filename = str.getFilename();
                    if (filename.equals(".") || filename.equals("..")) {
                        continue;
                    }
                    SftpATTRS attrs = str.getAttrs();
                    boolean isdir = attrs.isDir();
                    String localFilePath = localDir + "/" + filename;
                    String ftpFilePath = ftpDir + "/" + filename;
                    if (isdir) {
                        File dir2 = new File(localFilePath);
                        if (!dir2.exists()) {
                            logger.info("local file path mkdir :", localFilePath);
                            dir2.mkdir();
                        }
                        sum += downloadDir(ftpFilePath, localFilePath);
                    } else {
                        downloadFile(ftpFilePath, localFilePath);
                        sum++;
                    }
                }
            } catch (SftpException e) {
                logger.error("", e);
            }
            return sum;
        } catch (Exception e) {
            logger.error("sftp downloadDir error {}", e);
            return -1;
        }
    }


    /**
     * 重新创建本地路径
     *
     * @param localDir
     * @return
     */
    private boolean reCreateLocalDir(String localDir) {
        File dir = new File(localDir);
        if(dir.exists()) {
            delLocalDir(dir);
        }
        return mkLocalDir(localDir);
    }

    private boolean mkLocalDir(String localDir) {
        File dir = new File(localDir);
        if (dir.exists()) {
            return true;
        } else {
            String parentDir = localDir.substring(0, localDir.lastIndexOf("/"));
            mkLocalDir(parentDir);
            return dir.mkdir();
        }
    }

    private  boolean delLocalDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = delLocalDir(new File(dir, children[i]));
                if (!success)
                    return false;
            }

        }
        if (dir.delete()) {
            logger.info("delete success,dir={}", dir);
            return true;
        } else {
            logger.info("delete failed,dir={}", dir);

        }
        return false;
    }

    public Vector listFile(String ftpPath) throws SftpException {
        return channelSftp.ls(ftpPath);
    }


    public boolean uploadDir(String dstDir, String srcDir) {
        File file = new File(srcDir);
        if (file.isDirectory()) {
            dstDir += "/" + file.getName();
            if (!createIfNotExist(dstDir)) {
                logger.error("创建sftp服务器路径失败:" + dstDir);
                return false;
            }
            File[] files = file.listFiles();
            for (File file1 : files) {
                uploadDir(dstDir, file1.getPath());
            }
        } else {
            return upload(dstDir, file.getName(), file.getParent(), true);
        }
        return false;
    }

    public boolean upload(String baseDir, String filePath) {
        return upload(baseDir, filePath, false);
    }

    public boolean upload(String baseDir, String filePath, boolean multiplex) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("该路径不存在或不是文件路径");
        }
        return upload(baseDir, file.getName(), file.getParent(), multiplex);
    }

    /**
     *  sftp 上传文件 且会覆盖同名文件
     *  @param baseDir 目标路径
     *  @param fileName 文件名
     *  @param filePath 本地文件目录
     */
    public boolean upload(String baseDir, String fileName, String filePath, boolean multiplex) {
        logger.info("路径：baseDir="+baseDir);
        try {
            //检查路径
            if(!createIfNotExist(baseDir)){
                logger.error("创建sftp服务器路径失败:" + baseDir);
                return false;
            }
            String dst = baseDir + "/" + fileName;
            String src = filePath + "/" + fileName;
            logger.info("开始上传，本地服务器路径：["+src +"]目标服务器路径：["+dst+"]");
            channelSftp.put(src, dst);
            logger.info("上传成功");
            return true;
        } catch (Exception e) {
            logger.error("上传失败", e);
            return false;
        } finally {
            if (!multiplex) {
                close();
            }
        }
    }

    public boolean uploadInputStreamToHdfs(byte[] bytes, String dstPath) {
        return uploadInputStreamToHdfs(bytes, dstPath, false);
    }

    /**
     * 上传文件流到sftp
     *
     * 同名覆盖
     * @param bytes
     * @param dstPath 目标路径（含文件名）
     * @param multiplex 连接是否需要关闭
     * @return
     */
    public boolean uploadInputStreamToHdfs(byte[] bytes, String dstPath, boolean multiplex) {
        logger.info("路径：baseDir=" + dstPath);
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            File file = new File(dstPath);
            //检查路径
            if (!createIfNotExist(file.getParent())) {
                logger.error("创建sftp服务器路径失败:" + file.getParent());
                return false;
            }
            channelSftp.put(is, dstPath);
            logger.info("uploadInputStreamToHdfs success");
            return true;
        } catch (Exception e) {
            logger.error("上传失败", e);
            return false;
        } finally {
            if (!multiplex) {
                close();
            }
        }
    }



    /**
     * 判断文件夹是否存在
     * true 目录创建成功，false 目录创建失败
     * @param filePath 文件夹路径
     * @return
     */
    public boolean createIfNotExist(String filePath) {
        String paths[] = filePath.split("\\/");
        String dir = paths[0];
        for (int i = 0; i < paths.length - 1; i++) {
            dir = dir + "/" + paths[i + 1];
            if (!mkdir(dir)) return false;
        }
        return true;
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

    public void deleteDir(String ftpPath) {
        try {
            channelSftp.cd(ftpPath);
        } catch (SftpException e) {
            logger.info("", e);
            return;
        }
        try {
            Vector files = channelSftp.ls(ftpPath);
            for (Iterator<ChannelSftp.LsEntry> iterator = files.iterator(); iterator.hasNext(); ) {
                ChannelSftp.LsEntry str = iterator.next();
                String filename = str.getFilename();
                if (filename.equals(".") || filename.equals("..")) {
                    continue;
                }
                SftpATTRS attrs = str.getAttrs();
                if (attrs.isDir()) {
                    deleteDir(ftpPath + "/" + filename);
                } else {
                    channelSftp.rm(ftpPath + "/" + filename);
                }
            }
            if(channelSftp.ls(ftpPath).size() == 2) {
                channelSftp.rmdir(ftpPath);
            }
        } catch (SftpException e) {
            logger.error("", e);
            throw new RuntimeException("删除sftp路径失败，sftpPath=" + ftpPath);
        }
    }

    public boolean mkdir(String path) {
        try{
            channelSftp.cd(path);
        }catch(SftpException sException){
            if(ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id){
                try {
                    channelSftp.mkdir(path);
                } catch (SftpException e) {
                    logger.error("sftp isExist error {}", e);
                    return false;
                }
            }
        }
        return true;
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
