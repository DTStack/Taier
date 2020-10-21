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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Date: 2020/7/23
 * Company: www.dtstack.com
 * @author maqi
 */
public class HttpFileManage implements IFileManage {
    private static final Logger LOG = LoggerFactory.getLogger(HttpFileManage.class);

    public static final String PREFIX = "http://";

    private static final int BUFFER_SIZE = 10240;

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
        File outFile = new File(localPath);
        try {
            //如果当前文件存在则删除,覆盖最新的文件
            if (outFile.exists()) {
                outFile.delete();
            }

            Files.createParentDirs(outFile);//如果父目录不存在则创建
            outFile.createNewFile();

            FileOutputStream fout = new FileOutputStream(outFile);
            URL url = new URL(remotePath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            BufferedInputStream bfInputStream = new BufferedInputStream(httpURLConnection.getInputStream());

            byte[] buf = new byte[BUFFER_SIZE];
            int readSize = -1;
            while ((readSize = bfInputStream.read(buf)) != -1) {
                fout.write(buf, 0, readSize);
            }

            //释放资源
            fout.close();
            bfInputStream.close();
            httpURLConnection.disconnect();
            LOG.info("download from remote url:{} success,dest file name is {}.", remotePath, localPath);
        } catch (IOException e) {
            LOG.error("download from remote url:" + remotePath + "failure.", e);
            if (outFile.exists()) {
                outFile.delete();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean downloadDir(String remotePath, String localPath) {
        return false;
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
