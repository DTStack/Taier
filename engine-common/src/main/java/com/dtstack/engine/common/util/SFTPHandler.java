package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.common.sftp.SftpFactory;
import com.dtstack.engine.common.sftp.SftpPool;
import com.dtstack.engine.common.sftp.SftpPoolConfig;
import com.google.common.collect.Maps;
import com.jcraft.jsch.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


public class SFTPHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SFTPHandler.class);

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_TIMEOUT = "timeout";
    private static final String MAX_TOTAL = "maxTotal";
    private static final String MAX_IDLE = "maxIdle";
    private static final String MIN_IDLE = "minIdle";
    private static final String MAX_WAIT_MILLIS = "maxWaitMillis";
    private static final String MIN_EVICTABLE_IDLE_TIME = "minEvictableIdleTimeMillis";
    private static final String SOFT_MIN_EVICTABLE_IDLE_TIME = "softMinEvictableIdleTimeMillis";
    private static final String TIME_BETWEEN_EVICTION_RUNS = "timeBetweenEvictionRunsMillis";
    private static final String KEY_RSA = "rsaPath";
    private static final String KEY_AUTHENTICATION = "auth";
    private static final int DEFAULT_TIME_OUT = 0;
    private static final String DEFAULT_PORT = "22";

    //SftpPoolConfig
    private static final int MAX_TOTAL_VALUE = 8;
    private static final int MAX_IDLE_VALUE = 8;
    private static final int MIN_IDLE_VALUE = 1;
    private static final long MAX_WAIT_MILLIS_VALUE = 1000L * 60L * 60L;
    private static final long MIN_EVICTABLE_IDLE_TIME_VALUE = -1;
    private static final long SOFT_MIN_EVICTABLE_IDLE_TIME_VALUE = 1000L * 60L * 30L;
    private static final long TIME_BETWEEN_EVICTION_RUNS_VALUE = 1000L * 60L * 5L;


    private static final String KEYWORD_FILE_NOT_EXISTS = "No such file";

    private static Map<String, SftpPool> sftpPoolMap = Maps.newConcurrentMap();

    private ChannelSftp channelSftp;
    private SftpPool sftpPool;

    private SFTPHandler(ChannelSftp channelSftp, SftpPool sftpPool) {
        this.channelSftp = channelSftp;
        this.sftpPool = sftpPool;
    }

    public static SFTPHandler getInstance(String host, int port, String username, String password, Integer timeout) {
        return getInstance(new HashMap<String, String>() {{
            put(KEY_HOST, host);
            put(KEY_PORT, String.valueOf(port));
            put(KEY_USERNAME, username);
            put(KEY_PASSWORD, password);
            put(MAX_WAIT_MILLIS, timeout == null ? null : timeout.toString());
        }});
    }

    public static SFTPHandler getInstance(Map<String, String> sftpConfig){
        checkConfig(sftpConfig);
        String sftpPoolKey = MapUtils.getString(sftpConfig, KEY_HOST).trim() +
                MapUtils.getString(sftpConfig, KEY_PORT, DEFAULT_PORT).trim() +
                MapUtils.getString(sftpConfig, KEY_USERNAME).trim() +
                MapUtils.getString(sftpConfig, KEY_PASSWORD).trim();

        SftpPool sftpPool = sftpPoolMap.computeIfAbsent(sftpPoolKey, k -> {
            SftpPool sftpPool1 = null;
            //先检测sftp主机验证能否通过，再缓存
            SftpFactory sftpFactory = new SftpFactory(sftpConfig);
            ChannelSftp channelSftpTest = sftpFactory.create();
            if(channelSftpTest != null) {
                //释放资源，防止内存泄漏
                try {
                    channelSftpTest.disconnect();
                    channelSftpTest.getSession().disconnect();
                } catch (JSchException e) {
                    logger.error("channelSftpTest获取Session异常", e);
                }
                int maxTotal = MapUtils.getInteger(sftpConfig, MAX_TOTAL, MAX_TOTAL_VALUE);
                int maxIdle = MapUtils.getInteger(sftpConfig, MAX_IDLE, MAX_IDLE_VALUE);
                int minIdle = MapUtils.getInteger(sftpConfig, MIN_IDLE, MIN_IDLE_VALUE);
                long maxWaitMillis = MapUtils.getLongValue(sftpConfig, MAX_WAIT_MILLIS, MAX_WAIT_MILLIS_VALUE);
                long minEvictableIdleTimeMillis = MapUtils.getLongValue(sftpConfig, MIN_EVICTABLE_IDLE_TIME, MIN_EVICTABLE_IDLE_TIME_VALUE);
                long softMinEvictableIdleTimeMillis = MapUtils.getLongValue(sftpConfig, SOFT_MIN_EVICTABLE_IDLE_TIME, SOFT_MIN_EVICTABLE_IDLE_TIME_VALUE);
                long timeBetweenEvictionRunsMillis = MapUtils.getLongValue(sftpConfig, TIME_BETWEEN_EVICTION_RUNS, TIME_BETWEEN_EVICTION_RUNS_VALUE);
                SftpPoolConfig sftpPoolConfig = new SftpPoolConfig(maxTotal, maxIdle, minIdle);
                sftpPoolConfig.setMaxWaitMillis(maxWaitMillis); //从idle队列里面取对象时，阻塞时最大等待时长
                sftpPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis); //处于idle状态超过此值时，会被destory
                sftpPoolConfig.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis); //处于idle状态超过此值时，会被destory, 保留minIdle个空闲连接数。默认为-1
                sftpPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis); //evict线程每次间隔时间
                sftpPool1 = new SftpPool(sftpFactory, sftpPoolConfig);
            } else {
                String message = String.format("SFTPHandler连接sftp失败 : [%s]",
                        "message:host =" + MapUtils.getString(sftpConfig, KEY_HOST) +
                                ",username = " + MapUtils.getString(sftpConfig, KEY_USERNAME));
                logger.error(message);
            }
            return sftpPool1;
        });

        ChannelSftp channelSftp = sftpPool.borrowObject();
        setSessionTimeout(sftpConfig, channelSftp);
        return new SFTPHandler(channelSftp, sftpPool);
    }

    private static void checkConfig(Map<String, String> sftpConfig){
        if(sftpConfig == null || sftpConfig.isEmpty()){
            throw new IllegalArgumentException("The config of sftp is null");
        }

        if(StringUtils.isEmpty(sftpConfig.get(KEY_HOST))){
            throw new IllegalArgumentException("The host of sftp is null");
        }
    }

    private static void setSessionTimeout(Map<String, String> sftpConfig, ChannelSftp channelSftp){
        Session sessionSftp;
        try {
            sessionSftp = channelSftp.getSession();
            sessionSftp.setTimeout(MapUtils.getIntValue(sftpConfig, KEY_TIMEOUT, DEFAULT_TIME_OUT));
        } catch (JSchException e) {
            logger.error("获取sessionSftp异常", e);
            throw new RuntimeException("获取sessionSftp异常, 请检查sessionSftp是否正常", e);
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
                    String ftpFilePath;
                    if (channelSftp.stat(ftpDir).isDir()){
                        ftpFilePath = ftpDir + "/" + filename;
                    } else {
                        ftpFilePath = ftpDir;
                    }
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

    private boolean isFileExist(String ftpPath){
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
        sftpPool.returnObject(channelSftp);
    }

    public String loadFromSftp(String fileName, String remoteDir, String localDir){
        String remoteFile = remoteDir + File.separator + fileName;
        String localFile = localDir + File.separator + fileName;
        try {
            if (new File(fileName).exists()){
                return fileName;
            } else {
                downloadFile(remoteFile, localFile);
                return localFile;
            }
        } catch (Exception e){
            logger.error("load file error: ", e);
            return fileName;
        } finally {
            close();
        }
    }
}