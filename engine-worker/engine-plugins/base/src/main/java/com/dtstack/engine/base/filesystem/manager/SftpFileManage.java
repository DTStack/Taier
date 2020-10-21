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

package com.dtstack.engine.base.filesystem.manager;

import com.dtstack.engine.base.filesystem.manager.sftp.SftpFactory;
import com.dtstack.engine.base.filesystem.manager.sftp.SftpPool;
import com.dtstack.engine.base.filesystem.manager.sftp.SftpPoolConfig;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Date: 2020/7/20
 * Company: www.dtstack.com
 * @author maqi
 */
public class SftpFileManage implements IFileManage {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SftpFileManage.class);

    public static final String PREFIX = "sftp://";
    public static final String KEY_USERNAME = "username";
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
    public static final String KEY_RSA = "rsaPath";
    public static final String KEY_AUTHENTICATION = "auth";
    private static final int DEFAULT_TIME_OUT = 0;
    private static final String DEFAULT_PORT = "22";
    private static final String STRING_EMPTY = "";
    //有引用 勿删
    public static final String DEFAULT_RSA_PATH_TEMPLATE = "/%s/.ssh/id_rsa";

    public static final String CONSOLE_HADOOP_CONFIG = "/console/hadoop_config";
    public static final String STREAM_RESOURCE = "/stream/resource";
    public static final String BATCH_EXECUTEFILE = "/batch/execute_file";

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

    private SftpFileManage(ChannelSftp channelSftp, SftpPool sftpPool) {
        this.channelSftp = channelSftp;
        this.sftpPool = sftpPool;
    }

    public static SftpFileManage getInstance(String host, int port, String username, String password, Integer timeout) {
        return getInstance(new HashMap<String, String>() {{
            put(KEY_HOST, host);
            put(KEY_PORT, String.valueOf(port));
            put(KEY_USERNAME, username);
            put(KEY_PASSWORD, password);
            put(MAX_WAIT_MILLIS, timeout == null ? null : timeout.toString());
        }});
    }

    public static SftpFileManage getInstance(Map<String, String> sftpConfig) {
        checkConfig(sftpConfig);
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
                    LOG.error("channelSftpTest获取Session异常", e);
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
                LOG.error(message);
            }
            return sftpPool1;
        });

        ChannelSftp channelSftp = sftpPool.borrowObject();
        setSessionTimeout(sftpConfig, channelSftp);
        return new SftpFileManage(channelSftp, sftpPool);
    }

    private static void checkConfig(Map<String, String> sftpConfig) {
        if (sftpConfig == null || sftpConfig.isEmpty()) {
            throw new IllegalArgumentException("The config of sftp is null");
        }

        if (StringUtils.isEmpty(sftpConfig.get(KEY_HOST))) {
            throw new IllegalArgumentException("The host of sftp is null");
        }
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
            LOG.error("获取sessionSftp异常", e);
            throw new RuntimeException("获取sessionSftp异常, 请检查sessionSftp是否正常", e);
        }
    }


    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public boolean canHandle(String remotePath) {
        return remotePath.contains(PREFIX);
    }

    public boolean downloadFile(String remotePath, String localPath) {
        return downloadFile(remotePath, localPath, true);
    }

    @Override
    public boolean downloadFile(String remotePath, String localPath, boolean isEnd) {
        if (!isFileExist(remotePath)) {
            LOG.info("File not exist on sftp:" + remotePath);
            return false;
        }

        if (channelSftp.isClosed()) {
            channelSftp = sftpPool.borrowObject();
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(localPath));
            channelSftp.get(remotePath, os);
        } catch (Exception e) {
            LOG.error("download file from sftp error:", e);
            return false;
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    LOG.error("", e);
                }
            }
            if (isEnd) {
                close();
            }
        }
        return true;
    }

    @Override
    public boolean downloadDir(String remoteDir, String localDir) {
        try {
            File localDirPath = new File(localDir);
            if (!localDirPath.exists()) {
                boolean mkdirs = localDirPath.mkdirs();
                LOG.info("local file path {}  mkdir {} :", localDir, mkdirs);
            }

            if (channelSftp.isClosed()) {
                channelSftp = sftpPool.borrowObject();
            }

            Vector files = channelSftp.ls(remoteDir);
            if (files == null) {
                return false;
            }

            for (Iterator<ChannelSftp.LsEntry> iterator = files.iterator(); iterator.hasNext(); ) {
                ChannelSftp.LsEntry str = iterator.next();
                String filename = str.getFilename();
                if (".".equals(filename) || "..".equals(filename)) {
                    continue;
                }
                SftpATTRS attrs = str.getAttrs();
                boolean isdir = attrs.isDir();
                String localFilePath = localDir + "/" + filename;
                String ftpFilePath;
                if (channelSftp.stat(remoteDir).isDir()) {
                    ftpFilePath = remoteDir + "/" + filename;
                } else {
                    ftpFilePath = remoteDir;
                }
                if (isdir) {
                    File dir2 = new File(localFilePath);
                    if (!dir2.exists()) {
                        LOG.info("local file path mkdir :", localFilePath);
                        dir2.mkdir();
                    }
                    downloadDir(ftpFilePath, localFilePath);
                } else {
                    downloadFile(ftpFilePath, localFilePath, false);
                }
            }

            return true;
        } catch (Exception e) {
            LOG.error("sftp downloadDir error {}", e);
            clearDownloadFile(localDir);
            return false;
        }
    }

    @Override
    public boolean uploadFile(String remotePath, String localPath, boolean isEnd) {
        try {
            LOG.info("远程路径：remotePath:{}, 本地路径：localPath:{}", remotePath, localPath);
            channelSftp.put(localPath, remotePath);
        } catch (SftpException e) {
            LOG.error("上传失败", e);
            return false;
        }
        return true;
    }


    @Override
    public boolean uploadFile(String remotePath, String localPath, String fileName, boolean isEnd) {
        LOG.info("路径：localPath=" + localPath);
        try {
            //检查路径
            if (!mkdir(remotePath)) {
                LOG.error("创建sftp服务器路径失败:" + remotePath);
                return false;
            }
            String dst = remotePath + "/" + fileName;
            LOG.info("开始上传，本地服务器路径：[" + localPath + "]目标服务器路径：[" + dst + "]");
            channelSftp.put(localPath, dst);
            LOG.info("上传成功");
            return true;
        } catch (Exception e) {
            LOG.error("上传失败", e);
            return false;
        } finally {
            if (!isEnd) {
                close();
            }
        }
    }

    @Override
    public boolean uploadDir(String remotePath, String localPath) {
        File file = new File(localPath);
        if (file.isDirectory()) {
            if (!mkdir(remotePath)) {
                LOG.error("创建sftp服务器路径失败:" + remotePath);
                return false;
            }
            File[] files = file.listFiles();
            for (File file1 : files) {
                uploadDir(remotePath, file1.getPath());
            }
        } else {
            return uploadFile(remotePath, file.getPath(), false);
        }
        return true;
    }

    @Override
    public boolean deleteFile(String remotePath) {
        if (this.isFileExist(remotePath)) {
            try {
                channelSftp.rm(remotePath);
            } catch (SftpException e) {
                LOG.error("", e);
                throw new RuntimeException("删除sftp路径失败，sftpPath=" + remotePath);
            }
        }
        return true;
    }

    @Override
    public boolean deleteDir(String remotePath) {
        try {
            channelSftp.cd(remotePath);
        } catch (SftpException e) {
            LOG.info("", e);
            return false;
        }
        try {
            Vector files = channelSftp.ls(remotePath);
            for (Iterator<ChannelSftp.LsEntry> iterator = files.iterator(); iterator.hasNext(); ) {
                ChannelSftp.LsEntry str = iterator.next();
                String filename = str.getFilename();
                if (filename.equals(".") || filename.equals("..")) {
                    continue;
                }
                SftpATTRS attrs = str.getAttrs();
                if (attrs.isDir()) {
                    deleteDir(remotePath + "/" + filename);
                } else {
                    channelSftp.rm(remotePath + "/" + filename);
                }
            }
            if (channelSftp.ls(remotePath).size() == 2) {
                channelSftp.rmdir(remotePath);
            }
        } catch (SftpException e) {
            LOG.error("", e);
            throw new RuntimeException("删除sftp路径失败，sftpPath=" + remotePath);
        }
        return true;
    }

    public boolean isFileExist(String sftpPath) {
        try {
            channelSftp.lstat(sftpPath);
            return true;
        } catch (SftpException e) {
            if (e.getMessage().contains(KEYWORD_FILE_NOT_EXISTS)) {
                return false;
            } else {
                throw new RuntimeException("Check file exists error", e);
            }
        }
    }

    @Override
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
                        LOG.error("sftp isExist error {}", e);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean renamePath(String oldPth, String newPath) {
        try {
            channelSftp.rename(oldPth, newPath);
        } catch (SftpException e) {
            LOG.error("renamePath {} to {} error", oldPth, newPath, e);
            return false;
        }
        return true;
    }

    @Override
    public Vector listFile(String remotePath) {
        Vector files = new Vector();
        try {
            files = channelSftp.ls(remotePath);
        } catch (SftpException e) {
            LOG.error("listFile  error", e);
        }

        return files;
    }

    @Override
    public void close() {
        sftpPool.returnObject(channelSftp);
    }

}
