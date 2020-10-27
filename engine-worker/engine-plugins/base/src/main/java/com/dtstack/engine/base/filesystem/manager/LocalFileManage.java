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

import java.io.File;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *  本地文件存在性检查
 * Date: 2020/7/23
 * Company: www.dtstack.com
 * @author maqi
 */
public class LocalFileManage implements IFileManage {
    private static final Logger LOG = LoggerFactory.getLogger(HttpFileManage.class);

    public static final String PREFIX = "local://";

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public boolean canHandle(String remotePath) {
        return remotePath.startsWith(PREFIX);
    }

    public boolean downloadFile(String remotePath, String localPath) {
        return downloadFile(remotePath, localPath, true);
    }


    @Override
    public boolean downloadFile(String remotePath, String localPath, boolean isEnd) {
        LOG.info("LocalFileManage local file exist check localPath : {}", localPath);

        File jarFile = new File(localPath);
        if (!jarFile.exists()) {
            LOG.info("JAR file does not exist localPath :{} ", localPath);
            return false;
        } else if (!jarFile.isFile()) {
            LOG.info("JAR file does not a file localPath :{} ", localPath);
            return false;
        }

        return true;
    }

    @Override
    public boolean downloadDir(String remotePath, String localPath) {
        return false;
    }

    @Override
    public boolean uploadFile(String remotePath, String localPath) {
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
        return false;
    }

    @Override
    public boolean deleteDir(String remotePath) {
        return false;
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
