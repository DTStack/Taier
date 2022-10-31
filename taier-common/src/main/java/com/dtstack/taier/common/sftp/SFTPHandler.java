/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.sftp;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @description:
 * @create: 2021-12-15 22:44
 **/
public class SFTPHandler {
    private static final Logger logger = LoggerFactory.getLogger(SFTPHandler.class);

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_HOST = "host";
    public static final String KEY_PORT = "port";
    public static final String KEY_TIMEOUT = "timeout";
    public static final String KEY_RSA = "rsaPath";
    public static final String KEY_AUTHENTICATION = "auth";
    public static final String KEYWORD_FILE_NOT_EXISTS = "No such file";
    //有引用 勿删
    public static final String DEFAULT_RSA_PATH_TEMPLATE = "/%s/.ssh/id_rsa";

    private static final String KEY_ISUSEPOOL = "isUsePool";
    private static final String MAX_TOTAL = "maxTotal";
    private static final String MAX_IDLE = "maxIdle";
    private static final String MIN_IDLE = "minIdle";
    private static final String MAX_WAIT_MILLIS = "maxWaitMillis";
    private static final String MIN_EVICTABLE_IDLE_TIME = "minEvictableIdleTimeMillis";
    private static final String SOFT_MIN_EVICTABLE_IDLE_TIME = "softMinEvictableIdleTimeMillis";
    private static final String TIME_BETWEEN_EVICTION_RUNS = "timeBetweenEvictionRunsMillis";
    private static final int DEFAULT_TIME_OUT = 0;
    private static final String DEFAULT_PORT = "22";
    private static final String STRING_EMPTY = "";


    public static final int DEFAULT_HOST = 22;

    //SftpPoolConfig
    private static final int MAX_TOTAL_VALUE = 8;
    private static final int MAX_IDLE_VALUE = 8;
    private static final int MIN_IDLE_VALUE = 1;
    private static final long MAX_WAIT_MILLIS_VALUE = 1000L * 60L * 60L;
    private static final long MIN_EVICTABLE_IDLE_TIME_VALUE = -1;
    private static final long SOFT_MIN_EVICTABLE_IDLE_TIME_VALUE = 1000L * 60L * 30L;
    private static final long TIME_BETWEEN_EVICTION_RUNS_VALUE = 1000L * 60L * 5L;
    private static final boolean ISUSEPOOL_VALUE = true;

    private ChannelSftp channelSftp;
    private SftpPool sftpPool;

    private static Map<String, SftpPool> sftpPoolMap = Maps.newConcurrentMap();

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
            put(KEY_TIMEOUT, timeout == null ? null : timeout.toString());
        }});
    }

    public static SFTPHandler getInstance(Map<String, String> sftpConfig) {
        checkConfig(sftpConfig);

        boolean isUsePool = MapUtils.getBoolean(sftpConfig, KEY_ISUSEPOOL, ISUSEPOOL_VALUE);

        ChannelSftp channelSftp = null;
        SftpPool sftpPool = null;
        if (isUsePool) {
            logger.info("get channelSftp from SftpPool!");
            sftpPool = getSftpPool(sftpConfig);
            channelSftp = sftpPool.borrowObject();
        } else {
            logger.info("get channelSftp from native!");
            SftpFactory sftpFactory = new SftpFactory(sftpConfig);
            channelSftp = sftpFactory.create();
        }

        setSessionTimeout(sftpConfig, channelSftp);
        return new SFTPHandler(channelSftp, sftpPool);
    }

    static class SettleLogger implements com.jcraft.jsch.Logger {
        @Override
        public boolean isEnabled(int level) {
            return true;
        }

        @Override
        public void log(int level, String msg) {
            if (logger.isDebugEnabled()) {
                logger.debug(msg);
            }
        }
    }

    private static void checkConfig(Map<String, String> sftpConfig) {
        if (sftpConfig == null || sftpConfig.isEmpty()) {
            throw new IllegalArgumentException("The config of sftp is null");
        }

        if (StringUtils.isEmpty(sftpConfig.get(KEY_HOST))) {
            throw new IllegalArgumentException("The host of sftp is null");
        }
    }

    public void downloadFile(String ftpPath, String localPath) {
        if (!isFileExist(ftpPath)) {
            throw new DtCenterDefException("File not exist on sftp:" + ftpPath);
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(localPath));
            channelSftp.get(ftpPath, os);
        } catch (Exception e) {
            throw new DtCenterDefException("download file from sftp error");
        } finally {
            if (os != null) {
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
     * <p>
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
                    if (".".equals(filename) || "..".equals(filename) || "__MACOSX".equals(filename)) {
                        continue;
                    }
                    SftpATTRS attrs = str.getAttrs();
                    boolean isdir = attrs.isDir();
                    String localFilePath = localDir + "/" + filename;
                    String ftpFilePath = ftpDir + "/" + filename;
                    if (isdir) {
                        File dir2 = new File(localFilePath);
                        if (!dir2.exists()) {
                            logger.info("local file path mkdir {}:", localFilePath);
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
            logger.error("sftp downloadDir error ", e);
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
        if (dir.exists()) {
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

    private boolean delLocalDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = delLocalDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
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
            if (!mkdir(dstDir)) {
                logger.error("create path error:" + dstDir);
                return false;
            }
            File[] files = file.listFiles();
            for (File file1 : files) {
                uploadDir(dstDir, file1.getPath());
            }
        } else {
            return upload(dstDir, file.getName(), file.getParent());
        }
        return false;
    }

    public boolean upload(String baseDir, String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new DtCenterDefException("该路径不存在或不是文件路径");
        }
        return upload(baseDir, file.getName(), file.getParent());
    }

    /**
     * sftp 上传文件 且会覆盖同名文件
     *
     * @param baseDir  目标路径
     * @param fileName 文件名
     * @param filePath 本地文件目录
     */
    public boolean upload(String baseDir, String fileName, String filePath) {
        logger.info("：baseDir=" + baseDir);
        try {
            //检查路径
            if (!mkdir(baseDir)) {
                logger.error("create path error:" + baseDir);
                return false;
            }
            String dst = baseDir + "/" + fileName;
            String src = filePath + "/" + fileName;
            logger.info("begin upload，local path：[" + src + "] target path：[" + dst + "]");
            channelSftp.put(src, dst);
            logger.info("upload success");
            return true;
        } catch (Exception e) {
            logger.error("upload fail", e);
            return false;
        }
    }

    /**
     * sftp 删除文件
     *
     * @param dst 目标路径
     */
    public boolean remove(String dst) {
        try {
            logger.info("begin delete，target path：[" + dst + "]");
            channelSftp.rm(dst);
            logger.info("delete success");
            return true;
        } catch (Exception e) {
            logger.error("delete fail", e);
            return false;
        }
    }


    public boolean isFileExist(String ftpPath) {
        try {
            channelSftp.lstat(ftpPath);
            return true;
        } catch (SftpException e) {
            if (e.getMessage().contains(KEYWORD_FILE_NOT_EXISTS)) {
                return false;
            } else {
                throw new DtCenterDefException("Check file exists error");
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
                if (".".equals(filename) || "..".equals(filename)) {
                    continue;
                }
                SftpATTRS attrs = str.getAttrs();
                if (attrs.isDir()) {
                    deleteDir(ftpPath + "/" + filename);
                } else {
                    channelSftp.rm(ftpPath + "/" + filename);
                }
            }
            if (channelSftp.ls(ftpPath).size() == 2) {
                channelSftp.rmdir(ftpPath);
            }
        } catch (SftpException e) {
            logger.error("", e);
            throw new DtCenterDefException("删除sftp路径失败，sftpPath=" + ftpPath);
        }
    }

    /**
     * 支持创建多层路径
     *
     * @param path
     * @return
     */
    public boolean mkdir(String path) {
        String[] split = path.split("/");
        StringBuilder currPath = new StringBuilder();
        for (String dir : split) {
            currPath.append("/").append(dir).toString();
            try {
                channelSftp.cd(currPath.toString());
            } catch (SftpException sException) {
                if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
                    try {
                        channelSftp.mkdir(currPath.toString());
                    } catch (SftpException e) {
                        logger.error("sftp isExist error", e);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void close() {
        if (sftpPool != null) {
            sftpPool.returnObject(channelSftp);
        } else {
            try {
                channelSftp.disconnect();
                channelSftp.getSession().disconnect();
            } catch (Exception e) {
                logger.error("close channelSftp error: {}", e.getMessage());
            }
        }
    }

    private static SftpPool getSftpPool(Map<String, String> sftpConfig) {
        String sftpPoolKey = getSftpPoolKey(sftpConfig);
        SftpPool sftpPool = sftpPoolMap.computeIfAbsent(sftpPoolKey, k -> {
            SftpPool sftpPool1 = null;
            //先检测sftp主机验证能否通过，再缓存
            SftpFactory sftpFactory = new SftpFactory(sftpConfig);
            ChannelSftp channelSftpTest = sftpFactory.create();
            if (channelSftpTest != null) {
                //释放资源，防止内存泄漏
                try {
                    channelSftpTest.disconnect();
                    channelSftpTest.getSession().disconnect();
                } catch (JSchException e) {
                    logger.error("channelSftpTest get session error", e);
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
                String message = String.format("SFTPHandler connect sftp fail : [%s]",
                        "message:host =" + MapUtils.getString(sftpConfig, KEY_HOST) +
                                ",username = " + MapUtils.getString(sftpConfig, KEY_USERNAME));
                logger.error(message);
            }
            return sftpPool1;
        });
        return sftpPool;

    }

    private static String getSftpPoolKey(Map<String, String> sftpConfig) {
        return MapUtils.getString(sftpConfig, KEY_HOST, STRING_EMPTY).trim() +
                MapUtils.getString(sftpConfig, KEY_PORT, DEFAULT_PORT).trim() +
                MapUtils.getString(sftpConfig, KEY_USERNAME, STRING_EMPTY).trim() +
                MapUtils.getString(sftpConfig, KEY_PASSWORD, STRING_EMPTY).trim();
    }

    private static void setSessionTimeout(Map<String, String> sftpConfig, ChannelSftp channelSftp) {
        Session sessionSftp;
        try {
            sessionSftp = channelSftp.getSession();
            sessionSftp.setTimeout(MapUtils.getIntValue(sftpConfig, KEY_TIMEOUT, DEFAULT_TIME_OUT));
        } catch (JSchException e) {
            logger.error("get sessionSftp error", e);
            throw new RuntimeException("获取sessionSftp异常, 请检查sessionSftp是否正常", e);
        }
    }
}
