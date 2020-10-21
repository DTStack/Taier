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
import com.dtstack.engine.base.filesystem.manager.IFileManage;
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

    private static final String URL_SPLIT = "/";

    private static String fileSP = File.separator;

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
            try {
                IFileManage fileManage = factory.createFileManage(config);
                if (null != fileManage) {
                    fileManages.add(fileManage);
                }
            } catch (Exception e) {
                LOG.error("createFileManage error ", e);
            }
        }
        return fileManages;
    }

    /**
     *   远程文件下载到本地文件夹，优先使用本地已有文件
     * @param fileManages
     * @param remoteJarPath
     * @param localPath
     * @return
     * @throws FileNotFoundException
     */
    public File downloadJar(String remoteJarPath, String localPath) throws FileNotFoundException {
        return downloadJar(remoteJarPath, localPath, true, false, false);
    }

    /**
     * 远程文件下载到本地文件夹/文件，优先使用本地已有文件
     * @param fileManages
     * @param remoteJarPath
     * @param localPath
     * @param isLocalDir
     * @return
     * @throws FileNotFoundException
     */
    public File downloadJar(String remoteJarPath, String localPath, boolean isLocalDir) throws FileNotFoundException {
        return downloadJar(remoteJarPath, localPath, isLocalDir, false, false);
    }

    /**
     * 远程文件下载到本地文件夹/文件，优先使用本地已有文件
     * @param fileManages
     * @param remoteJarPath
     * @param localPath
     * @param isLocalDir
     * @param alwaysPullNew
     * @return
     * @throws FileNotFoundException
     */
    public File downloadJar(String remoteJarPath, String localPath, boolean isLocalDir, boolean alwaysPullNew) throws FileNotFoundException {
        return downloadJar(remoteJarPath, localPath, isLocalDir, alwaysPullNew, false);
    }

    /**
     *  使用文件管理器下载文件，返回本地文件
     * @param fileManages
     * @param remoteJarPath  远程jar完整路径
     * @param localPath     本地临时文件夹/文件
     * @param isLocalDir    tmpFileDirPath是否为本地文件夹
     * @param alwaysPullNew   总是从远程下载最新文件
     * @param isEnd          文件下载后是否归还连接
     * @return
     */
    public File downloadJar(String remoteJarPath, String localPath, boolean isLocalDir, boolean alwaysPullNew, boolean isEnd) throws FileNotFoundException {
        LOG.info("download file remoteJarPath:{},localPath:{},isLocalDir:{},alwaysPullNew:{}", remoteJarPath, localPath, isLocalDir, alwaysPullNew);
        boolean downLoadSuccess = false;
        String localJarPath = isLocalDir ? getTmpFileName(remoteJarPath, localPath) : localPath;

        if (!alwaysPullNew) {
            File jarFile = new File(localJarPath);
            if (jarFile.exists()) {
                return jarFile;
            }
        }

        for (IFileManage fileManage : fileManages) {
            if (fileManage.filterPrefix()) {
                String prefix = fileManage.getPrefix();
                remoteJarPath = remoteJarPath.startsWith(prefix) ? StringUtils.substringAfter(remoteJarPath, prefix) : remoteJarPath;
            }

            downLoadSuccess = fileManage.downloadFile(remoteJarPath, localJarPath, isEnd);
            if (downLoadSuccess) {
                LOG.info("download file success fileManage is :{}", fileManage.getClass().getSimpleName());
                break;
            }
        }

        return getLocalJarFile(localJarPath);
    }

    /**
     *  使用文件管理器下载文件夹内容
     * @return
     */
    public boolean downloadDir(String remoteDir, String localDir) {
        LOG.info("download dir remoteDir:{}, localDir:{},alwaysPullNew:{}", remoteDir, localDir);

        boolean downLoadSuccess = false;
        for (IFileManage fileManage : fileManages) {
            if (fileManage.filterPrefix()) {
                String prefix = fileManage.getPrefix();
                remoteDir = remoteDir.startsWith(prefix) ? StringUtils.substringAfter(remoteDir, prefix) : remoteDir;
            }

            downLoadSuccess = fileManage.downloadDir(remoteDir, localDir);
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
    public File getLocalJarFile(String localJarPath) throws FileNotFoundException {
        File jarFile = new File(localJarPath);
        if (!jarFile.exists()) {
            throw new FileNotFoundException("JAR file does not exist: " + localJarPath);
        } else if (!jarFile.isFile()) {
            throw new FileNotFoundException("JAR file is not a file: " + localJarPath);
        }
        return jarFile;
    }


    /**
     * 根据远程文件路径，构建本地临时文件名
     * @param fileUrl
     * @param toPath
     * @return
     */
    public String getTmpFileName(String fileUrl, String toPath) {
        String name = fileUrl.substring(fileUrl.lastIndexOf(URL_SPLIT) + 1);
        String tmpFileName = toPath + fileSP + name;
        return tmpFileName;
    }

}
