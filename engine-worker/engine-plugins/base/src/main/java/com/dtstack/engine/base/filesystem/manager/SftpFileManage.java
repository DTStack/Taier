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

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFactory;
import com.dtstack.engine.common.sftp.SftpPool;
import com.dtstack.engine.common.sftp.SftpPoolConfig;
import com.dtstack.engine.common.util.SFTPHandler;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Date: 2020/7/20
 * Company: www.dtstack.com
 *
 * 参考 @com.dtstack.engine.common.util.SFTPHandler
 * @author maqi
 */
public class SftpFileManage implements IFileManage {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SftpFileManage.class);

    public static final String PREFIX = "sftp://";

    private static final String KEYWORD_FILE_NOT_EXISTS = "No such file";

    private static Map<String, SftpPool> sftpPoolMap = Maps.newConcurrentMap();

    private ChannelSftp channelSftp;
    private SftpPool sftpPool;
    private SftpConfig sftpConfig;


    private SftpFileManage(ChannelSftp channelSftp, SftpPool sftpPool, SftpConfig sftpConfig) {
        this.channelSftp = channelSftp;
        this.sftpPool = sftpPool;
        this.sftpConfig = sftpConfig;
    }

    public static SftpFileManage getInstance(SftpConfig sftpConfig) {
        checkConfig(sftpConfig);

        boolean isUsePool = sftpConfig.getIsUsePool();

        ChannelSftp channelSftp = null;
       SftpPool sftpPool = null;
        if (isUsePool) {
            LOG.info("get channelSftp from SftpPool!");
            sftpPool = getSftpPool(sftpConfig);
            channelSftp = sftpPool.borrowObject();
        } else {
            LOG.info("get channelSftp from native!");
           SftpFactory sftpFactory = new SftpFactory(sftpConfig);
            channelSftp = sftpFactory.create();
        }

        setSessionTimeout(sftpConfig, channelSftp);
        return new SftpFileManage(channelSftp, sftpPool, sftpConfig);
    }

    private static SftpPool getSftpPool(SftpConfig sftpConfig) {
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
                SftpPoolConfig sftpPoolConfig = new SftpPoolConfig(sftpConfig.getMaxTotal(), sftpConfig.getMaxIdle(), sftpConfig.getMinIdle());
                sftpPoolConfig.setMaxWaitMillis(sftpConfig.getMaxWaitMillis()); //从idle队列里面取对象时，阻塞时最大等待时长
                sftpPoolConfig.setMinEvictableIdleTimeMillis(sftpConfig.getMinEvictableIdleTimeMillis()); //处于idle状态超过此值时，会被destory
                sftpPoolConfig.setSoftMinEvictableIdleTimeMillis(sftpConfig.getSoftMinEvictableIdleTimeMillis()); //处于idle状态超过此值时，会被destory, 保留minIdle个空闲连接数。默认为-1
                sftpPoolConfig.setTimeBetweenEvictionRunsMillis(sftpConfig.getTimeBetweenEvictionRunsMillis()); //evict线程每次间隔时间
                sftpPool1 = new SftpPool(sftpFactory, sftpPoolConfig);
            } else {
                LOG.info("SFTPHandler连接sftp失败, host:{} username:{} .", sftpConfig.getHost(), sftpConfig.getUsername());
            }
            return sftpPool1;
        });
        return sftpPool;

    }

    private static String getSftpPoolKey(SftpConfig sftpConfig) {
        return sftpConfig.getHost() + sftpConfig.getPort() + sftpConfig.getUsername() + sftpConfig.getPath();
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

    private static void setSessionTimeout(SftpConfig sftpConfig, ChannelSftp channelSftp) {
        Session sessionSftp;
        try {
            sessionSftp = channelSftp.getSession();
            sessionSftp.setTimeout(sftpConfig.getTimeout());
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

        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(localPath));
            channelSftp.get(remotePath, os);
            return true;
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
    }

    /**
     * download 后自动释放连接
     * @param remoteDir
     * @param localDir
     * @return
     */
    @Override
    public boolean downloadDir(String remoteDir, String localDir) {
        try {
            File localDirPath = new File(localDir);
            if (!localDirPath.exists()) {
                boolean mkdirs = localDirPath.mkdirs();
                LOG.info("local file path {}  mkdir {} :", localDir, mkdirs);
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
                    downloadFile(ftpFilePath, localFilePath, true);
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
        File file = new File(localPath);
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("该路径不存在或不是文件路径");
        }

        return uploadFile(remotePath, file.getParent(), file.getName(), isEnd);
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
            String src = localPath + "/" + fileName;
            LOG.info("开始上传，本地服务器路径：[" + src + "]目标服务器路径：[" + dst + "]");
            channelSftp.put(src, dst);
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
            remotePath += "/" + file.getName();
            if (!mkdir(remotePath)) {
                LOG.error("创建sftp服务器路径失败:" + remotePath);
                return false;
            }
            File[] files = file.listFiles();
            for (File file1 : files) {
                uploadDir(remotePath, file1.getPath());
            }
            return true;
        } else {
            return uploadFile(remotePath, file.getParent(), file.getName(), true);
        }
    }

    @Override
    public boolean deleteFile(String remotePath) {
        if (this.isFileExist(remotePath)) {
            try {
                channelSftp.rm(remotePath);
                return true;
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
            return true;
        } catch (SftpException e) {
            LOG.error("", e);
            throw new RuntimeException("删除sftp路径失败，sftpPath=" + remotePath);
        }
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
        try {
            return channelSftp.ls(remotePath);
        } catch (SftpException e) {
            LOG.error("listFile  error", e);
            throw new RdosDefineException(e);
        }
    }

    @Override
    public void close() {
        if (sftpPool != null) {
            sftpPool.returnObject(channelSftp);
        } else {
            try {
                channelSftp.disconnect();
                channelSftp.getSession().disconnect();
            } catch (Exception e) {
                LOG.error("close channelSftp error: {}", e.getMessage());
            }
        }
    }

}
