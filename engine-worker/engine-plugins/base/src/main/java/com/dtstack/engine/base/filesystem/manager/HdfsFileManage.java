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

import com.dtstack.engine.common.IFileManage;
import com.google.common.io.Files;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Vector;

/**
 * Date: 2020/7/20
 * Company: www.dtstack.com
 * @author maqi
 */
public class HdfsFileManage implements IFileManage {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HdfsFileManage.class);

    private static final String PREFIX = "hdfs://";

    private static final Integer BUFFER_SIZE = 2048;

    private final Configuration hadoopConf;

    public HdfsFileManage(Configuration hadoopConf) {
        this.hadoopConf = hadoopConf;
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public boolean canHandle(String remotePath) {
        return remotePath.contains(PREFIX);
    }

    @Override
    public boolean downloadFile(String remotePath, String localPath) {

        try (FileSystem fs = FileSystem.get(hadoopConf)) {

            //检查并创建本地文件目录
            File file = new File(localPath);
            if(!file.getParentFile().exists()){
                Files.createParentDirs(file);
            }

            Path hdfsFilePath = new Path(remotePath);
            if (!fs.exists(hdfsFilePath)) {
                return false;
            }

            //读取文件
            InputStream is = fs.open(hdfsFilePath);
            //保存到本地
            IOUtils.copyBytes(is, new FileOutputStream(file), BUFFER_SIZE, true);
            return true;
        } catch (Exception e) {
            LOG.error("downloadFile from hdfs error:", e);
            return false;
        }
    }

    @Override
    public boolean downloadDir(String remotePath, String localDir) {
        try (FileSystem fs = FileSystem.get(hadoopConf)) {
            //检查并创建本地文件目录
            File localDirPath = new File(localDir);
            if (!localDirPath.exists()) {
                boolean mkdirs = localDirPath.mkdirs();
                LOG.info("local file localDir {}  mkdir {} :", localDir, mkdirs);
            }

            Path path = new Path(remotePath);
            if (!fs.exists(path)) {
                LOG.info("hdfs not exists" + path);
                return false;
            }

            if (!fs.isDirectory(path)) {
                LOG.info("transfer must be directory");
                return false;
            }

            FileStatus[] statusArr = fs.listStatus(path);
            for (FileStatus status : statusArr) {
                String subPath = status.getPath().toString();
                String fileName = status.getPath().getName();
                String localDstFileName = localDir + File.separator + fileName;
                downloadFile(subPath, localDstFileName);
            }
            return true;
        } catch (Exception e) {
            LOG.error("downloadDir from hdfs error:", e);
//            clearDownloadFile(localDir);
            return false;
        }
    }

    @Override
    public boolean downloadDirManager(String remotePath, String localPath) {
        try {
            return downloadDir(remotePath,localPath);
        } catch (Exception e) {
            LOG.error("downloadDir from hdfs error:", e);
        }
        return false;
    }

    @Override
    public boolean uploadFile(String remotePath, String localPath) {
        return false;
    }

    @Override
    public boolean uploadFile(String remotePath, String localPath, String fileName) {
        return false;
    }

    @Override
    public boolean uploadDir(String remotePath, String localPath) {
        return false;
    }

    @Override
    public boolean deleteFile(String remotePath) {
        return true;
    }

    @Override
    public boolean deleteDir(String remotePath) {
        return true;
    }

    @Override
    public boolean mkdir(String path) {
        return false;
    }

    @Override
    public boolean renamePath(String oldPth, String newPath) {
        return false;
    }

    @Override
    public Vector listFile(String remotePath) {
        return null;
    }

    @Override
    public void close() {

    }

}
