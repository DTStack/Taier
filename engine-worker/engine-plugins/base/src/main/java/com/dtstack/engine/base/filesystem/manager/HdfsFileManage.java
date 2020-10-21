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

import com.google.common.io.Files;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 2020/7/20
 * Company: www.dtstack.com
 * @author maqi
 */
public class HdfsFileManage implements IFileManage {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HdfsFileManage.class);

    public static final String PREFIX = "hdfs://";

    private static final String HDFS_PATTERN = "(hdfs://[^/]+)(.*)";

    private static String fileSP = File.separator;

    private static Pattern pattern = Pattern.compile(HDFS_PATTERN);

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
    public boolean filterPrefix() {
        return false;
    }

    @Override
    public boolean downloadFile(String remotePath, String localPath, boolean isEnd) {
        Pair<String, String> pair = parseHdfsUri(remotePath);
        if (pair == null) {
            LOG.info("can't parse hdfs url from given uriStr:{}", remotePath);
            return false;
        }

        String hdfsUri = pair.getLeft();
        String hdfsFilePathStr = pair.getRight();
        try (FileSystem fs = FileSystem.get(new URI(hdfsUri), hadoopConf)) {
            Path hdfsFilePath = new Path(hdfsFilePathStr);
            if (!fs.exists(hdfsFilePath)) {
                return false;
            }


            File file = new File(localPath);
            if(!file.getParentFile().exists()){
                Files.createParentDirs(file);
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
    public boolean downloadDir(String remotePath, String localPath) {
        try (FileSystem fs = FileSystem.get(hadoopConf)) {
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
                String localDstFileName = localPath + fileSP + fileName;
                downloadFile(subPath, localDstFileName, true);
            }
            return true;
        } catch (Exception e) {
            LOG.error("downloadDir from hdfs error:", e);
            clearDownloadFile(localPath);
            return false;
        }
    }

    @Override
    public boolean uploadFile(String remotePath, String localPath, boolean isEnd) {
        return false;
    }

    @Override
    public boolean uploadFile(String remotePath, String localPath, String fileName, boolean isEnd) {
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


    private static Pair<String, String> parseHdfsUri(String path){
        Matcher matcher = pattern.matcher(path);
        if(matcher.find() && matcher.groupCount() == 2){
            String hdfsUri = matcher.group(1);
            String hdfsPath = matcher.group(2);
            return new MutablePair<>(hdfsUri, hdfsPath);
        }else{
            return null;
        }
    }

    @Override
    public void close() {

    }

}
