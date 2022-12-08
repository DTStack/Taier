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

package com.dtstack.taier.pluginapi.sftp;

import com.dtstack.taier.pluginapi.IFileManage;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date: 2020/7/20
 * Company: www.dtstack.com
 * <p>
 *
 * @author maqi
 */
public class SftpFileManage implements IFileManage {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SftpFileManage.class);

    public static final String PREFIX = "sftp://";

    private static final String KEYWORD_FILE_NOT_EXISTS = "No such file";

    private Map<String, SftpPool> sftpPoolMap = Maps.newConcurrentMap();
    private Map<String, Long> fileLastModifyMap = Maps.newConcurrentMap();
    private static Map<SftpConfig, SftpFileManage> sftpMap = new ConcurrentHashMap<>();


    private SftpFactory sftpFactory;
    private SftpPool sftpPool;
    private SftpConfig sftpConfig;

    public static SftpFileManage getSftpManager(SftpConfig sftpConfig) {
        return sftpMap.computeIfAbsent(sftpConfig, k -> new SftpFileManage(sftpConfig));
    }

    public SftpFileManage retrieveSftpManager(SftpConfig sftpConfig) {
        return sftpMap.computeIfAbsent(sftpConfig, k -> new SftpFileManage(sftpConfig));
    }

    private static class SingletonHolder {
        private static SftpFileManage singleton = new SftpFileManage();
    }

    private SftpFileManage() {
    }

    public static SftpFileManage getInstance() {
        return SingletonHolder.singleton;
    }

    private SftpFileManage(SftpConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
        checkConfig(sftpConfig);
        boolean isUsePool = sftpConfig.getIsUsePool();

        if (isUsePool) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("get channelSftp from SftpPool!");
            }
            this.sftpPool = getSftpPool(sftpConfig);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("get channelSftp from native!");
            }
            this.sftpFactory = new SftpFactory(sftpConfig);
        }
    }

    public ChannelSftp getChannelSftp() {
        ChannelSftp channelSftp = null;
        boolean isUsePool = sftpConfig.getIsUsePool();
        if (isUsePool) {
            channelSftp = sftpPool.borrowObject();
        } else {
            channelSftp = sftpFactory.create();
        }
        setSessionTimeout(sftpConfig, channelSftp);
        return channelSftp;
    }

    private SftpPool getSftpPool(SftpConfig sftpConfig) {
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
                    LOGGER.error("channelSftpTest获取Session异常", e);
                }
                SftpPoolConfig sftpPoolConfig = new SftpPoolConfig(sftpConfig.getMaxTotal(), sftpConfig.getMaxIdle(), sftpConfig.getMinIdle());
                sftpPoolConfig.setMaxWaitMillis(sftpConfig.getMaxWaitMillis()); //从idle队列里面取对象时，阻塞时最大等待时长
                sftpPoolConfig.setMinEvictableIdleTimeMillis(sftpConfig.getMinEvictableIdleTimeMillis()); //处于idle状态超过此值时，会被destory
                sftpPoolConfig.setSoftMinEvictableIdleTimeMillis(sftpConfig.getSoftMinEvictableIdleTimeMillis()); //处于idle状态超过此值时，会被destory, 保留minIdle个空闲连接数。默认为-1
                sftpPoolConfig.setTimeBetweenEvictionRunsMillis(sftpConfig.getTimeBetweenEvictionRunsMillis()); //evict线程每次间隔时间
                sftpPoolConfig.setTestOnBorrow(true);
                sftpPool1 = new SftpPool(sftpFactory, sftpPoolConfig);
            } else {
                LOGGER.info("SFTPHandler connect fail, host:{} username:{} .", sftpConfig.getHost(), sftpConfig.getUsername());
            }
            return sftpPool1;
        });
        return sftpPool;

    }

    private String getSftpPoolKey(SftpConfig sftpConfig) {
        return sftpConfig.getHost() + sftpConfig.getPort() + sftpConfig.getUsername() + sftpConfig.getPath();
    }

    private void checkConfig(SftpConfig sftpConfig) {
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

    private void setSessionTimeout(SftpConfig sftpConfig, ChannelSftp channelSftp) {
        Session sessionSftp;
        try {
            sessionSftp = channelSftp.getSession();
            sessionSftp.setTimeout(sftpConfig.getTimeout());
        } catch (JSchException e) {
            LOGGER.error("获取sessionSftp异常", e);
            throw new RuntimeException("获取sessionSftp异常, 请检查sessionSftp是否正常", e);
        }
    }

    public String cacheOverloadFile(String fileName, String remoteDir, String localDir) {
        String remoteFile = remoteDir + File.separator + fileName;
        String localFile = localDir + File.separator + fileName;

        Long lastModifyTime;
        long fileTimeout;
        if ((fileTimeout = sftpConfig.getFileTimeout()) != 0L && (lastModifyTime = fileLastModifyMap.get(localFile)) != null) {
            if (System.currentTimeMillis() - lastModifyTime <= fileTimeout) {
                File file = new File(localFile);
                if (file.exists()) {
                    return localFile;
                }

            }
        }
        try {
            downloadFile(remoteFile, localFile);
            fileLastModifyMap.put(localFile, new File(localFile).lastModified());
            return localFile;
        } catch (Exception e) {
            LOGGER.error("load file error: ", e);
            return null;
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
        ChannelSftp channelSftp = getChannelSftp();
        try {
            //检查并创建本地文件目录
            File localPathFile = new File(localPath);
            if (!localPathFile.getParentFile().exists()) {
                boolean mkdirs = localPathFile.getParentFile().mkdirs();
                LOGGER.info("local file localParentFile {}  mkdir {} :", localPathFile.getParent(), mkdirs);
            }

            if (!isFileExist(channelSftp, remotePath)) {
                LOGGER.info("File not exist on sftp:" + remotePath);
                return false;
            }
            return downloadFile(remotePath, localPath, channelSftp);
        } finally {
            close(channelSftp);
        }
    }

    private boolean downloadFile(String remotePath, String localPath, ChannelSftp channelSftp) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(localPath));
            channelSftp.get(remotePath, os);
            return true;
        } catch (Exception e) {
            LOGGER.error("download file from sftp error:", e);
            return false;
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    /**
     * download 后自动释放连接
     *
     * @param remoteDir
     * @param localDir
     * @return
     */
    public boolean downloadDirManager(String remoteDir, String localDir) {
        try {
            return downloadDir(remoteDir, localDir);
        } catch (Throwable e) {
            LOGGER.error("sftp downloadDir error {}", e);
        }
        return Boolean.FALSE;
    }

    /**
     * download 后自动释放连接
     *
     * @param remoteDir
     * @param localDir
     * @return
     */
    public boolean downloadDir(String remoteDir, String localDir) {
        ChannelSftp channelSftp = null;
        try {
            //检查并创建本地文件目录
            File localDirPath = new File(localDir);
            if (!localDirPath.exists()) {
                boolean mkdirs = localDirPath.mkdirs();
                LOGGER.info("local file localDir {}  mkdir {} :", localDir, mkdirs);
            }

            channelSftp = getChannelSftp();
            Vector files = channelSftp.ls(remoteDir);
            if (files == null) {
                return false;
            }
            // 递归下载文件
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
                        LOGGER.info("local file path mkdir :", localFilePath);
                        dir2.mkdir();
                    }
                    downloadDir(ftpFilePath, localFilePath);
                } else {
                    downloadFile(ftpFilePath, localFilePath, channelSftp);
                }
            }

            return true;
        } catch (Exception e) {
            LOGGER.error("sftp downloadDir error {}", e);
            throw new PluginDefineException(e);
        } finally {
            close(channelSftp);
        }
    }

    public boolean uploadFile(String remotePath, String localPath) {
        File file = new File(localPath);
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("该路径不存在或不是文件路径");
        }

        return uploadFile(remotePath, file.getParent(), file.getName());
    }


    public boolean uploadFile(String remotePath, String localPath, String fileName) {
        LOGGER.info("path：localPath=" + localPath);
        ChannelSftp channelSftp = null;
        try {
            //检查路径
            if (!this.mkdir(remotePath)) {
                LOGGER.error("create sftp path error:" + remotePath);
                return false;
            }
            channelSftp = getChannelSftp();
            String dst = remotePath + "/" + fileName;
            String src = localPath + "/" + fileName;
            LOGGER.info("begin upload local path：[" + src + "]  target path：[" + dst + "]");
            channelSftp.put(src, dst);
            LOGGER.info("upload success");
            return true;
        } catch (Exception e) {
            LOGGER.error("upload fail", e);
            return false;
        } finally {
            close(channelSftp);
        }
    }

    public boolean uploadDir(String remotePath, String localPath) {
        File file = new File(localPath);
        if (file.isDirectory()) {
            remotePath += "/" + file.getName();
            if (!mkdir(remotePath)) {
                LOGGER.error("创建sftp服务器路径失败:" + remotePath);
                return false;
            }
            File[] files = file.listFiles();
            for (File file1 : files) {
                uploadDir(remotePath, file1.getPath());
            }
            return true;
        } else {
            return uploadFile(remotePath, file.getParent(), file.getName());
        }
    }

    public boolean deleteFile(String remotePath) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = getChannelSftp();
            if (this.isFileExist(channelSftp, remotePath)) {
                channelSftp.rm(remotePath);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("删除失败:{}", remotePath, e);
            return false;
        } finally {
            close(channelSftp);
        }
    }

    public boolean deleteDir(String remotePath) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = getChannelSftp();
            channelSftp.cd(remotePath);
        } catch (SftpException e) {
            LOGGER.info("", e);
            //释放连接
            close(channelSftp);
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
            return true;
        } catch (SftpException e) {
            LOGGER.error("", e);
            throw new RuntimeException("删除sftp路径失败，sftpPath=" + remotePath);
        } finally {
            close(channelSftp);
        }
    }

    public boolean isFileExist(ChannelSftp channelSftp, String sftpPath) {
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

    public boolean mkdir(String path) {
        String[] split = path.split("/");
        StringBuilder currPath = new StringBuilder();
        ChannelSftp channelSftp = null;
        try {
            channelSftp = getChannelSftp();
            for (String dir : split) {
                currPath.append("/").append(dir).toString();
                try {
                    channelSftp.cd(currPath.toString());
                } catch (SftpException sException) {
                    if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
                        try {
                            channelSftp.mkdir(currPath.toString());
                        } catch (SftpException e) {
                            LOGGER.error("sftp isExist error {}", e);
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            close(channelSftp);
        }

        return true;
    }

    public boolean renamePath(String oldPth, String newPath) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = getChannelSftp();
            channelSftp.rename(oldPth, newPath);
        } catch (SftpException e) {
            LOGGER.error("renamePath {} to {} error", oldPth, newPath, e);
            return false;
        } finally {
            close(channelSftp);
        }
        return true;
    }

    public Vector listFile(String remotePath) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = getChannelSftp();
            Vector vector = channelSftp.ls(remotePath);
            return vector;
        } catch (SftpException e) {
            LOGGER.error("listFile  error", e);
            throw new PluginDefineException(e);
        } finally {
            close(channelSftp);
        }
    }

    public void close() {
    }

    public void close(ChannelSftp channelSftp) {
        if (null == channelSftp) {
            return;
        }
        if (null != sftpPool) {
            sftpPool.returnObject(channelSftp);
        } else {
            try {
                channelSftp.disconnect();
                channelSftp.getSession().disconnect();
            } catch (Exception e) {
                LOGGER.error("close channelSftp error:", e);
            }
        }
    }

}
