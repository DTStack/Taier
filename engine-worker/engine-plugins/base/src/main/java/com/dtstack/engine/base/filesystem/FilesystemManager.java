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

package com.dtstack.engine.base.filesystem;

import com.dtstack.engine.base.filesystem.factory.IFileManageFactory;
import com.dtstack.engine.common.IFileManage;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Date: 2020/10/21
 * Company: www.dtstack.com
 * @author toutian
 */
public class FilesystemManager {

    private static final Logger LOG = LoggerFactory.getLogger(FilesystemManager.class);

    private List<IFileManage> fileManages;

    private FileConfig fileConfig;

    public FilesystemManager(FileConfig config) {
        this.fileManages = getFileManage(config);
    }

    public FilesystemManager(Configuration configuration, SftpConfig sftpConfig) {
        this.fileConfig = new FileConfig(configuration, sftpConfig);
        this.fileManages = getFileManage(fileConfig);
    }

    /**
     * 创建可用的的文件管理器
     * @param config
     * @return
     */
    private List<IFileManage> getFileManage(FileConfig config) {
        List<IFileManageFactory> fileManageFactory = FilesystemManageFactoryServiceLoader.findFileManageFactory(IFileManageFactory.class);

        List<IFileManage> fileManages = Lists.newArrayList();
        for (IFileManageFactory factory : fileManageFactory) {
            IFileManage fileManage = factory.createFileManage(config);
            if (null != fileManage) {
                fileManages.add(fileManage);
            }
        }
        return fileManages;
    }

    /**
     *   远程文件下载到本地文件夹，优先使用本地已有文件
     * @param remoteJarPath
     * @param localPath
     * @return
     * @throws FileNotFoundException
     */
    public File downloadFile(String remoteJarPath, String localPath) {
        return downloadFile(remoteJarPath, localPath, false);
    }

    /**
     *  使用文件管理器下载文件，返回本地文件
     * @param remoteJarPath  远程jar完整路径
     * @param localPath     本地临时文件夹/文件
     * @param alwaysPullNew   总是从远程下载最新文件
     * @return
     */
    public File downloadFile(String remoteFilePath, String localFilePath, boolean alwaysPullNew) {
        LOG.info("download file remoteFilePath:{} localFilePath:{} alwaysPullNew:{}", remoteFilePath, localFilePath, alwaysPullNew);

        File jarFile = new File(localFilePath);
        if (!alwaysPullNew) {
            if (jarFile.exists()) {
                return jarFile;
            }
        }

        //检查并创建本地文件目录
        if (!jarFile.getParentFile().exists()) {
            boolean mkdirs = jarFile.getParentFile().mkdirs();
            LOG.info("local file localParentFile {}  mkdir {} :", jarFile.getParent(), mkdirs);
        }

        for (IFileManage fileManage : fileManages) {
            if (fileManage.filterPrefix()) {
                String prefix = fileManage.getPrefix();
                remoteFilePath = remoteFilePath.startsWith(prefix) ? StringUtils.substringAfter(remoteFilePath, prefix) : remoteFilePath;
            }

            boolean downLoadSuccess = fileManage.downloadFile(remoteFilePath, localFilePath);
            if (downLoadSuccess) {
                LOG.info("download file success fileManage is :{}", fileManage.getClass().getSimpleName());
                break;
            }
        }

        return getLocalJarFile(localFilePath);
    }

    /**
     *  使用文件管理器下载文件夹内容
     * @return
     */
    public boolean downloadDir(String remoteDir, String localDir) {
        LOG.info("download dir remoteDir:{}, localDir:{}", remoteDir, localDir);

        //检查并创建本地文件目录
        File localDirPath = new File(localDir);
        if (!localDirPath.exists()) {
            boolean mkdirs = localDirPath.mkdirs();
            LOG.info("local file localDir {}  mkdir {} :", localDir, mkdirs);
        }

        boolean downLoadSuccess = false;
        for (IFileManage fileManage : fileManages) {
            if (fileManage.filterPrefix()) {
                String prefix = fileManage.getPrefix();
                remoteDir = remoteDir.startsWith(prefix) ? StringUtils.substringAfter(remoteDir, prefix) : remoteDir;
            }

            downLoadSuccess = fileManage.downloadDirManager(remoteDir, localDir);
            fileManage.close();
            if (downLoadSuccess) {
                LOG.info("download file success fileManage is :{}", fileManage.getClass().getSimpleName());
                break;
            }
        }
        return downLoadSuccess;
    }

    /**
     *  根据路径加载本地文件
     * @param localJarPath
     * @return
     */
    private File getLocalJarFile(String localJarPath) {
        File jarFile = new File(localJarPath);
        if (!jarFile.exists()) {
            throw new RuntimeException("File does not exist: " + localJarPath);
        } else if (!jarFile.isFile()) {
            throw new RuntimeException("File is not a file: " + localJarPath);
        }
        return jarFile;
    }

}
